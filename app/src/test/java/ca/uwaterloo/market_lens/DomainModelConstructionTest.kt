package ca.uwaterloo.market_lens

import ca.uwaterloo.market_lens.domain.model.*
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class DomainModelConstructionTest {

    @Test
    fun constructs_aiExplanation_with_sentiment_and_confidence() {
        val explanation = AiExplanation(
            eventId = "evt-7",
            summary = "Earnings surprise likely drove the rally.",
            bullets = listOf("Revenue beat", "Guidance raised"),
            sentiment = Sentiment.BULLISH,
            confidence = 0.86
        )

        assertEquals("evt-7", explanation.eventId)
        assertEquals(Sentiment.BULLISH, explanation.sentiment)
        assertEquals(2, explanation.bullets.size)
        assertEquals(0.86, explanation.confidence, 0.0)
    }

    @Test
    fun constructs_alertRule_with_threshold_and_enabled_flag() {
        val rule = AlertRule(
            id = "rule-3",
            tickerKey = "AMZN",
            alertType = AlertType.PRICE_CHANGE,
            threshold = 3.5,
            enabled = true
        )

        assertEquals("rule-3", rule.id)
        assertEquals("AMZN", rule.tickerKey)
        assertEquals(AlertType.PRICE_CHANGE, rule.alertType)
        assertEquals(3.5, rule.threshold, 0.0)
    }

    @Test
    fun constructs_authState_data_variants_with_payloads() {
        val signedIn = AuthState.SignedIn(userId = "user-1", email = "u@example.com")
        val error = AuthState.Error(message = "Invalid credentials")

        assertEquals("user-1", signedIn.userId)
        assertEquals("u@example.com", signedIn.email)
        assertEquals("Invalid credentials", error.message)
    }

    @Test
    fun constructs_eventCause_with_rank_and_relevance() {
        val cause = EventCause(
            eventId = "evt-1",
            newsItemId = "news-99",
            rank = 1,
            title = "Major product launch announced",
            relevanceScore = 0.92,
            rationale = "Headline aligns with timing of the move"
        )

        assertEquals("evt-1", cause.eventId)
        assertEquals("news-99", cause.newsItemId)
        assertEquals(1, cause.rank)
        assertEquals(0.92, cause.relevanceScore, 0.0)
    }

    @Test
    fun constructs_marketEvent_with_expected_values() {
        val start = Instant.parse("2026-02-01T09:30:00Z")
        val detected = Instant.parse("2026-02-01T09:32:00Z")
        val event = MarketEvent(
            id = "evt-1",
            tickerKey = "MSFT",
            eventType = EventType.PRICE_SPIKE_UP,
            percentMove = 4.2,
            startTime = start,
            detectedAt = detected,
            priceBefore = 410.0,
            priceAfter = 427.22,
            briefDescription = "Strong move after guidance update"
        )

        assertEquals("evt-1", event.id)
        assertEquals(EventType.PRICE_SPIKE_UP, event.eventType)
        assertEquals(start, event.startTime)
        assertEquals(detected, event.detectedAt)
        assertEquals(427.22, event.priceAfter, 0.0)
    }

    @Test
    fun constructs_newsItem_with_expected_values() {
        val publishedAt = Instant.parse("2026-02-10T14:00:00Z")
        val source = NewsSource(
            name = "Reuters",
            url = "https://www.reuters.com",
            reliabilityScore = 0.95
        )
        val item = NewsItem(
            id = "news-100",
            tickerKey = "AAPL",
            title = "Apple launches new product line",
            source = source,
            publishedAt = publishedAt,
            url = "https://example.com/apple-launch",
            snippet = "Investors react positively to the announcement."
        )

        assertEquals("news-100", item.id)
        assertEquals("AAPL", item.tickerKey)
        assertEquals("Apple launches new product line", item.title)
        assertEquals(source, item.source)
        assertEquals(publishedAt, item.publishedAt)
        assertEquals("https://example.com/apple-launch", item.url)
        assertEquals("Investors react positively to the announcement.", item.snippet)
    }

    @Test
    fun constructs_newsSource_with_expected_values() {
        val source = NewsSource(
            name = "Bloomberg",
            url = "https://www.bloomberg.com",
            reliabilityScore = 0.9
        )

        assertEquals("Bloomberg", source.name)
        assertEquals("https://www.bloomberg.com", source.url)
        assertEquals(0.9, source.reliabilityScore ?: -1.0, 0.0)
    }

    @Test
    fun constructs_portfolio_with_positions() {
        val portfolio = Portfolio(
            id = "portfolio-1",
            ownerUserId = "user-1",
            positions = listOf(
                PortfolioPosition(tickerKey = "AAPL", shares = 0.5),
                PortfolioPosition(tickerKey = "NVDA", shares = 0.5)
            )
        )

        assertEquals("portfolio-1", portfolio.id)
        assertEquals("user-1", portfolio.ownerUserId)
        assertEquals(2, portfolio.positions.size)
    }

    @Test
    fun constructs_priceSeries_with_range_and_points() {
        val points = listOf(
            PricePoint(Instant.parse("2026-01-01T00:00:00Z"), 100.0),
            PricePoint(Instant.parse("2026-01-02T00:00:00Z"), 103.4)
        )
        val series = PriceSeries(
            tickerKey = "META",
            range = PriceRange.FIVE_DAYS,
            points = points
        )

        assertEquals("META", series.tickerKey)
        assertEquals(PriceRange.FIVE_DAYS, series.range)
        assertEquals(points, series.points)
    }

    @Test
    fun constructs_stockAnalysis_with_expected_values() {
        val analysis = StockAnalysis(
            tickerKey = "NFLX",
            summary = "Momentum remains positive.",
            sentiment = Sentiment.BULLISH,
            confidence = 0.72
        )

        assertEquals("NFLX", analysis.tickerKey)
        assertEquals("Momentum remains positive.", analysis.summary)
        assertEquals(Sentiment.BULLISH, analysis.sentiment)
        assertEquals(0.72, analysis.confidence, 0.0)
    }

    @Test
    fun constructs_stockQuote_with_expected_values() {
        val asOf = Instant.parse("2026-02-14T10:15:30Z")
        val quote = StockQuote(
            tickerKey = "TSLA",
            price = 245.22,
            changePercent = -1.9,
            asOf = asOf,
            volume = 1_230_000L,
            marketCap = 780_000_000_000L,
            peRatio = 61.2
        )

        assertEquals("TSLA", quote.tickerKey)
        assertEquals(245.22, quote.price, 0.0)
        assertEquals(-1.9, quote.changePercent, 0.0)
        assertEquals(asOf, quote.asOf)
        assertEquals(1_230_000L, quote.volume)
        assertEquals(780_000_000_000L, quote.marketCap)
        assertEquals(61.2, quote.peRatio ?: -1.0, 0.0)
    }

    @Test
    fun constructs_ticker_with_symbol_and_name() {
        val ticker = Ticker(
            symbol = "NVDA",
            name = "NVIDIA Corporation"
        )

        assertEquals("NVDA", ticker.symbol)
        assertEquals("NVIDIA Corporation", ticker.name)
        assertEquals("NVDA", ticker.tickerKey)
    }

    @Test
    fun constructs_user_with_expected_values() {
        val user = User(
            id = "u-42",
            email = "user@example.com",
            displayName = "Alex"
        )

        assertEquals("u-42", user.id)
        assertEquals("user@example.com", user.email)
        assertEquals("Alex", user.displayName)
    }
}
