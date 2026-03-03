package ca.uwaterloo.market_lens.domain.repository

import ca.uwaterloo.market_lens.domain.model.AlertRule
import ca.uwaterloo.market_lens.domain.model.AlertType

interface AlertsRepository {
    suspend fun getAlertRules(): List<AlertRule>
    suspend fun upsertAlertRule(
        tickerKey: String,
        alertType: AlertType,
        threshold: Double,
        enabled: Boolean
    )
}
