package ca.uwaterloo.market_lens.data.mock

import ca.uwaterloo.market_lens.domain.model.PriceRange
import ca.uwaterloo.market_lens.domain.model.PriceSeries
import ca.uwaterloo.market_lens.domain.model.StockQuote
import ca.uwaterloo.market_lens.domain.repository.MarketDataRepository
import java.time.Instant

class MockMarketDataRepository : MarketDataRepository {

    override suspend fun getQuote(tickerKey: String): StockQuote {
        val existing = MockDb.quotes[tickerKey]
        if (existing != null) {
            // Update timestamp to feel "live"
            return existing.copy(asOf = Instant.now())
        }

        // MVP behavior: return a generic quote if missing
        return StockQuote(
            tickerKey = tickerKey,
            price = 100.0,
            changePercent = 0.0,
            asOf = Instant.now()
        )
    }

    override suspend fun getPriceSeries(tickerKey: String, range: PriceRange): PriceSeries {
        val key = tickerKey to range
        val existing = MockDb.series[key]
        if (existing != null) return existing

        // MVP behavior: return empty series if missing (or generate on the fly)
        return PriceSeries(
            tickerKey = tickerKey,
            range = range,
            points = emptyList()
        )
    }
}