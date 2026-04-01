package ca.uwaterloo.market_lens.data.mock

import ca.uwaterloo.market_lens.domain.model.Portfolio
import ca.uwaterloo.market_lens.domain.model.PortfolioPosition
import ca.uwaterloo.market_lens.domain.model.Ticker
import ca.uwaterloo.market_lens.domain.repository.PortfolioRepository

class MockPortfolioRepository : PortfolioRepository {
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

    override suspend fun updateShares(tickerKey: String, shares: Double, avgCost: Double?) {
        val index = MockDb.portfolioPositions.indexOfFirst { it.tickerKey == tickerKey }
        if (index >= 0) MockDb.portfolioPositions[index] = MockDb.portfolioPositions[index].copy(shares = shares, avgCost = avgCost)
    }
}
