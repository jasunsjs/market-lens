package ca.uwaterloo.market_lens.data.supabase

import android.util.Log
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
    private val TAG = "SupabaseExplRepo"

    private val http = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    // In SupabaseExplanationRepository.kt
    override suspend fun getExplanation(eventId: String): AiExplanation {
        Log.d(TAG, "Fetching explanation for eventId: $eventId")
        return try {
            val row = client.from("ai_explanations")
                .select { filter { eq("event_id", eventId) }; limit(1) }
                .decodeSingleOrNull<AiExplanationRow>()

            Log.d(TAG, "Fetched explanation row: $row")
            row?.toDomain() ?: AiExplanation(
                eventId = eventId,
                summary = "No explanation is available for this event yet.",
                bullets = emptyList(),
                sentiment = Sentiment.NEUTRAL,
                confidence = 0.0
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching explanation", e)
            // Return default so UI can still show the section header
            AiExplanation(eventId, "Error loading explanation content.", emptyList(), Sentiment.NEUTRAL, 0.0)
        }
    }


    override suspend fun getStockAnalysis(tickerKey: String): StockAnalysis {
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
data class AiExplanationRow(
    @SerialName("event_id")
    val eventId: String,
    val summary: String? = null,
    val bullets: List<String> = emptyList(),
    val sentiment: String? = null,
    val confidence: Double? = null,
    @SerialName("generated_at")
    val generatedAt: String? = null
) {
    fun toDomain(): AiExplanation =
        AiExplanation(
            eventId = eventId,
            summary = summary ?: "No summary available.",
            bullets = bullets,
            sentiment = try { Sentiment.valueOf(sentiment?.uppercase() ?: "NEUTRAL") } catch (e: Exception) { Sentiment.NEUTRAL },
            confidence = confidence ?: 0.0
        )
}

@Serializable
private data class StockAnalysisRow(
    val id: String? = null,
    @SerialName("ticker_key")
    val tickerKey: String,
    val summary: String? = null,
    val sentiment: String? = null,
    val confidence: Double? = null,
    @SerialName("generated_at")
    val generatedAt: String? = null
) {
    fun toDomain(): StockAnalysis =
        StockAnalysis(
            tickerKey = tickerKey,
            summary = summary ?: "No summary available.",
            sentiment = try { Sentiment.valueOf(sentiment?.uppercase() ?: "NEUTRAL") } catch (e: Exception) { Sentiment.NEUTRAL },
            confidence = confidence ?: 0.0
        )
}
