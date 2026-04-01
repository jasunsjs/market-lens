package ca.uwaterloo.market_lens.data.supabase

import ca.uwaterloo.market_lens.domain.model.Portfolio
import ca.uwaterloo.market_lens.domain.model.PortfolioPosition
import ca.uwaterloo.market_lens.domain.repository.PortfolioRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class SupabasePortfolioRepository : PortfolioRepository {
    private val client = SupabaseClientProvider.client

    override suspend fun getPortfolio(): Portfolio {
        val userId = client.requireCurrentUserId()
        val portfolio = findPortfolioForCurrentUser(userId)
            ?: return Portfolio(
                id = "",
                ownerUserId = userId,
                positions = emptyList()
            )

        val positions = client.from("portfolio_positions")
            .select {
                filter {
                    eq("portfolio_id", portfolio.id)
                }
                order("added_at", Order.ASCENDING)
            }
            .decodeList<PortfolioPositionRow>()
            .map { it.toDomain() }

        return portfolio.toDomain(positions)
    }

    override suspend fun addTicker(tickerKey: String) {
        val portfolio = ensurePortfolioForCurrentUser()

        client.from("portfolio_positions").upsert(
            PortfolioPositionInsert(portfolioId = portfolio.id, tickerKey = tickerKey)
        ) {
            onConflict = "portfolio_id,ticker_key"
            ignoreDuplicates = true
        }
    }

    override suspend fun removeTicker(tickerKey: String) {
        val portfolio = ensurePortfolioForCurrentUser()

        client.from("portfolio_positions").delete {
            filter {
                eq("portfolio_id", portfolio.id)
                eq("ticker_key", tickerKey)
            }
        }
    }

    override suspend fun updateShares(tickerKey: String, shares: Double, avgCost: Double?) {
        val portfolio = ensurePortfolioForCurrentUser()

        client.from("portfolio_positions").update(
            mapOf("weight" to shares, "avg_cost" to avgCost)
        ) {
            filter {
                eq("portfolio_id", portfolio.id)
                eq("ticker_key", tickerKey)
            }
        }
    }

    private suspend fun ensurePortfolioForCurrentUser(): PortfolioRow {
        val userId = client.requireCurrentUserId()

        return findPortfolioForCurrentUser(userId)
            ?: client.from("portfolios")
                .insert(PortfolioInsert(ownerUserId = userId)) {
                    select()
                }
                .decodeSingle<PortfolioRow>()
    }

    private suspend fun findPortfolioForCurrentUser(userId: String): PortfolioRow? {
        return client.from("portfolios")
            .select {
                filter {
                    eq("owner_user_id", userId)
                }
                order("created_at", Order.ASCENDING)
                limit(1)
            }
            .decodeSingleOrNull<PortfolioRow>()
    }
}

@Serializable
private data class PortfolioRow(
    val id: String,
    @SerialName("owner_user_id")
    val ownerUserId: String,
    @SerialName("created_at")
    val createdAt: String? = null
) {
    fun toDomain(positions: List<PortfolioPosition>): Portfolio =
        Portfolio(
            id = id,
            ownerUserId = ownerUserId,
            positions = positions
        )
}

@Serializable
private data class PortfolioInsert(
    @SerialName("owner_user_id")
    val ownerUserId: String
)

@Serializable
private data class PortfolioPositionRow(
    val id: String,
    @SerialName("portfolio_id")
    val portfolioId: String,
    @SerialName("ticker_key")
    val tickerKey: String,
    val weight: Double? = null,
    @SerialName("avg_cost")
    val avgCost: Double? = null,
    @SerialName("added_at")
    val addedAt: String? = null
) {
    fun toDomain(): PortfolioPosition =
        PortfolioPosition(
            tickerKey = tickerKey,
            shares = weight,
            avgCost = avgCost
        )
}

@Serializable
private data class PortfolioPositionInsert(
    @SerialName("portfolio_id")
    val portfolioId: String,
    @SerialName("ticker_key")
    val tickerKey: String
)
