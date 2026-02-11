package ca.uwaterloo.market_lens.ui.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.round
import java.util.Locale

class AlertsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AlertConfigUiState())
    val uiState = _uiState.asStateFlow()

    private val _savedConfiguration = MutableStateFlow<AlertConfiguration?>(null)
    val savedConfiguration = _savedConfiguration.asStateFlow()
    private var saveFeedbackJob: Job? = null

    fun onAlertTypeSelected(alertType: AlertType) {
        _uiState.update { it.copy(alertType = alertType, threshold = DEFAULT_THRESHOLD) }
    }

    fun onThresholdChanged(value: Float) {
        _uiState.update { state ->
            val clamped = value
                .coerceIn(state.alertType.minThreshold, state.alertType.maxThreshold)
                .roundToSingleDecimal()
            state.copy(threshold = clamped)
        }
    }

    fun onAlertsEnabledChanged(enabled: Boolean) {
        _uiState.update { it.copy(alertsEnabled = enabled) }
    }

    fun saveConfiguration() {
        val state = _uiState.value
        _savedConfiguration.value = AlertConfiguration(
            alertType = state.alertType,
            threshold = state.threshold,
            alertsEnabled = state.alertsEnabled
        )
        _uiState.update { it.copy(isSavedFeedbackVisible = true) }

        saveFeedbackJob?.cancel()
        saveFeedbackJob = viewModelScope.launch {
            delay(SAVE_FEEDBACK_DURATION_MS)
            _uiState.update { it.copy(isSavedFeedbackVisible = false) }
        }
    }

    fun Float.roundToSingleDecimal(): Float {
        return round(this * 10f) / 10f
    }

    companion object {
        private const val DEFAULT_THRESHOLD = 5f
        private const val SAVE_FEEDBACK_DURATION_MS = 1200L
    }
}

enum class AlertType(
    val label: String,
    val description: String,
    val minThreshold: Float,
    val maxThreshold: Float,
    val unitFormatter: (Float) -> String
) {
    PRICE_CHANGE(
        label = "Price Change",
        description = "Get notified when stock price changes significantly",
        minThreshold = 0f,
        maxThreshold = 20f,
        unitFormatter = { value -> "\u00B1${formatThresholdValue(value)}%" }
    ),
    VOLUME_SPIKE(
        label = "Volume Spike",
        description = "Get notified when trading volume spikes above normal",
        minThreshold = 0f,
        maxThreshold = 10f,
        unitFormatter = { value -> "${formatThresholdValue(value)}x normal" }
    ),
    EARNINGS_ANNOUNCEMENT(
        label = "Earnings Announcement",
        description = "Get notified about earnings announcements",
        minThreshold = 0f,
        maxThreshold = 30f,
        unitFormatter = { value -> "${formatThresholdValue(value)} days before" }
    )
}

data class AlertConfigUiState(
    val alertType: AlertType = AlertType.PRICE_CHANGE,
    val threshold: Float = 5f,
    val alertsEnabled: Boolean = true,
    val isSavedFeedbackVisible: Boolean = false
)

data class AlertConfiguration(
    val alertType: AlertType,
    val threshold: Float,
    val alertsEnabled: Boolean
)

fun formatThresholdValue(value: Float): String {
    return if (value % 1f == 0f) {
        value.toInt().toString()
    } else {
        String.format(Locale.US, "%.1f", value)
    }
}
