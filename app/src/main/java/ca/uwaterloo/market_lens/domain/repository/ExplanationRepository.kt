package ca.uwaterloo.market_lens.domain.repository

import ca.uwaterloo.market_lens.domain.model.AiExplanation
import ca.uwaterloo.market_lens.domain.model.StockAnalysis

interface ExplanationRepository {
    suspend fun getExplanation(eventId: String): AiExplanation
    suspend fun getStockAnalysis(tickerKey: String): StockAnalysis
}
