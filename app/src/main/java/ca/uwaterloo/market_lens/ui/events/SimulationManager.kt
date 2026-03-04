package ca.uwaterloo.market_lens.ui.events

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Tracks simulated event IDs across different ViewModels.
 * This ensures the TopBar can trigger a simulation and the EventsScreen can react to it.
 */
object SimulationManager {
    private val _simulatedEventIds = MutableStateFlow<Set<String>>(emptySet())
    val simulatedEventIds = _simulatedEventIds.asStateFlow()

    fun triggerEvent(eventId: String) {
        _simulatedEventIds.update { it + eventId }
    }
}
