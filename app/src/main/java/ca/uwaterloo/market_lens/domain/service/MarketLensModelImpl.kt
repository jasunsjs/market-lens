//package ca.uwaterloo.market_lens.domain.service
//
//import ca.uwaterloo.market_lens.domain.model.*
//import ca.uwaterloo.market_lens.domain.repository.*
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//
//class MarketLensModelImpl(
////    private val authRepository: AuthRepository,
////    private val portfolioRepository: PortfolioRepository,
////    private val marketDataRepository: MarketDataRepository,
////    private val alertsRepository: AlertsRepository,
////    private val newsRepository: NewsRepository,
////    private val eventsRepository: EventsRepository,
////    private val explanationRepository: ExplanationRepository
//) : MarketLensModel {
//
//    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
//    override val authState: StateFlow<AuthState> = _authState.asStateFlow()
//
//    init {
//        // Sprint 2: set to SignedOut immediately (or load from mock)
//        _authState.value = AuthState.SignedOut
//    }
//
//    override suspend fun login(email: String, password: String) {
//        _authState.value = AuthState.Loading
//        _authState.value = authRepository.login(email, password)
//    }
//
//    override suspend fun logout() {
//        authRepository.logout()
//        _authState.value = AuthState.SignedOut
//    }
//
//    override suspend fun getAvailableTickers(): List<Ticker> =
//        portfolioRepository.getAvailableTickers()
//
//    override suspend fun getPortfolio(): Portfolio =
//        portfolioRepository.getPortfolio()
//
//    override suspend fun addTickerToPortfolio(tickerKey: String) {
//        portfolioRepository.addTicker(tickerKey)
//    }
//
//    override suspend fun removeTickerFromPortfolio(tickerKey: String) {
//        portfolioRepository.removeTicker(tickerKey)
//    }
//
//    override suspend fun getQuote(tickerKey: String): StockQuote =
//        marketDataRepository.getQuote(tickerKey)
//
//    override suspend fun refreshQuote(tickerKey: String): StockQuote =
//        marketDataRepository.getQuote(tickerKey) // same for mock
//
//    override suspend fun getPriceSeries(tickerKey: String, range: PriceRange): PriceSeries =
//        marketDataRepository.getPriceSeries(tickerKey, range)
//
//    override suspend fun getNewsByTicker(tickerKey: String): List<NewsItem> =
//        newsRepository.getNewsByTicker(tickerKey)
//
//    override suspend fun getAlertRules(): List<AlertRule> =
//        alertsRepository.getAlertRules()
//
//    override suspend fun addAlertRule(tickerKey: String, alertType: AlertType, threshold: Double, enabled: Boolean) {
//        alertsRepository.addAlertRule(tickerKey, alertType, threshold, enabled)
//    }
//
//    override suspend fun editAlertRule(rule: AlertRule) {
//        alertsRepository.editAlertRule(rule)
//    }
//
//    override suspend fun deleteAlertRule(ruleId: String) {
//        alertsRepository.deleteAlertRule(ruleId)
//    }
//
//    override suspend fun getEvents(): List<MarketEvent> =
//        eventsRepository.getEvents()
//
//    override suspend fun getEventById(eventId: String): MarketEvent =
//        eventsRepository.getEventById(eventId)
//
//    override suspend fun getEventCauses(eventId: String): List<EventCause> =
//        eventsRepository.getEventCauses(eventId)
//
//    override suspend fun getExplanation(eventId: String): AiExplanation =
//        explanationRepository.getExplanation(eventId)
//}