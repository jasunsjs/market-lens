package ca.uwaterloo.market_lens.domain.repository

import ca.uwaterloo.market_lens.domain.model.AiExplanation
import ca.uwaterloo.market_lens.domain.model.EventCause
import ca.uwaterloo.market_lens.domain.model.MarketEvent

interface EventsRepository {
    suspend fun getEvents(): List<MarketEvent>
    suspend fun getEventById(eventId: String): MarketEvent
    suspend fun getEventCauses(eventId: String): List<EventCause>
    suspend fun getEventExplanation(eventId: String): AiExplanation?
}
