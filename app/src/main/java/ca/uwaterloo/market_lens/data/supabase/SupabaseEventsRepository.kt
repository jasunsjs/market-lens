package ca.uwaterloo.market_lens.data.supabase

import android.util.Log
import ca.uwaterloo.market_lens.domain.model.EventCause
import ca.uwaterloo.market_lens.domain.model.EventType
import ca.uwaterloo.market_lens.domain.model.MarketEvent
import ca.uwaterloo.market_lens.domain.repository.EventsRepository
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class SupabaseEventsRepository : EventsRepository {
    private val client = SupabaseClientProvider.client
    private val TAG = "SupabaseEventsRepo"

    override suspend fun getEvents(): List<MarketEvent> = try {
        Log.d(TAG, "Querying market_events table...")
        val response = client.from("market_events")
            .select {
                order("detected_at", Order.DESCENDING)
            }
        
        // Log the raw JSON to see if any data is coming back
        Log.d(TAG, "Raw response body: ${response.data}")

        val events = response.decodeList<MarketEventRow>()
        Log.d(TAG, "Successfully decoded ${events.size} events")
        events.map { it.toDomain() }
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching events from Supabase", e)
        emptyList()
    }

    override suspend fun getEventById(eventId: String): MarketEvent =
        client.from("market_events")
            .select {
                filter {
                    eq("id", eventId)
                }
                limit(1)
            }
            .decodeSingleOrNull<MarketEventRow>()
            ?.toDomain()
            ?: throw NoSuchElementException("No event for $eventId")

    override suspend fun getEventCauses(eventId: String): List<EventCause> = try {
        client.from("event_causes")
            .select {
                filter {
                    eq("event_id", eventId)
                }
                order("rank", Order.ASCENDING)
            }
            .decodeList<EventCauseRow>()
            .map { it.toDomain() }
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching event causes", e)
        emptyList()
    }
}

@Serializable
private data class MarketEventRow(
    val id: String,
    @SerialName("ticker_key")
    val tickerKey: String,
    @SerialName("event_type")
    val eventType: String,
    @SerialName("percent_move")
    val percentMove: Double,
    @SerialName("start_time")
    val startTime: String,
    @SerialName("detected_at")
    val detectedAt: String,
    @SerialName("price_before")
    val priceBefore: Double,
    @SerialName("price_after")
    val priceAfter: Double,
    @SerialName("brief_description")
    val briefDescription: String
) {
    fun toDomain(): MarketEvent =
        MarketEvent(
            id = id,
            tickerKey = tickerKey.uppercase(),
            eventType = try {
                EventType.valueOf(eventType.uppercase())
            } catch (e: Exception) {
                Log.w("MarketEventRow", "Unknown event type: $eventType, defaulting to PRICE_SPIKE_UP")
                EventType.PRICE_SPIKE_UP
            },
            percentMove = percentMove,
            startTime = parseInstant(startTime),
            detectedAt = parseInstant(detectedAt),
            priceBefore = priceBefore,
            priceAfter = priceAfter,
            briefDescription = briefDescription
        )
}

@Serializable
private data class EventCauseRow(
    val id: String,
    @SerialName("event_id")
    val eventId: String,
    @SerialName("news_item_id")
    val newsItemId: String,
    val url: String,
    val rank: Int,
    val title: String,
    @SerialName("relevance_score")
    val relevanceScore: Double,
    val rationale: String
) {
    fun toDomain(): EventCause =
        EventCause(
            eventId = eventId,
            newsItemId = newsItemId,
            rank = rank,
            title = title,
            relevanceScore = relevanceScore,
            rationale = rationale
        )
}
