export const getEventAnalysisPrompt = (ticker: string, percentChange: number, newsContext: string) => `
You are a financial analyst. A price event occurred for ${ticker}: ${percentChange}% change.

Recent news headlines:
${newsContext}

Task:
1. Identify up to 3 most relevant news items from the list above that likely caused this price move.
2. Reference them by their list index (1, 2, 3, etc.).
3. Provide a detailed summary and bullet points for the event.

Return a JSON object ONLY with this structure (no markdown, no code fences):
{
  "causes": [
    { "news_index": 1, "rationale": "Why this specific news caused the move", "relevance_score": 0.95 }
  ],
  "explanation": {
    "summary": "2-3 sentence overview of the event",
    "bullets": ["Key point 1", "Key point 2", "Key point 3"],
    "sentiment": "BULLISH|BEARISH|NEUTRAL",
    "confidence": 0.9
  }
}
`.trim();
