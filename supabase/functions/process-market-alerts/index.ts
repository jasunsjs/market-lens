import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"

const FINNHUB_API_KEY = Deno.env.get('FINNHUB_API_KEY')
const GEMINI_API_KEY = Deno.env.get('GEMINI_API_KEY')
const SUPABASE_URL = Deno.env.get('SUPABASE_URL')
const SUPABASE_SERVICE_ROLE_KEY = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY')

serve(async (req) => {
  try {
    const supabase = createClient(SUPABASE_URL!, SUPABASE_SERVICE_ROLE_KEY!)

    // get enabled alert rules
    const { data: rules, error: rulesError } = await supabase
      .from('alert_rules')
      .select('*')
      .eq('enabled', true)

    if (rulesError) throw rulesError

    // list of tickers to check
    const uniqueTickers = [...new Set(rules.map(r => r.ticker_key))]
    
    console.log(`Checking ${uniqueTickers.length} tickers...`)

    for (const ticker of uniqueTickers) {
      const quoteResponse = await fetch(`https://finnhub.io/api/v1/quote?symbol=${ticker}&token=${FINNHUB_API_KEY}`)
      const quote = await quoteResponse.json()
      
      const currentPrice = quote.c
      const percentChange = quote.dp

      // check against rules for each ticker
      const tickerRules = rules.filter(r => r.ticker_key === ticker)
      
      for (const rule of tickerRules) {
        const thresholdHit = Math.abs(percentChange) >= rule.threshold
        
        if (thresholdHit) {
          console.log(`THRESHOLD HIT: ${ticker} moved ${percentChange}% (Threshold: ${rule.threshold}%)`)
          
          // NEXT STEP: Trigger Gemini Analysis & Notification
          // We will implement this in the next increment
        }
      }
    }

    return new Response(JSON.stringify({ message: "Processed" }), {
      headers: { "Content-Type": "application/json" },
      status: 200,
    })
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      headers: { "Content-Type": "application/json" },
      status: 500,
    })
  }
})