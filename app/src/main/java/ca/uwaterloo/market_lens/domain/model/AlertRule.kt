package ca.uwaterloo.market_lens.domain.model

data class AlertRule(
    val id: String,
    val tickerKey: String,
    val alertType: AlertType,
    val threshold: Double,
    val enabled: Boolean
)
