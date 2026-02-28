package ca.uwaterloo.market_lens.domain.model

data class PriceSeries(
    val tickerKey: String,
    val range: PriceRange,
    val points: List<PricePoint>
)

