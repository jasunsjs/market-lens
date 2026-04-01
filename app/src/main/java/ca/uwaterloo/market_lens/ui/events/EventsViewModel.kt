package ca.uwaterloo.market_lens.ui.events

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.market_lens.di.AppGraph
import ca.uwaterloo.market_lens.domain.model.MarketEvent
import ca.uwaterloo.market_lens.domain.service.MarketLensModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EventsUiState(
    val events: List<MarketEvent> = emptyList(),
    val isLoading: Boolean = false
)

class EventsViewModel(
    private val model: MarketLensModel = AppGraph.model
) : ViewModel() {
    private val TAG = "EventsViewModel"
    private val _uiState = MutableStateFlow(EventsUiState())
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    private val _allEvents = MutableStateFlow<List<MarketEvent>>(emptyList())

    init {
        loadEvents()
        observeSimulation()
    }

    fun loadEvents() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Loading events from repository...")
                _uiState.value = _uiState.value.copy(isLoading = true)
                val events = model.getEvents()
                Log.d(TAG, "Fetched ${events.size} events")
                _allEvents.value = events
                updateFilteredEvents(SimulationManager.simulatedEventIds.value)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading events", e)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun observeSimulation() {
        viewModelScope.launch {
            SimulationManager.simulatedEventIds.collect { simulatedIds ->
                Log.d(TAG, "Observed simulated IDs change: $simulatedIds")
                updateFilteredEvents(simulatedIds)
            }
        }
    }

    private suspend fun updateFilteredEvents(simulatedIds: Set<String>) {
        try {
            val portfolio = model.getPortfolio()
            val portfolioTickers = portfolio.positions.map { it.tickerKey.uppercase() }.toSet()
            
            Log.d(TAG, "Filtering events. Portfolio tickers: $portfolioTickers, Simulated IDs: $simulatedIds")

            val filteredEvents = _allEvents.value.filter { event ->
                val isSimulated = event.id in simulatedIds
                val inPortfolio = event.tickerKey.uppercase() in portfolioTickers
                isSimulated && inPortfolio
            }

            Log.d(TAG, "Filtered result: ${filteredEvents.size} events")

            _uiState.value = _uiState.value.copy(
                events = filteredEvents.sortedByDescending { it.detectedAt },
                isLoading = false
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error filtering events", e)
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
}
