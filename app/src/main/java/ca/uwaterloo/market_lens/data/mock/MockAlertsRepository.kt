package ca.uwaterloo.market_lens.data.mock

import ca.uwaterloo.market_lens.domain.model.AlertRule
import ca.uwaterloo.market_lens.domain.model.AlertType
import ca.uwaterloo.market_lens.domain.repository.AlertsRepository

class MockAlertsRepository : AlertsRepository {
    override suspend fun getAlertRules(): List<AlertRule> = MockDb.alertRules.toList()

    override suspend fun addAlertRule(tickerKey: String, alertType: AlertType, threshold: Double, enabled: Boolean) {
        MockDb.alertRules.add(
            AlertRule(
                id = "alert-${System.currentTimeMillis()}",
                tickerKey = tickerKey,
                alertType = alertType,
                threshold = threshold,
                enabled = enabled
            )
        )
    }

    override suspend fun editAlertRule(rule: AlertRule) {
        val index = MockDb.alertRules.indexOfFirst { it.id == rule.id }
        if (index >= 0) MockDb.alertRules[index] = rule
    }

    override suspend fun deleteAlertRule(ruleId: String) {
        MockDb.alertRules.removeAll { it.id == ruleId }
    }
}
