import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"

const FINNHUB_API_KEY = Deno.env.get('FINNHUB_API_KEY')
const GEMINI_API_KEY = Deno.env.get('GEMINI_API_KEY')
const SUPABASE_URL = Deno.env.get('SUPABASE_URL')
const SUPABASE_SERVICE_ROLE_KEY = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY')

const STALE_THRESHOLD_HOURS = 24

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response('ok', { headers: corsHeaders })
  }

  try {
    const { ticker, eventId } = await req.json()

    const supabase = createClient(SUPABASE_URL!, SUPABASE_SERVICE_ROLE_KEY!)

    // If eventId is provided, we are generating an AI Explanation for a specific Market Event
    if (eventId) {
      console.log(`Generating explanation for event: ${eventId}`)

      // 1. Fetch event details
      const { data: event, error: eventErr } = await supabase
        .from('market_events')
        .select('*')
        .eq('id', eventId)
        .single()

      if (eventErr || !event) throw new Error('Event not found')

      // 2. Fetch associated news causes
      const { data: causes, error: causesErr } = await supabase
        .from('event_causes')
        .select('*')
        .eq('event_id', eventId)
        .order('rank', { ascending: true })

      const causesContext = (causes || []).map(c => `- ${c.title}: ${c.rationale}`).join('\n')

      const prompt = `You are a financial analyst explaining a specific market event.

Ticker: ${event.ticker_key}
Event: ${event.event_type} (${event.percent_move}%)
Description: ${event.brief_description}

Key Causes Identified:
${causesContext}

Provide a detailed explanation of this event.
Return a JSON object ONLY with this exact structure:
{
  "summary": "2-3 sentence overview of the event",
  "bullets": ["Key point 1", "Key point 2", "Key point 3"],
  "sentiment": "BULLISH|BEARISH|NEUTRAL",
  "confidence": 0.95
}`

      const geminiRes = await fetch(
        `https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${GEMINI_API_KEY}`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            contents: [{ parts: [{ text: prompt }] }],
            generationConfig: { response_mime_type: 'application/json' },
          }),
        }
      )

      const geminiData = await geminiRes.json()
      const rawText = geminiData?.candidates?.[0]?.content?.parts?.[0]?.text
      if (!rawText) throw new Error('Empty response from Gemini')
      const explanation = JSON.parse(rawText)

      const { data: saved, error: saveError } = await supabase
        .from('ai_explanations')
        .upsert({
          event_id: eventId,
          summary: explanation.summary,
          bullets: explanation.bullets,
          sentiment: explanation.sentiment,
          confidence: explanation.confidence,
          generated_at: new Date().toISOString(),
        })
        .select()
        .single()

      if (saveError) throw saveError
      return new Response(JSON.stringify(saved), { headers: { ...corsHeaders, 'Content-Type': 'application/json' } })
    }

    // Otherwise, generate a general Stock Analysis
    if (!ticker) {
      return new Response(JSON.stringify({ error: 'ticker or eventId is required' }), {
        headers: { ...corsHeaders, 'Content-Type': 'application/json' },
        status: 400,
      })
    }

    // Check for a fresh analysis (generated within STALE_THRESHOLD_HOURS)
    const staleThreshold = new Date(Date.now() - STALE_THRESHOLD_HOURS * 3600 * 1000).toISOString()
    const { data: existing } = await supabase
      .from('stock_analyses')
      .select('*')
      .eq('ticker_key', ticker)
      .gt('generated_at', staleThreshold)
      .order('generated_at', { ascending: false })
      .limit(1)
      .maybeSingle()

    if (existing) {
      return new Response(JSON.stringify(existing), {
        headers: { ...corsHeaders, 'Content-Type': 'application/json' },
        status: 200,
      })
    }

    // Fetch recent news and current quote from Finnhub
    const today = new Date().toISOString().split('T')[0]
    const weekAgo = new Date(Date.now() - 7 * 86_400_000).toISOString().split('T')[0]

    const [newsRes, quoteRes, profileRes] = await Promise.all([
      fetch(`https://finnhub.io/api/v1/company-news?symbol=${ticker}&from=${weekAgo}&to=${today}&token=${FINNHUB_API_KEY}`),
      fetch(`https://finnhub.io/api/v1/quote?symbol=${ticker}&token=${FINNHUB_API_KEY}`),
      fetch(`https://finnhub.io/api/v1/stock/profile2?symbol=${ticker}&token=${FINNHUB_API_KEY}`),
    ])

    const newsArticles: any[] = (await newsRes.json()).slice(0, 10)
    const quote: any = await quoteRes.json()
    const profile: any = await profileRes.json()

    const companyName = profile?.name ?? ticker
    const currentPrice = quote?.c ?? 0
    const percentChange = quote?.dp ?? 0

    const newsContext = newsArticles.length > 0
      ? newsArticles.map((n, i) => `${i + 1}. ${n.headline}`).join('\n')
      : 'No recent news available.'

    const prompt = `You are a financial analyst providing a daily stock overview for retail investors.

Company: ${companyName} (${ticker})
Current price: $${currentPrice.toFixed(2)}
Today's change: ${percentChange.toFixed(2)}%

Recent news headlines (last 7 days):
${newsContext}

Write a concise 2–3 paragraph analysis covering:
1. A brief overview of what ${companyName} does and its market position
2. Recent price action and performance context
3. Key themes from the news that investors should monitor

Return a JSON object ONLY with this exact structure:
{"summary":"<full analysis text>","sentiment":"BULLISH|BEARISH|NEUTRAL|MIXED","confidence":<0.0 to 1.0>}`

    const geminiRes = await fetch(
      `https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${GEMINI_API_KEY}`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          contents: [{ parts: [{ text: prompt }] }],
          generationConfig: { response_mime_type: 'application/json' },
        }),
      }
    )

    const geminiData = await geminiRes.json()
    const rawText = geminiData?.candidates?.[0]?.content?.parts?.[0]?.text
    if (!rawText) throw new Error('Empty response from Gemini')

    const analysis = JSON.parse(rawText)

    // Upsert: delete stale entry then insert fresh one
    await supabase
      .from('stock_analyses')
      .delete()
      .eq('ticker_key', ticker)

    const { data: saved, error: saveError } = await supabase
      .from('stock_analyses')
      .insert({
        ticker_key: ticker,
        summary: analysis.summary,
        sentiment: analysis.sentiment,
        confidence: analysis.confidence,
        generated_at: new Date().toISOString(),
      })
      .select()
      .single()

    if (saveError) throw saveError

    return new Response(JSON.stringify(saved), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      status: 200,
    })
  } catch (error: any) {
    console.error('generate-stock-analysis error:', error)
    return new Response(JSON.stringify({ error: error.message }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      status: 500,
    })
  }
})
