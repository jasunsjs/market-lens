package ca.uwaterloo.market_lens.data.supabase

import ca.uwaterloo.market_lens.BuildConfig
import ca.uwaterloo.market_lens.domain.model.AiExplanation
import ca.uwaterloo.market_lens.domain.model.Sentiment
import ca.uwaterloo.market_lens.domain.model.StockAnalysis
import ca.uwaterloo.market_lens.domain.repository.ExplanationRepository
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class SupabaseExplanationRepository : ExplanationRepository {
    private val client = SupabaseClientProvider.client

    private val http = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

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

    override suspend fun getStockAnalysis(tickerKey: String): StockAnalysis {
        // Call the Edge Function - it returns a fresh (or newly generated) analysis.
        // If the function fails for any reason, fall back to the most recent DB row.
        val edgeResult = runCatching {
            http.post("${BuildConfig.SUPABASE_URL}/functions/v1/generate-stock-analysis") {
                header(HttpHeaders.Authorization, "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
                contentType(ContentType.Application.Json)
                setBody("""{"ticker":"$tickerKey"}""")
            }.body<StockAnalysisRow>()
        }.getOrNull()

        if (edgeResult != null) {
            return edgeResult.toDomain()
        }

        // Fallback: direct DB read
        return client.from("stock_analyses")
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
    val id: String? = null,
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
