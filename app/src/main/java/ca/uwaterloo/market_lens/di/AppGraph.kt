package ca.uwaterloo.market_lens.di

import ca.uwaterloo.market_lens.data.mock.*
import ca.uwaterloo.market_lens.data.supabase.SupabaseAlertsRepository
import ca.uwaterloo.market_lens.data.supabase.SupabaseAuthRepository
import ca.uwaterloo.market_lens.data.supabase.SupabaseEventsRepository
import ca.uwaterloo.market_lens.data.supabase.SupabaseExplanationRepository
import ca.uwaterloo.market_lens.data.supabase.SupabasePortfolioRepository
import ca.uwaterloo.market_lens.domain.repository.*
import ca.uwaterloo.market_lens.domain.service.*

object AppGraph {
    val authRepository: AuthRepository = SupabaseAuthRepository()
    val portfolioRepository: PortfolioRepository = SupabasePortfolioRepository()
    val marketDataRepository: MarketDataRepository = MockMarketDataRepository()
    val eventsRepository: EventsRepository = MockEventsRepository()
    val alertsRepository: AlertsRepository = SupabaseAlertsRepository()
    val newsRepository: NewsRepository = MockNewsRepository()
    val explanationRepository: ExplanationRepository = SupabaseExplanationRepository()


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
