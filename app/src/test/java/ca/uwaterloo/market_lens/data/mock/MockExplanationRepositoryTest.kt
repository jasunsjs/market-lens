package ca.uwaterloo.market_lens.data.mock

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class MockExplanationRepositoryTest {

    private val repository = MockExplanationRepository()

    @Test
    fun getExplanation_returns_matching_explanation() = runBlocking {
        val (eventId, expected) = MockDb.aiExplanations.entries.first()

        val explanation = repository.getExplanation(eventId)

        assertEquals(expected, explanation)
    }

    @Test
    fun getExplanation_throws_for_unknown_event_id() {
        assertThrows(NoSuchElementException::class.java) {
            runBlocking {
                repository.getExplanation("event-does-not-exist")
            }
        }
    }

    @Test
    fun getStockAnalysis_returns_matching_analysis() = runBlocking {
        val (tickerKey, expected) = MockDb.stockAnalyses.entries.first()

        val analysis = repository.getStockAnalysis(tickerKey)

        assertEquals(expected, analysis)
    }

    @Test
    fun getStockAnalysis_throws_for_unknown_ticker() {
        assertThrows(NoSuchElementException::class.java) {
            runBlocking {
                repository.getStockAnalysis("ticker-does-not-exist")
            }
        }
    }
}
