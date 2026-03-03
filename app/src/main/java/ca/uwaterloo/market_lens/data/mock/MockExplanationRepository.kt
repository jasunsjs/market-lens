package ca.uwaterloo.market_lens.data.mock

import ca.uwaterloo.market_lens.domain.model.AiExplanation
import ca.uwaterloo.market_lens.domain.model.StockAnalysis
import ca.uwaterloo.market_lens.domain.repository.ExplanationRepository

class MockExplanationRepository : ExplanationRepository {
    override suspend fun getExplanation(eventId: String): AiExplanation =
        MockDb.aiExplanations[eventId]
            ?: throw NoSuchElementException("No explanation for $eventId")

    override suspend fun getStockAnalysis(tickerKey: String): StockAnalysis =
        MockDb.stockAnalyses[tickerKey]
            ?: throw NoSuchElementException("No analysis for $tickerKey")
}
