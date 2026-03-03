package ca.uwaterloo.market_lens.data.mock

import ca.uwaterloo.market_lens.domain.model.AlertRule
import ca.uwaterloo.market_lens.domain.model.AlertType
import ca.uwaterloo.market_lens.domain.repository.AlertsRepository

class MockAlertsRepository : AlertsRepository {
    override suspend fun getAlertRules(): List<AlertRule> = MockDb.alertRules.toList()

    override suspend fun upsertAlertRule(
        tickerKey: String,
        alertType: AlertType,
        threshold: Double,
        enabled: Boolean
    ) {
        val index =
            MockDb.alertRules.indexOfFirst { it.tickerKey == tickerKey && it.alertType == alertType }
        val rule = AlertRule(
            id = "alert-$tickerKey-$alertType",
            tickerKey = tickerKey,
            alertType = alertType,
            threshold = threshold,
            enabled = enabled
        )
        if (index >= 0) MockDb.alertRules[index] = rule
        else MockDb.alertRules.add(rule)
    }
}
