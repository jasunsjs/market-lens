package ca.uwaterloo.market_lens.di

import ca.uwaterloo.market_lens.data.mock.*
import ca.uwaterloo.market_lens.domain.repository.*
import ca.uwaterloo.market_lens.domain.service.MarketLensModel

object AppGraph {
//    val authRepository: AuthRepository = MockAuthRepository()
//    val portfolioRepository: PortfolioRepository = MockPortfolioRepository()
    val marketDataRepository: MarketDataRepository = MockMarketDataRepository()
//    val eventsRepository: EventsRepository = MockEventsRepository()
    // UNCOMMENT THESE WHEN IMPLEMENTED

    // Central domain model
//    val model: MarketLensModel = MarketLensModelImpl(
//        authRepository = authRepository,
//        portfolioRepository = portfolioRepository,
//        marketDataRepository = marketDataRepository,
//        eventsRepository = eventsRepository,
//
//    )
}