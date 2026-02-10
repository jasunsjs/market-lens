package ca.uwaterloo.market_lens.ui.events

data class ContributingFactor(
    val rank: Int,
    val title: String,
    val percentage: Int,
    val description: String,
    val source: String,
    val sourceUrl: String
)

data class EventData(
    val id: String,
    val ticker: String,
    val percentChange: Double,
    val briefDescription: String,
    val timestamp: String,
    val aiExplanation: String,
    val contributingFactors: List<ContributingFactor>,
    val overallConfidence: Int
)
