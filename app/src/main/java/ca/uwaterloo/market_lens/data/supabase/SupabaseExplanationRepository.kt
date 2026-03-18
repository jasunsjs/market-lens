package ca.uwaterloo.market_lens.data.supabase

import ca.uwaterloo.market_lens.domain.model.AiExplanation
import ca.uwaterloo.market_lens.domain.model.Sentiment
import ca.uwaterloo.market_lens.domain.model.StockAnalysis
import ca.uwaterloo.market_lens.domain.repository.ExplanationRepository
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class SupabaseExplanationRepository : ExplanationRepository {
    private val client = SupabaseClientProvider.client

    override suspend fun getExplanation(eventId: String): AiExplanation =
        client.from("ai_explanations")
            .select {
                filter {
                    eq("event_id", eventId)
                }
                limit(1)
            }
            .decodeSingleOrNull<AiExplanationRow>()
            ?.toDomain()
            ?: AiExplanation(
                eventId = eventId,
                summary = "No explanation is available for this event yet.",
                bullets = emptyList(),
                sentiment = Sentiment.NEUTRAL,
                confidence = 0.0
            )

    override suspend fun getStockAnalysis(tickerKey: String): StockAnalysis =
        client.from("stock_analyses")
            .select {
                filter {
                    eq("ticker_key", tickerKey)
                }
                order("generated_at", Order.DESCENDING)
                limit(1)
            }
            .decodeSingleOrNull<StockAnalysisRow>()
            ?.toDomain()
            ?: StockAnalysis(
                tickerKey = tickerKey,
                summary = "No stock analysis is available for this ticker yet.",
                sentiment = Sentiment.NEUTRAL,
                confidence = 0.0
            )
}

@Serializable
private data class AiExplanationRow(
    @SerialName("event_id")
    val eventId: String,
    val summary: String,
    val bullets: List<String>,
    val sentiment: String,
    val confidence: Double,
    @SerialName("generated_at")
    val generatedAt: String? = null
) {
    fun toDomain(): AiExplanation =
        AiExplanation(
            eventId = eventId,
            summary = summary,
            bullets = bullets,
            sentiment = Sentiment.valueOf(sentiment),
            confidence = confidence
        )
}

@Serializable
private data class StockAnalysisRow(
    val id: String,
    @SerialName("ticker_key")
    val tickerKey: String,
    val summary: String,
    val sentiment: String,
    val confidence: Double,
    @SerialName("generated_at")
    val generatedAt: String? = null
) {
    fun toDomain(): StockAnalysis =
        StockAnalysis(
            tickerKey = tickerKey,
            summary = summary,
            sentiment = Sentiment.valueOf(sentiment),
            confidence = confidence
        )
}
