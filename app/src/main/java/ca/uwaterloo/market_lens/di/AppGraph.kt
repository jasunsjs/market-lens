package ca.uwaterloo.market_lens.di

import ca.uwaterloo.market_lens.data.mock.*
import ca.uwaterloo.market_lens.domain.repository.*
import ca.uwaterloo.market_lens.domain.service.*

object AppGraph {
    val authRepository: AuthRepository = MockAuthRepository()
    val portfolioRepository: PortfolioRepository = MockPortfolioRepository()
    val marketDataRepository: MarketDataRepository = MockMarketDataRepository()
    val eventsRepository: EventsRepository = MockEventsRepository()
    val alertsRepository: AlertsRepository = MockAlertsRepository()
    val newsRepository: NewsRepository = MockNewsRepository()
    val explanationRepository: ExplanationRepository = MockExplanationRepository()


    // Central domain model
    val model: MarketLensModel = MarketLensModelImpl(
        authRepository = authRepository,
        portfolioRepository = portfolioRepository,
        marketDataRepository = marketDataRepository,
        eventsRepository = eventsRepository,
        alertsRepository = alertsRepository,
        newsRepository = newsRepository,
        explanationRepository = explanationRepository,
    )
}