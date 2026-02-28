package ca.uwaterloo.market_lens.domain.repository

import ca.uwaterloo.market_lens.domain.model.PriceRange
import ca.uwaterloo.market_lens.domain.model.PriceSeries
import ca.uwaterloo.market_lens.domain.model.StockQuote

interface MarketDataRepository {
    suspend fun getQuote(tickerKey: String): StockQuote
    suspend fun getPriceSeries(tickerKey: String, range: PriceRange): PriceSeries
}