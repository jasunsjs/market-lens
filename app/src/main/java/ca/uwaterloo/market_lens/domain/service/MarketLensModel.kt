package ca.uwaterloo.market_lens.domain.service

import ca.uwaterloo.market_lens.domain.model.*
import kotlinx.coroutines.flow.StateFlow


/* Interface for domain models
- Provide screen level methods that ViewModels can call
- Contain minimal business logic
 */
interface MarketLensModel {

    // Auth
    val authState: StateFlow<AuthState>
    suspend fun login(email: String, password: String): AuthState
    suspend fun signUp(email: String, password: String): AuthState
    suspend fun logout()

    // Main View
    suspend fun getPortfolio(): Portfolio
    suspend fun addTickerToPortfolio(tickerKey: String)
    suspend fun removeTickerFromPortfolio(tickerKey: String)

    // Quotes for list + header
    suspend fun getQuote(tickerKey: String): StockQuote
    suspend fun refreshQuote(tickerKey: String): StockQuote

    // Stock detail
    suspend fun getPriceSeries(tickerKey: String, range: PriceRange): PriceSeries
    suspend fun getNewsByTicker(tickerKey: String): List<NewsItem>
    suspend fun getNewsItem(newsItemId: String): NewsItem
    suspend fun getStockAnalysis(tickerKey: String): StockAnalysis

    // Alerts
    suspend fun getAlertRules(): List<AlertRule>
    suspend fun addAlertRule(tickerKey: String, alertType: AlertType, threshold: Double, enabled: Boolean)
    suspend fun editAlertRule(rule: AlertRule)
    suspend fun deleteAlertRule(ruleId: String)

    // Events
    suspend fun getEvents(): List<MarketEvent>
    suspend fun getEventById(eventId: String): MarketEvent
    suspend fun getEventCauses(eventId: String): List<EventCause>

    // AI overview
    suspend fun getExplanation(eventId: String): AiExplanation
}
