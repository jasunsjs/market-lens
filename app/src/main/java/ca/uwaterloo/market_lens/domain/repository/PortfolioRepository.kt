package ca.uwaterloo.market_lens.domain.repository

import ca.uwaterloo.market_lens.domain.model.Portfolio
import ca.uwaterloo.market_lens.domain.model.Ticker

interface PortfolioRepository {
    suspend fun getAvailableTickers(): List<Ticker>
    suspend fun getPortfolio(): Portfolio
    suspend fun addTicker(tickerKey: String)
    suspend fun removeTicker(tickerKey: String)
}
