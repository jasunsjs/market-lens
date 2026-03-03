package ca.uwaterloo.market_lens.data.mock

import ca.uwaterloo.market_lens.domain.model.*
import java.time.Instant
import java.time.temporal.ChronoUnit

object MockDb {

    // Quotes by tickerKey
    val quotes: MutableMap<String, StockQuote> = mutableMapOf(
        "AAPL" to StockQuote(
            "AAPL",
            190.12,
            -0.84,
            Instant.now(),
            volume = 45_000_000,
            marketCap = 2_900_000_000_000,
            peRatio = 29.1
        ),
        "MSFT" to StockQuote(
            "MSFT",
            410.44,
            1.22,
            Instant.now(),
            volume = 28_000_000,
            marketCap = 3_000_000_000_000,
            peRatio = 35.7
        ),
        "NVDA" to StockQuote(
            "NVDA",
            850.01,
            2.45,
            Instant.now(),
            volume = 52_000_000,
            marketCap = 2_100_000_000_000,
            peRatio = 70.3
        )
    )

    // Price series by (tickerKey, range)
    val series: MutableMap<Pair<String, PriceRange>, PriceSeries> = run {
        val now = Instant.now()
        fun genSeries(tickerKey: String, range: PriceRange, startPrice: Double): PriceSeries {
            val points = (0 until 30).map { i ->
                PricePoint(
                    timestamp = now.minus((29 - i).toLong(), ChronoUnit.DAYS),
                    close = startPrice + (i * 0.7) - (i % 5) // simple deterministic variation
                )
            }
            return PriceSeries(tickerKey, range, points)
        }

        mutableMapOf(
            ("AAPL" to PriceRange.ONE_MONTH) to genSeries("AAPL", PriceRange.ONE_MONTH, 175.0),
            ("MSFT" to PriceRange.ONE_MONTH) to genSeries("MSFT", PriceRange.ONE_MONTH, 395.0),
            ("NVDA" to PriceRange.ONE_MONTH) to genSeries("NVDA", PriceRange.ONE_MONTH, 780.0)
        )
    }

    val portfolioPositions: MutableList<PortfolioPosition> = mutableListOf()

    val events: MutableList<MarketEvent> = mutableListOf()

    val eventCauses: MutableList<EventCause> = mutableListOf()

    val newsItems: MutableList<NewsItem> = mutableListOf()

    val alertRules: MutableList<AlertRule> = mutableListOf()

    val aiExplanations: MutableMap<String, AiExplanation> = mutableMapOf()

    val stockAnalyses: MutableMap<String, StockAnalysis> = mutableMapOf()
}
