package ca.uwaterloo.market_lens

import ca.uwaterloo.market_lens.domain.model.*
import org.junit.Assert.*
import org.junit.Test

class DomainTypeContractsTest {

    @Test
    fun alertType_values_match_expected_contract() {
        val names = AlertType.entries.map { it.name }

        assertEquals(
            listOf("PRICE_CHANGE", "VOLUME_SPIKE", "EARNINGS_ANNOUNCEMENT"),
            names
        )
    }

    @Test
    fun eventType_values_match_expected_contract() {
        val names = EventType.entries.map { it.name }

        assertEquals(
            listOf("PRICE_SPIKE_UP", "PRICE_SPIKE_DOWN", "VOLATILITY_ANOMALY"),
            names
        )
    }

    @Test
    fun priceRange_values_match_expected_contract() {
        val names = PriceRange.entries.map { it.name }

        assertEquals(
            listOf("ONE_DAY", "FIVE_DAYS", "ONE_MONTH", "SIX_MONTHS", "ONE_YEAR"),
            names
        )
    }

    @Test
    fun sentiment_values_match_expected_contract() {
        val names = Sentiment.entries.map { it.name }

        assertEquals(
            listOf("BULLISH", "BEARISH", "NEUTRAL", "MIXED"),
            names
        )
    }
}
