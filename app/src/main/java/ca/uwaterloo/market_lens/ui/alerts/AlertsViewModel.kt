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
    val portfolioTickers: List<String> = emptyList(),
    val selectedTicker: String = "",
    val selectedType: AlertType = AlertType.PRICE_CHANGE,
    val threshold: Double = 5.0,
    val isLoading: Boolean = false,
    val isSavedFeedbackVisible: Boolean = false,
    val errorMessage: String? = null
)

class AlertsViewModel(
    private val model: MarketLensModel = AppGraph.model
) : ViewModel() {
    private val _uiState = MutableStateFlow(AlertConfigUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                val portfolio = model.getPortfolio()
                val tickers = portfolio.positions.map { it.tickerKey }
                val allRules = model.getAlertRules()

                // Filter rules to only show those in portfolio
                val filteredRules = allRules.filter { it.tickerKey in tickers }

                _uiState.update { it.copy(
                    alertRules = filteredRules,
                    portfolioTickers = tickers,
                    selectedTicker = if (tickers.isNotEmpty()) tickers[0] else "",
                    isLoading = false,
                    errorMessage = null
                ) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unable to load alerts."
                    )
                }
            }
        }
    }

    fun onTickerSelected(ticker: String) {
        _uiState.update { it.copy(selectedTicker = ticker) }
    }

    fun onTypeSelected(type: AlertType) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun onThresholdChanged(threshold: Double) {
        _uiState.update { it.copy(threshold = threshold) }
    }

    fun addAlert(onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val state = _uiState.value
                if (state.selectedTicker.isNotEmpty()) {
                    model.addAlertRule(
                        tickerKey = state.selectedTicker,
                        alertType = state.selectedType,
                        threshold = state.threshold,
                        enabled = true
                    )
                    _uiState.update { it.copy(isSavedFeedbackVisible = true, errorMessage = null) }
                    loadData()
                    delay(1200L)
                    _uiState.update { it.copy(isSavedFeedbackVisible = false) }
                    onComplete(true)
                } else {
                    _uiState.update { it.copy(errorMessage = "Please select a ticker.") }
                    onComplete(false)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSavedFeedbackVisible = false,
                        errorMessage = e.message ?: "Unable to create alert."
                    )
                }
                onComplete(false)
            }
        }
    }

    fun onAlertEnabledChanged(ruleId: String, enabled: Boolean) {
        viewModelScope.launch {
            try {
                val rule = _uiState.value.alertRules.find { it.id == ruleId } ?: return@launch
                model.editAlertRule(rule.copy(enabled = enabled))
                loadData()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Unable to update alert.")
                }
            }
        }
    }

    fun deleteAlertRule(ruleId: String) {
        viewModelScope.launch {
            try {
                model.deleteAlertRule(ruleId)
                loadData()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Unable to delete alert.")
                }
            }
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
