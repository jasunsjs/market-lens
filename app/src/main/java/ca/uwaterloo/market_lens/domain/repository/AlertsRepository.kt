package ca.uwaterloo.market_lens.domain.repository

import ca.uwaterloo.market_lens.domain.model.AlertRule
import ca.uwaterloo.market_lens.domain.model.AlertType

interface AlertsRepository {
    suspend fun getAlertRules(): List<AlertRule>
    suspend fun addAlertRule(tickerKey: String, alertType: AlertType, threshold: Double, enabled: Boolean)
    suspend fun editAlertRule(rule: AlertRule)
    suspend fun deleteAlertRule(ruleId: String)
}
