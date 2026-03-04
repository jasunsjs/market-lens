package ca.uwaterloo.market_lens.data.mock

import ca.uwaterloo.market_lens.domain.model.PriceRange
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class MockMarketDataRepositoryTest {

    private val repository = MockMarketDataRepository()

    @Test
    fun getQuote_returns_existing_quote_data_for_known_ticker() = runBlocking {
        val (tickerKey, existing) = MockDb.quotes.entries.first()

        val quote = repository.getQuote(tickerKey)

        assertEquals(tickerKey, quote.tickerKey)
        assertEquals(existing.price, quote.price, 0.0)
        assertEquals(existing.changePercent, quote.changePercent, 0.0)
        assertEquals(existing.volume, quote.volume)
        assertEquals(existing.marketCap, quote.marketCap)
        assertEquals(existing.peRatio, quote.peRatio)
        assertTrue(!quote.asOf.isBefore(existing.asOf))
    }

    @Test
    fun getQuote_returns_default_quote_for_unknown_ticker() = runBlocking {
        val quote = repository.getQuote("UNKNOWN_TICKER")

        assertEquals("UNKNOWN_TICKER", quote.tickerKey)
        assertEquals(100.0, quote.price, 0.0)
        assertEquals(0.0, quote.changePercent, 0.0)
    }

    @Test
    fun getPriceSeries_returns_existing_series_for_known_key() = runBlocking {
        val (key, existingSeries) = MockDb.series.entries.first()

        val series = repository.getPriceSeries(key.first, key.second)

        assertEquals(existingSeries, series)
    }
}
