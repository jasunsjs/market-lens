package ca.uwaterloo.market_lens.ui.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.market_lens.di.AppGraph
import ca.uwaterloo.market_lens.domain.model.AlertRule
import ca.uwaterloo.market_lens.domain.model.AlertType
import ca.uwaterloo.market_lens.domain.service.MarketLensModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class AlertConfigUiState(
    val alertRules: List<AlertRule> = emptyList(),
    val isLoading: Boolean = false,
    val isSavedFeedbackVisible: Boolean = false
)

class AlertsViewModel(
    private val model: MarketLensModel = AppGraph.model
) : ViewModel() {
    private val _uiState = MutableStateFlow(AlertConfigUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAlertRules()
    }

    private fun loadAlertRules() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val rules = model.getAlertRules()
            _uiState.update { it.copy(alertRules = rules, isLoading = false) }
        }
    }

    fun onAlertEnabledChanged(ruleId: String, enabled: Boolean) {
        viewModelScope.launch {
            val rule = _uiState.value.alertRules.find { it.id == ruleId } ?: return@launch
            model.editAlertRule(rule.copy(enabled = enabled))
            loadAlertRules()
        }
    }

    fun onThresholdChanged(ruleId: String, threshold: Double) {
        viewModelScope.launch {
            val rule = _uiState.value.alertRules.find { it.id == ruleId } ?: return@launch
            model.editAlertRule(rule.copy(threshold = threshold))
            loadAlertRules()
        }
    }

    fun deleteAlertRule(ruleId: String) {
        viewModelScope.launch {
            model.deleteAlertRule(ruleId)
            loadAlertRules()
        }
    }

    fun saveConfiguration() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSavedFeedbackVisible = true) }
            delay(1200L)
            _uiState.update { it.copy(isSavedFeedbackVisible = false) }
        }
    }
}

fun formatThresholdValue(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        String.format(Locale.US, "%.1f", value)
    }
}

fun formatRuleLabel(type: AlertType, threshold: Double): String {
    return when (type) {
        AlertType.PRICE_CHANGE -> "Price Change \u00B1${formatThresholdValue(threshold)}%"
        AlertType.VOLUME_SPIKE -> "Volume Spike ${formatThresholdValue(threshold)}x"
        AlertType.EARNINGS_ANNOUNCEMENT -> "Earnings ${formatThresholdValue(threshold)} days before"
    }
}
