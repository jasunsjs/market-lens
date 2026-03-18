package ca.uwaterloo.market_lens.data.supabase

import ca.uwaterloo.market_lens.domain.model.AlertRule
import ca.uwaterloo.market_lens.domain.model.AlertType
import ca.uwaterloo.market_lens.domain.repository.AlertsRepository
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class SupabaseAlertsRepository : AlertsRepository {
    private val client = SupabaseClientProvider.client

    override suspend fun getAlertRules(): List<AlertRule> {
        val userId = client.requireCurrentUserId()

        return client.from("alert_rules")
            .select {
                filter {
                    eq("user_id", userId)
                }
                order("created_at", Order.DESCENDING)
            }
            .decodeList<AlertRuleRow>()
            .map { it.toDomain() }
    }

    override suspend fun addAlertRule(tickerKey: String, alertType: AlertType, threshold: Double, enabled: Boolean) {
        val userId = client.requireCurrentUserId()

        client.from("alert_rules").insert(
            AlertRuleInsert(
                userId = userId,
                tickerKey = tickerKey,
                alertType = alertType.name,
                threshold = threshold,
                enabled = enabled
            )
        )
    }

    override suspend fun editAlertRule(rule: AlertRule) {
        val userId = client.requireCurrentUserId()

        client.from("alert_rules").update(
            AlertRuleUpdate(
                tickerKey = rule.tickerKey,
                alertType = rule.alertType.name,
                threshold = rule.threshold,
                enabled = rule.enabled
            )
        ) {
            filter {
                eq("id", rule.id)
                eq("user_id", userId)
            }
        }
    }

    override suspend fun deleteAlertRule(ruleId: String) {
        val userId = client.requireCurrentUserId()

        client.from("alert_rules").delete {
            filter {
                eq("id", ruleId)
                eq("user_id", userId)
            }
        }
    }
}

@Serializable
private data class AlertRuleRow(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("ticker_key")
    val tickerKey: String,
    @SerialName("alert_type")
    val alertType: String,
    val threshold: Double,
    val enabled: Boolean,
    @SerialName("created_at")
    val createdAt: String? = null
) {
    fun toDomain(): AlertRule =
        AlertRule(
            id = id,
            tickerKey = tickerKey,
            alertType = AlertType.valueOf(alertType),
            threshold = threshold,
            enabled = enabled
        )
}

@Serializable
private data class AlertRuleInsert(
    @SerialName("user_id")
    val userId: String,
    @SerialName("ticker_key")
    val tickerKey: String,
    @SerialName("alert_type")
    val alertType: String,
    val threshold: Double,
    val enabled: Boolean
)

@Serializable
private data class AlertRuleUpdate(
    @SerialName("ticker_key")
    val tickerKey: String,
    @SerialName("alert_type")
    val alertType: String,
    val threshold: Double,
    val enabled: Boolean
)
