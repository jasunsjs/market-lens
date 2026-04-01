import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"
import { getEventAnalysisPrompt } from "./prompts.ts"

const FINNHUB_API_KEY = Deno.env.get('FINNHUB_API_KEY')
const GEMINI_API_KEY = Deno.env.get('GEMINI_API_KEY')
const SUPABASE_URL = Deno.env.get('SUPABASE_URL')
const SUPABASE_SERVICE_ROLE_KEY = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY')

async function retry<T>(fn: () => Promise<T>, attempts = 3, delay = 500): Promise<T> {
  let lastErr
  for (let i = 0; i < attempts; i++) {
    try {
      return await fn()
    } catch (err) {
      lastErr = err
      await new Promise(res => setTimeout(res, delay * (i + 1)))
    }
  }
  throw lastErr
}

function safeParse(raw: string) {
  try {
    return JSON.parse(raw)
  } catch {
    const cleaned = raw
      .replace(/```json/g, '')
      .replace(/```/g, '')
      .replace(/\n/g, ' ')
      .trim()

    try {
      return JSON.parse(cleaned)
    } catch {
      console.error("JSON parse failed:", raw)
      return null
    }
  }
}

function isValid(ai: any) {
  if (!ai) return false
  if (!Array.isArray(ai.causes)) return false
  if (!ai.explanation) return false
  const exp = ai.explanation
  return (
    typeof exp.summary === "string" &&
    Array.isArray(exp.bullets) &&
    typeof exp.sentiment === "string" &&
    typeof exp.confidence === "number"
  )
}

async function callGemini(prompt: string) {
  const res = await fetch(
    `https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite-preview:generateContent?key=${GEMINI_API_KEY}`,
    {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        contents: [{ parts: [{ text: prompt }] }],
        generationConfig: {
          temperature: 0.2,
          maxOutputTokens: 800
        }
      })
    }
  )

  const data = await res.json()

  if (data.error) {
    throw new Error(JSON.stringify(data.error))
  }

  return data?.candidates?.[0]?.content?.parts?.[0]?.text
}

serve(async () => {
  try {
    console.log("Starting process-market-alerts")
    const supabase = createClient(SUPABASE_URL!, SUPABASE_SERVICE_ROLE_KEY!)
    const { data: rules, error } = await supabase
      .from('alert_rules')
      .select('*')
      .eq('enabled', true)
    if (error) throw error
    const uniqueTickers = [...new Set(rules.map(r => r.ticker_key))]
    const today = new Date().toISOString().split('T')[0]
    const yesterday = new Date(Date.now() - 86400000).toISOString().split('T')[0]
    for (const ticker of uniqueTickers) {
      console.log(`Checking ${ticker}`)
      const quote = await retry(async () => {
        const res = await fetch(
          `https://finnhub.io/api/v1/quote?symbol=${ticker}&token=${FINNHUB_API_KEY}`
        )
        return res.json()
      })
      const percentChange = quote.dp
      const matchingRules = rules.filter(
        r => r.ticker_key === ticker && Math.abs(percentChange) >= r.threshold
      )
      if (matchingRules.length === 0) continue

      // check if a duplicate event was recorded in the last 60 minutes
      const oneHourAgo = new Date(Date.now() - 60 * 60 * 1000).toISOString()
      const { data: existingEvent } = await supabase
        .from('market_events')
        .select('id')
        .eq('ticker_key', ticker)
        .eq('price_after', quote.c)
        .eq('percent_move', quote.dp)
        .gt('start_time', oneHourAgo)
        .limit(1)
        .maybeSingle()
      if (existingEvent) {
        console.log(`Duplicate event detected for ${ticker} at price ${quote.c}. Skipping.`)
        continue
      }
      console.log(`MATCH ${ticker}: ${percentChange}%`)
      const news: any[] = await retry(async () => {
        const res = await fetch(
          `https://finnhub.io/api/v1/company-news?symbol=${ticker}&from=${yesterday}&to=${today}&token=${FINNHUB_API_KEY}`
        )
        return res.json()
      })
      const topNews = news.slice(0, 10)
      const { data: event } = await supabase
        .from('market_events')
        .insert({
          ticker_key: ticker,
          event_type: percentChange > 0 ? 'PRICE_SPIKE_UP' : 'PRICE_SPIKE_DOWN',
          percent_move: percentChange,
          start_time: new Date().toISOString(),
          price_before: quote.pc,
          price_after: quote.c,
          brief_description: topNews[0]?.headline || ticker
        })
        .select()
        .single()
      if (!event) continue
      await supabase.from('alert_notifications').insert(
        matchingRules.map(rule => ({
          alert_rule_id: rule.id,
          event_id: event.id
        }))
      )

      if (!GEMINI_API_KEY || topNews.length === 0) continue

      try {
        const newsContext = topNews
          .map((n, i) => `${i + 1}. ${n.headline} | ${n.summary}`)
          .join('\n')
        const prompt = getEventAnalysisPrompt(ticker, percentChange, newsContext)
        const rawText = await retry(() => callGemini(prompt), 3, 700)
        if (!rawText) {
          console.warn("No Gemini output")
          continue
        }
        const aiResult = safeParse(rawText)
        if (!isValid(aiResult)) {
          console.error("Invalid AI structure:", aiResult)
          continue
        }
        const causes = aiResult.causes.map((c: any, i: number) => {
          const idx = Math.max(0, Math.min(topNews.length - 1, c.news_index - 1))
          const newsItem = topNews[idx]
          return {
            event_id: event.id,
            title: newsItem?.headline || "Unknown",
            rationale: c.rationale,
            url: newsItem?.url || "",
            news_item_id: newsItem?.id?.toString() || "",
            rank: i + 1,
            relevance_score: c.relevance_score
          }
        })
        await supabase.from('event_causes').insert(causes)
        await supabase.from('ai_explanations').insert({
          event_id: event.id,
          summary: aiResult.explanation.summary,
          bullets: aiResult.explanation.bullets,
          sentiment: aiResult.explanation.sentiment,
          confidence: aiResult.explanation.confidence,
          generated_at: new Date().toISOString()
        })
        console.log(`Gemini processed for ${ticker}`)
      } catch (err) {
        console.error("Gemini failed:", err)
      }
    }
    return new Response(JSON.stringify({ message: "Processed" }), {
      headers: { "Content-Type": "application/json" },
      status: 200
    })
  } catch (err) {
    console.error("Global error:", err)
    return new Response(JSON.stringify({ error: err.message }), {
      status: 500
    })
  }
})