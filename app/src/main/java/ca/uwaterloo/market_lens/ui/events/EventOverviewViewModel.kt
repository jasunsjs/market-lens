package ca.uwaterloo.market_lens.ui.events

import android.util.Log
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
            Log.d("EventOverviewVM", "Loading details for event: $eventId")

            // Load event info
            try {
                val event = model.getEventById(eventId)
                _uiState.value = _uiState.value.copy(event = event)
            } catch (e: Exception) {
                Log.e("EventOverviewVM", "Failed to load event basic info", e)
            }

            // Load causes
            try {
                val causes = model.getEventCauses(eventId)
                _uiState.value = _uiState.value.copy(causes = causes)
            } catch (e: Exception) {
                Log.e("EventOverviewVM", "Failed to load event causes", e)
            }

            // Load explanation
            try {
                val explanation = model.getExplanation(eventId)
                Log.d("EventOverviewVM", "Loaded explanation summary: ${explanation.summary.take(20)}...")
                _uiState.value = _uiState.value.copy(explanation = explanation)
            } catch (e: Exception) {
                Log.e("EventOverviewVM", "Failed to load explanation", e)
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
}
