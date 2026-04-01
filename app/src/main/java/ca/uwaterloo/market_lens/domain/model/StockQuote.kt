package ca.uwaterloo.market_lens.domain.model

import java.time.Instant

data class StockQuote(
    val tickerKey: String,
    val price: Double,
    val changePercent: Double,
    val asOf: Instant,
    val volume: Long? = null,
    val marketCap: Long? = null,
    val peRatio: Double? = null,
    val logoUrl: String? = null
)