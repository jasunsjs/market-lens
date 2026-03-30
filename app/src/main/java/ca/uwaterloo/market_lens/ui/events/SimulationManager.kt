package ca.uwaterloo.market_lens.ui.events

import android.util.Log
import ca.uwaterloo.market_lens.domain.model.MarketEvent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Tracks simulated event IDs across different ViewModels.
 * This ensures the TopBar can trigger a simulation and the EventsScreen can react to it.
 */
object SimulationManager {
    private const val TAG = "SimulationManager"
    
    private val _simulatedEventIds = MutableStateFlow<Set<String>>(emptySet())
    val simulatedEventIds = _simulatedEventIds.asStateFlow()

    // need a valid buffer and collector for simulated event to run
    private val _latestTriggeredEvent = MutableSharedFlow<MarketEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val latestTriggeredEvent = _latestTriggeredEvent.asSharedFlow()

    fun triggerEvent(event: MarketEvent) {
        Log.d(TAG, "Triggering event: ${event.id} for ${event.tickerKey}")
        _simulatedEventIds.update { it + event.id }
        val success = _latestTriggeredEvent.tryEmit(event)
        Log.d(TAG, "Event emission success: $success")
    }
}
