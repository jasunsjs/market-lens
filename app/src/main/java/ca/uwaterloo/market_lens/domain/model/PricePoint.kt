package ca.uwaterloo.market_lens.domain.model

import java.time.Instant

data class PricePoint(
    val timestamp: Instant,
    val close: Double
)