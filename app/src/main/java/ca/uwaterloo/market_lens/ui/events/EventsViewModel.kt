package ca.uwaterloo.market_lens.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.market_lens.di.AppGraph
import ca.uwaterloo.market_lens.domain.model.MarketEvent
import ca.uwaterloo.market_lens.domain.service.MarketLensModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EventsUiState(
    val events: List<MarketEvent> = emptyList(),
    val isLoading: Boolean = false
)

class EventsViewModel(
    private val model: MarketLensModel = AppGraph.model
) : ViewModel() {
    private val _uiState = MutableStateFlow(EventsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadEvents()
    }

    private fun loadEvents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val portfolio = model.getPortfolio()
            val portfolioTickers = portfolio.positions.map { it.tickerKey }.toSet()
            
            val allEvents = model.getEvents()
            val filteredEvents = allEvents.filter { it.tickerKey in portfolioTickers }
            
            _uiState.value = _uiState.value.copy(events = filteredEvents, isLoading = false)
        }
    }
}
