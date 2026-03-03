package ca.uwaterloo.market_lens.domain.model

data class EventCause(
    val eventId: String,
    val newsItemId: String,
    val rank: Int,
    val title: String,
    val relevanceScore: Double,
    val rationale: String
)
