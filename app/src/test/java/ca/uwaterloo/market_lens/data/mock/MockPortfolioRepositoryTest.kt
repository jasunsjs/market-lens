package ca.uwaterloo.market_lens.data.mock

import ca.uwaterloo.market_lens.domain.model.PortfolioPosition
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MockPortfolioRepositoryTest {

    private lateinit var repository: MockPortfolioRepository
    private lateinit var positionsSnapshot: List<PortfolioPosition>

    @Before
    fun setUp() {
        repository = MockPortfolioRepository()
        positionsSnapshot = MockDb.portfolioPositions.toList()
    }

    @After
    fun tearDown() {
        MockDb.portfolioPositions.clear()
        MockDb.portfolioPositions.addAll(positionsSnapshot)
    }

    @Test
    fun getPortfolio_returns_metadata_and_positions_snapshot() = runBlocking {
        val portfolio = repository.getPortfolio()

        assertEquals("mock-portfolio-1", portfolio.id)
        assertEquals("mock-user-1", portfolio.ownerUserId)
        assertEquals(MockDb.portfolioPositions, portfolio.positions)

        MockDb.portfolioPositions.add(PortfolioPosition(tickerKey = "TEMP"))
        assertEquals(positionsSnapshot.size, portfolio.positions.size)
    }

    @Test
    fun addTicker_adds_new_ticker() = runBlocking {
        val tickerKey = "ZZZZ_TEST"

        repository.addTicker(tickerKey)

        assertTrue(MockDb.portfolioPositions.any { it.tickerKey == tickerKey })
    }

    @Test
    fun removeTicker_removes_existing_ticker() = runBlocking {
        val tickerToRemove = MockDb.portfolioPositions.first().tickerKey

        repository.removeTicker(tickerToRemove)

        assertTrue(MockDb.portfolioPositions.none { it.tickerKey == tickerToRemove })
    }

    @Test
    fun updateShares_updates_shares_for_existing_ticker() = runBlocking {
        val ticker = MockDb.portfolioPositions.first().tickerKey

        repository.updateShares(ticker, 42.5, null)

        val updated = MockDb.portfolioPositions.first { it.tickerKey == ticker }
        assertEquals(42.5, updated.shares!!, 0.0)
    }

    @Test
    fun updateShares_updates_avgCost_for_existing_ticker() = runBlocking {
        val ticker = MockDb.portfolioPositions.first().tickerKey

        repository.updateShares(ticker, 10.0, 150.25)

        val updated = MockDb.portfolioPositions.first { it.tickerKey == ticker }
        assertEquals(10.0, updated.shares!!, 0.0)
        assertEquals(150.25, updated.avgCost!!, 0.0)
    }

    @Test
    fun updateShares_does_nothing_for_unknown_ticker() = runBlocking {
        val before = MockDb.portfolioPositions.toList()

        repository.updateShares("UNKNOWN", 99.0, 100.0)

        assertEquals(before, MockDb.portfolioPositions.toList())
    }
}
