package ca.uwaterloo.market_lens.data.mock

import ca.uwaterloo.market_lens.domain.model.AiExplanation
import ca.uwaterloo.market_lens.domain.model.EventCause
import ca.uwaterloo.market_lens.domain.model.MarketEvent
import ca.uwaterloo.market_lens.domain.repository.EventsRepository

class MockEventsRepository : EventsRepository {
    override suspend fun getEvents(): List<MarketEvent> = MockDb.events
    
    override suspend fun getEventById(eventId: String): MarketEvent =
        MockDb.events.first { it.id == eventId }

    override suspend fun getEventCauses(eventId: String): List<EventCause> =
        MockDb.eventCauses.filter { it.eventId == eventId }

    override suspend fun getEventExplanation(eventId: String): AiExplanation? =
        MockDb.aiExplanations[eventId]
}
