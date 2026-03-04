package ca.uwaterloo.market_lens.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.market_lens.di.AppGraph
import ca.uwaterloo.market_lens.domain.model.AiExplanation
import ca.uwaterloo.market_lens.domain.model.EventCause
import ca.uwaterloo.market_lens.domain.model.MarketEvent
import ca.uwaterloo.market_lens.domain.service.MarketLensModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EventOverviewUiState(
    val event: MarketEvent? = null,
    val causes: List<EventCause> = emptyList(),
    val explanation: AiExplanation? = null,
    val isLoading: Boolean = false
)

class EventOverviewViewModel(
    private val model: MarketLensModel = AppGraph.model
) : ViewModel() {
    private val _uiState = MutableStateFlow(EventOverviewUiState())
    val uiState = _uiState.asStateFlow()

    fun loadEventDetail(eventId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val event = model.getEventById(eventId)
            val causes = model.getEventCauses(eventId)
            val explanation = model.getExplanation(eventId)
            
            _uiState.value = _uiState.value.copy(
                event = event,
                causes = causes,
                explanation = explanation,
                isLoading = false
            )
        }
    }
}
