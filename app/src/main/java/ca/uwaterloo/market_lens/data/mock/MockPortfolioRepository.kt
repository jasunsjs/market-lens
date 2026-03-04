package ca.uwaterloo.market_lens.data.mock

import ca.uwaterloo.market_lens.domain.model.Portfolio
import ca.uwaterloo.market_lens.domain.model.PortfolioPosition
import ca.uwaterloo.market_lens.domain.model.Ticker
import ca.uwaterloo.market_lens.domain.repository.PortfolioRepository

class MockPortfolioRepository : PortfolioRepository {
    override suspend fun getAvailableTickers(): List<Ticker> =
        MockDb.quotes.keys.map { Ticker(it) }

    override suspend fun getPortfolio(): Portfolio =
        Portfolio(
            id = "mock-portfolio-1",
            ownerUserId = "mock-user-1",
            positions = MockDb.portfolioPositions.toList()
        )

    override suspend fun addTicker(tickerKey: String) {
        if (MockDb.portfolioPositions.none { it.tickerKey == tickerKey })
            MockDb.portfolioPositions.add(PortfolioPosition(tickerKey))
    }

    override suspend fun removeTicker(tickerKey: String) {
        MockDb.portfolioPositions.removeIf { it.tickerKey == tickerKey }
    }
}
