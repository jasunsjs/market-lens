package ca.uwaterloo.market_lens.domain.model

data class PortfolioPosition(
    val tickerKey: String,
    val shares: Double? = null,
    val avgCost: Double? = null
)
