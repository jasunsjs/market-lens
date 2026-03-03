package ca.uwaterloo.market_lens.domain.model

data class StockAnalysis(
    val tickerKey: String,
    val summary: String,
    val sentiment: Sentiment,
    val confidence: Double
)
