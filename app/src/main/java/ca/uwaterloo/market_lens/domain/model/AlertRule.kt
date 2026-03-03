package ca.uwaterloo.market_lens.domain.model

data class AlertRule(
    val id: String,
    val tickerKey: String,
    val percentThreshold: Double,
    val enabled: Boolean
)
