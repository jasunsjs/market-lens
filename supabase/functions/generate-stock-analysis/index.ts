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
    const { ticker } = await req.json()
    if (!ticker) {
      return new Response(JSON.stringify({ error: 'ticker is required' }), {
        headers: { ...corsHeaders, 'Content-Type': 'application/json' },
        status: 400,
      })
    }

    const supabase = createClient(SUPABASE_URL!, SUPABASE_SERVICE_ROLE_KEY!)

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

Return a JSON object ONLY with this exact structure (no markdown, no code fences):
{"summary":"<full analysis text>","sentiment":"BULLISH|BEARISH|NEUTRAL|MIXED","confidence":<0.0 to 1.0>}`

    const geminiRes = await fetch(
      `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${GEMINI_API_KEY}`,
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
    console.log('Gemini status:', geminiRes.status)
    console.log('Gemini response:', JSON.stringify(geminiData))
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
