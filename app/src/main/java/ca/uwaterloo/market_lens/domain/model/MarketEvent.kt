package ca.uwaterloo.market_lens.domain.model

import java.time.Instant

data class MarketEvent(
    val id: String,
    val tickerKey: String,
    val eventType: EventType,
    val percentMove: Double,
    val startTime: Instant,
    val detectedAt: Instant,
    val priceBefore: Double,
    val priceAfter: Double,
    val briefDescription: String
)
