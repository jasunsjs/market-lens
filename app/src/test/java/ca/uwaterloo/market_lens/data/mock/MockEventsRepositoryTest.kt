package ca.uwaterloo.market_lens.data.mock

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class MockEventsRepositoryTest {

    private val repository = MockEventsRepository()

    @Test
    fun getEvents_returns_mockdb_events() = runBlocking {
        val events = repository.getEvents()

        assertEquals(MockDb.events, events)
    }

    @Test
    fun getEventById_returns_matching_event() = runBlocking {
        val expected = MockDb.events.first()

        val event = repository.getEventById(expected.id)

        assertEquals(expected, event)
    }

    @Test
    fun getEventById_throws_for_unknown_id() {
        assertThrows(NoSuchElementException::class.java) {
            runBlocking {
                repository.getEventById("event-does-not-exist")
            }
        }
    }

    @Test
    fun getEventCauses_filters_causes_by_event_id() = runBlocking {
        val eventId = MockDb.eventCauses.first().eventId
        val expected = MockDb.eventCauses.filter { it.eventId == eventId }

        val causes = repository.getEventCauses(eventId)

        assertEquals(expected, causes)
        assertTrue(causes.all { it.eventId == eventId })
    }
}
