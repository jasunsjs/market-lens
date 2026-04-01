import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"

const FINNHUB_API_KEY = Deno.env.get('FINNHUB_API_KEY')
const SUPABASE_URL = Deno.env.get('SUPABASE_URL')
const SUPABASE_SERVICE_ROLE_KEY = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY')

serve(async (req) => {
  try {
    const supabase = createClient(SUPABASE_URL!, SUPABASE_SERVICE_ROLE_KEY!)

    // 1. Fetch enabled alert rules
    const { data: rules, error: rulesError } = await supabase
      .from('alert_rules')
      .select('*')
      .eq('enabled', true)

    if (rulesError) throw rulesError

    const uniqueTickers = [...new Set(rules.map(r => r.ticker_key))]
    const today = new Date().toISOString().split('T')[0]
    const yesterday = new Date(Date.now() - 86400000).toISOString().split('T')[0]

    for (const ticker of uniqueTickers) {
      // 2. Get quote from Finnhub
      const quoteResponse = await fetch(`https://finnhub.io/api/v1/quote?symbol=${ticker}&token=${FINNHUB_API_KEY}`)
      const quote = await quoteResponse.json()
      const percentChange = quote.dp

      // 3. Check against user-defined rules
      const matchingRules = rules.filter(r => r.ticker_key === ticker && Math.abs(percentChange) >= r.threshold)

      if (matchingRules.length > 0) {
        console.log(`Processing event for ${ticker}: ${percentChange}% change`)

        // 4. Fetch news to use headline as description
        const newsRes = await fetch(`https://finnhub.io/api/v1/company-news?symbol=${ticker}&from=${yesterday}&to=${today}&token=${FINNHUB_API_KEY}`)
        const news = await newsRes.json()
        const description = news.length > 0 ? news[0].headline : `Significant price move for ${ticker}`

        // 5. Save the event to market_events
        const { data: event, error: eventErr } = await supabase.from('market_events').insert({
          ticker_key: ticker,
          event_type: percentChange > 0 ? 'PRICE_SPIKE_UP' : 'PRICE_SPIKE_DOWN',
          percent_move: percentChange,
          start_time: new Date().toISOString(),
          price_before: quote.pc,
          price_after: quote.c,
          brief_description: description
        }).select().single()

        if (event) {
          // 6. Record notifications for all users whose rules were triggered
          await supabase.from('alert_notifications').insert(
            matchingRules.map(rule => ({
              alert_rule_id: rule.id,
              event_id: event.id
            }))
          )
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
