package ca.uwaterloo.market_lens.domain.model

data class Portfolio(
    val id: String,
    val ownerUserId: String,
    val positions: List<PortfolioPosition>
)
