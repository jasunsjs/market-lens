package ca.uwaterloo.market_lens.data.mock

import ca.uwaterloo.market_lens.domain.model.AlertRule
import ca.uwaterloo.market_lens.domain.model.AlertType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class MockAlertsRepositoryTest {

    private lateinit var repository: MockAlertsRepository
    private lateinit var alertRulesSnapshot: List<AlertRule>

    @Before
    fun setUp() {
        repository = MockAlertsRepository()
        alertRulesSnapshot = MockDb.alertRules.toList()
    }

    @After
    fun tearDown() {
        MockDb.alertRules.clear()
        MockDb.alertRules.addAll(alertRulesSnapshot)
    }

    @Test
    fun getAlertRules_returns_snapshot_copy() = runBlocking {
        val rules = repository.getAlertRules()

        assertEquals(MockDb.alertRules, rules)

        MockDb.alertRules.add(
            AlertRule(
                id = "alert-temp",
                tickerKey = "SHOP",
                alertType = AlertType.PRICE_CHANGE,
                threshold = 4.0,
                enabled = true
            )
        )

        assertEquals(alertRulesSnapshot.size, rules.size)
    }

    @Test
    fun editAlertRule_updates_existing_rule_by_id() = runBlocking {
        val original = MockDb.alertRules.first()
        val edited = original.copy(
            tickerKey = "TSLA",
            alertType = AlertType.EARNINGS_ANNOUNCEMENT,
            threshold = original.threshold + 2.0,
            enabled = !original.enabled
        )

        repository.editAlertRule(edited)

        val stored = MockDb.alertRules.first { it.id == original.id }
        assertEquals(edited, stored)
        assertEquals(alertRulesSnapshot.size, MockDb.alertRules.size)
    }


    @Test
    fun deleteAlertRule_removes_all_rules_with_matching_id() = runBlocking {
        val duplicateId = "alert-duplicate"
        MockDb.alertRules.add(
            AlertRule(
                id = duplicateId,
                tickerKey = "AAPL",
                alertType = AlertType.PRICE_CHANGE,
                threshold = 3.0,
                enabled = true
            )
        )
        MockDb.alertRules.add(
            AlertRule(
                id = duplicateId,
                tickerKey = "MSFT",
                alertType = AlertType.VOLUME_SPIKE,
                threshold = 2.0,
                enabled = false
            )
        )

        repository.deleteAlertRule(duplicateId)

        assertTrue(MockDb.alertRules.none { it.id == duplicateId })
    }
}
