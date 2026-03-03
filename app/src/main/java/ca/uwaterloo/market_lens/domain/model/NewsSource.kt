package ca.uwaterloo.market_lens.domain.model

data class NewsSource(
    val name: String,
    val url: String? = null,
    val reliabilityScore: Double? = null
)
