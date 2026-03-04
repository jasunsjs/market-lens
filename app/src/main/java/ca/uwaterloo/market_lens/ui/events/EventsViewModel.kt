package ca.uwaterloo.market_lens.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.market_lens.di.AppGraph
import ca.uwaterloo.market_lens.domain.model.MarketEvent
import ca.uwaterloo.market_lens.domain.service.MarketLensModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class EventsUiState(
    val events: List<MarketEvent> = emptySet<MarketEvent>().toList(),
    val isLoading: Boolean = false
)

class EventsViewModel(
    private val model: MarketLensModel = AppGraph.model
) : ViewModel() {
    private val _uiState = MutableStateFlow(EventsUiState())
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    private val _allEvents = MutableStateFlow<List<MarketEvent>>(emptyList())

    init {
        loadEvents()
        observeSimulation()
    }

    private fun loadEvents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            _allEvents.value = model.getEvents()
            updateFilteredEvents(SimulationManager.simulatedEventIds.value)
        }
    }

    private fun observeSimulation() {
        viewModelScope.launch {
            SimulationManager.simulatedEventIds.collect { simulatedIds ->
                updateFilteredEvents(simulatedIds)
            }
        }
    }

    private suspend fun updateFilteredEvents(simulatedIds: Set<String>) {
        val portfolio = model.getPortfolio()
        val portfolioTickers = portfolio.positions.map { it.tickerKey }.toSet()
        
        val filteredEvents = _allEvents.value.filter { 
            it.id in simulatedIds && it.tickerKey in portfolioTickers 
        }
        
        _uiState.value = _uiState.value.copy(
            events = filteredEvents.sortedByDescending { it.detectedAt },
            isLoading = false
        )
    }
}
