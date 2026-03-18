import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"

const FINNHUB_API_KEY = Deno.env.get('FINNHUB_API_KEY')
const GEMINI_API_KEY = Deno.env.get('GEMINI_API_KEY')
const SUPABASE_URL = Deno.env.get('SUPABASE_URL')
const SUPABASE_SERVICE_ROLE_KEY = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY')

serve(async (req) => {
  try {
    const supabase = createClient(SUPABASE_URL!, SUPABASE_SERVICE_ROLE_KEY!)

    // fetch alert rules
    const { data: rules, error: rulesError } = await supabase
      .from('alert_rules')
      .select('*')
      .eq('enabled', true)

    if (rulesError) throw rulesError

    const uniqueTickers = [...new Set(rules.map(r => r.ticker_key))]

    for (const ticker of uniqueTickers) {
      const quoteResponse = await fetch(`https://finnhub.io/api/v1/quote?symbol=${ticker}&token=${FINNHUB_API_KEY}`)
      const quote = await quoteResponse.json()
      const percentChange = quote.dp

      // check against rules for each ticker
      // also check that the percent change is > threshold
      const matchingRules = rules.filter(r => r.ticker_key === ticker && Math.abs(percentChange) >= r.threshold)

      if (matchingRules.length > 0) {
        console.log(`Processing event for ${ticker}: ${percentChange}% change`)
        const newsRes = await fetch(`https://finnhub.io/api/v1/company-news?symbol=${ticker}&from=2024-01-01&to=2025-12-31&token=${FINNHUB_API_KEY}`)
        const news = (await newsRes.json()).slice(0, 5) // Top 5 headlines

        // prompt gemini for ai analysis
        const prompt = `Analyze why ${ticker} moved ${percentChange}% today.
          Latest news: ${news.map((n: any) => n.headline).join('; ')}.
          Return a JSON object ONLY with this structure:
          { "summary": "string", "sentiment": "BULLISH|BEARISH", "confidence": 0.0-1.0,
            "causes": [{ "title": "string", "rationale": "string", "relevance": 0.0-1.0 }] }`

        const geminiRes = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${GEMINI_API_KEY}`, {
          method: 'POST',
          body: JSON.stringify({
            contents: [{ parts: [{ text: prompt }] }],
            generationConfig: { response_mime_type: "application/json" }
          })
        })
        const geminiData = await geminiRes.json()
        const analysis = JSON.parse(geminiData.candidates[0].content.parts[0].text)

        // write all data to db
        const { data: event, error: eventErr } = await supabase.from('market_events').insert({
          ticker_key: ticker,
          event_type: percentChange > 0 ? 'PRICE_SPIKE_UP' : 'PRICE_SPIKE_DOWN',
          percent_move: percentChange,
          start_time: new Date().toISOString(),
          price_before: quote.pc,
          price_after: quote.c,
          brief_description: analysis.summary
        }).select().single()

        if (event) {
          // write AI explanation to db
          await supabase.from('ai_explanations').insert({
            event_id: event.id,
            summary: analysis.summary,
            bullets: analysis.causes.map((c: any) => c.title),
            sentiment: analysis.sentiment,
            confidence: analysis.confidence
          })

          // write cause of events to db
          for (const cause of analysis.causes) {
            await supabase.from('event_causes').insert({
              event_id: event.id,
              title: cause.title,
              rationale: cause.rationale,
              relevance_score: cause.relevance,
              rank: analysis.causes.indexOf(cause) + 1
            })
          }

          // Link triggered rules to this event for notifications
          for (const rule of matchingRules) {
            await supabase.from('alert_notifications').insert({
              alert_rule_id: rule.id,
              event_id: event.id
            })
          }
        }
      }
    }

    return new Response(JSON.stringify({ message: "Processed" }), {
      headers: { "Content-Type": "application/json" },
      status: 200,
    })
  } catch (error) {
    console.error(error)
    return new Response(JSON.stringify({ error: error.message }), {
      headers: { "Content-Type": "application/json" },
      status: 500,
    })
  }
})
