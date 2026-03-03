package ca.uwaterloo.market_lens.domain.model

data class AiExplanation(
    val eventId: String,
    val summary: String,
    val bullets: List<String>,
    val sentiment: Sentiment,
    val confidence: Double
)
