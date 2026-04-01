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
        ),
        "GOOGL" to StockQuote(
            "GOOGL",
            175.50,
            1.85,
            Instant.now(),
            volume = 22_000_000,
            marketCap = 2_200_000_000_000,
            peRatio = 23.4
        ),
        "AMD" to StockQuote(
            "AMD",
            132.45,
            -1.20,
            Instant.now(),
            volume = 38_000_000,
            marketCap = 214_000_000_000,
            peRatio = 42.5
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
            ("NVDA" to PriceRange.ONE_MONTH) to genSeries("NVDA", PriceRange.ONE_MONTH, 780.0),
            ("GOOGL" to PriceRange.ONE_MONTH) to genSeries("GOOGL", PriceRange.ONE_MONTH, 163.0),
            ("AMD" to PriceRange.ONE_MONTH) to genSeries("AMD", PriceRange.ONE_MONTH, 118.0)
        )
    }

    val portfolioPositions: MutableList<PortfolioPosition> = mutableListOf(
        PortfolioPosition(tickerKey = "AAPL", shares = 30.0),
        PortfolioPosition(tickerKey = "MSFT", shares = 50.0),
        PortfolioPosition(tickerKey = "NVDA", shares = 20.0)
    )

    val newsItems: MutableList<NewsItem> = mutableListOf(
        // AAPL - general (stock screen)
        NewsItem(
            id = "news-aapl-1",
            tickerKey = "AAPL",
            title = "AAPL Announces New Product Line",
            source = NewsSource(name = "Bloomberg", url = "https://www.bloomberg.com/canada", reliabilityScore = 0.90),
            publishedAt = Instant.now().minus(2, ChronoUnit.HOURS),
            url = "https://www.bloomberg.com/canada",
            snippet = "Company reveals innovative solutions targeting enterprise customers, expected to drive Q4 revenue growth."
        ),
        NewsItem(
            id = "news-aapl-2",
            tickerKey = "AAPL",
            title = "Analyst Upgrades AAPL Price Target",
            source = NewsSource(name = "Reuters", url = "https://www.reuters.com/", reliabilityScore = 0.92),
            publishedAt = Instant.now().minus(5, ChronoUnit.HOURS),
            url = "https://www.reuters.com/",
            snippet = "Major investment bank raises price target by 15% citing strong fundamentals and market position."
        ),
        NewsItem(
            id = "news-aapl-3",
            tickerKey = "AAPL",
            title = "Sector Faces Regulatory Scrutiny",
            source = NewsSource(name = "Financial Times", url = "https://www.ft.com/", reliabilityScore = 0.91),
            publishedAt = Instant.now().minus(1, ChronoUnit.DAYS),
            url = "https://www.ft.com/",
            snippet = "New proposed regulations could impact operations, though long-term effects remain uncertain."
        ),
        NewsItem(
            id = "news-aapl-4",
            tickerKey = "AAPL",
            title = "Q3 Earnings Beat Expectations",
            source = NewsSource(name = "CNBC", url = "https://www.cnbc.com/", reliabilityScore = 0.85),
            publishedAt = Instant.now().minus(3, ChronoUnit.DAYS),
            url = "https://www.cnbc.com/",
            snippet = "Revenue exceeded analyst forecasts with strong performance across all business segments."
        ),
        // AAPL - event-cause items (referenced by evt-aapl-1)
        NewsItem(
            id = "news-aapl-5",
            tickerKey = "AAPL",
            title = "Q4 Earnings Miss Analyst Expectations",
            source = NewsSource(name = "Financial Times", url = "https://www.ft.com/", reliabilityScore = 0.91),
            publishedAt = Instant.now().minus(35, ChronoUnit.DAYS),
            url = "https://www.ft.com/",
            snippet = "Q4 earnings per share came in at \$2.10 vs. the expected \$2.18, representing a significant miss that triggered immediate selling pressure."
        ),
        NewsItem(
            id = "news-aapl-6",
            tickerKey = "AAPL",
            title = "Apple China Sales Decline 11% YoY",
            source = NewsSource(name = "Bloomberg", url = "https://www.bloomberg.com/canada", reliabilityScore = 0.90),
            publishedAt = Instant.now().minus(35, ChronoUnit.DAYS),
            url = "https://www.bloomberg.com/canada",
            snippet = "iPhone sales in Greater China dropped 11% year-over-year amid increasing competition from domestic smartphone manufacturers."
        ),
        NewsItem(
            id = "news-aapl-7",
            tickerKey = "AAPL",
            title = "Apple Q1 Guidance Falls Below Consensus",
            source = NewsSource(name = "Reuters", url = "https://www.reuters.com/", reliabilityScore = 0.92),
            publishedAt = Instant.now().minus(35, ChronoUnit.DAYS),
            url = "https://www.reuters.com/",
            snippet = "Management projected Q1 revenue of \$117-122B, below the \$124B consensus, citing macroeconomic uncertainty and currency headwinds."
        ),
        // MSFT - general (stock screen)
        NewsItem(
            id = "news-msft-1",
            tickerKey = "MSFT",
            title = "Microsoft Azure Growth Beats Estimates",
            source = NewsSource(name = "Bloomberg", url = "https://www.bloomberg.com/canada", reliabilityScore = 0.90),
            publishedAt = Instant.now().minus(3, ChronoUnit.HOURS),
            url = "https://www.bloomberg.com/canada",
            snippet = "Azure cloud revenue surged 31% year-over-year, exceeding analyst estimates and reinforcing Microsoft's cloud leadership."
        ),
        NewsItem(
            id = "news-msft-2",
            tickerKey = "MSFT",
            title = "Copilot Adoption Accelerates Across Enterprise",
            source = NewsSource(name = "Reuters", url = "https://www.reuters.com/", reliabilityScore = 0.92),
            publishedAt = Instant.now().minus(1, ChronoUnit.DAYS),
            url = "https://www.reuters.com/",
            snippet = "Enterprise AI assistant adoption reached record levels, with Copilot seat growth outpacing initial projections by a wide margin."
        ),
        NewsItem(
            id = "news-msft-3",
            tickerKey = "MSFT",
            title = "Antitrust Review Targets Cloud Dominance",
            source = NewsSource(name = "Financial Times", url = "https://www.ft.com/", reliabilityScore = 0.91),
            publishedAt = Instant.now().minus(4, ChronoUnit.DAYS),
            url = "https://www.ft.com/",
            snippet = "Regulators open inquiry into Microsoft's bundling of cloud and productivity services, raising concerns over competitive practices."
        ),
        // MSFT - event-cause items (referenced by evt-msft-1)
        NewsItem(
            id = "news-msft-4",
            tickerKey = "MSFT",
            title = "Microsoft Q2 Earnings Smash Estimates on Cloud Strength",
            source = NewsSource(name = "Financial Times", url = "https://www.ft.com/", reliabilityScore = 0.91),
            publishedAt = Instant.now().minus(20, ChronoUnit.DAYS),
            url = "https://www.ft.com/",
            snippet = "Microsoft reported Q2 EPS of \$3.23 vs. the \$3.11 consensus, with revenue coming in at \$62.0B against expectations of \$60.9B, driven by Azure outperformance."
        ),
        NewsItem(
            id = "news-msft-5",
            tickerKey = "MSFT",
            title = "Azure Cloud Growth Accelerates to 33% Year-Over-Year",
            source = NewsSource(name = "Bloomberg", url = "https://www.bloomberg.com/canada", reliabilityScore = 0.90),
            publishedAt = Instant.now().minus(20, ChronoUnit.DAYS),
            url = "https://www.bloomberg.com/canada",
            snippet = "Azure revenue growth re-accelerated to 33% this quarter, surpassing the 29% posted last quarter and well ahead of analyst estimates of 30%, signalling continued AI-driven cloud demand."
        ),
        NewsItem(
            id = "news-msft-6",
            tickerKey = "MSFT",
            title = "Copilot Enterprise Seats Triple Quarter Over Quarter",
            source = NewsSource(name = "Reuters", url = "https://www.reuters.com/", reliabilityScore = 0.92),
            publishedAt = Instant.now().minus(20, ChronoUnit.DAYS),
            url = "https://www.reuters.com/",
            snippet = "Microsoft's AI productivity suite saw enterprise seat count triple in a single quarter, with average revenue per user rising as organizations expand Copilot deployments."
        ),
        // NVDA - general (stock screen)
        NewsItem(
            id = "news-nvda-1",
            tickerKey = "NVDA",
            title = "NVDA Blackwell Chip Demand Outpaces Supply",
            source = NewsSource(name = "Bloomberg", url = "https://www.bloomberg.com/canada", reliabilityScore = 0.90),
            publishedAt = Instant.now().minus(1, ChronoUnit.HOURS),
            url = "https://www.bloomberg.com/canada",
            snippet = "Hyperscalers and cloud providers are placing orders months in advance as Blackwell GPU allocation remains constrained through mid-year."
        ),
        NewsItem(
            id = "news-nvda-2",
            tickerKey = "NVDA",
            title = "Data Center Revenue Hits Record High",
            source = NewsSource(name = "CNBC", url = "https://www.cnbc.com/", reliabilityScore = 0.85),
            publishedAt = Instant.now().minus(2, ChronoUnit.DAYS),
            url = "https://www.cnbc.com/",
            snippet = "NVIDIA's data center segment posted a record quarter, driven by insatiable demand for AI training infrastructure worldwide."
        ),
        NewsItem(
            id = "news-nvda-3",
            tickerKey = "NVDA",
            title = "Export Controls May Limit China Sales",
            source = NewsSource(name = "Reuters", url = "https://www.reuters.com/", reliabilityScore = 0.92),
            publishedAt = Instant.now().minus(5, ChronoUnit.DAYS),
            url = "https://www.reuters.com/",
            snippet = "Expanded U.S. chip export restrictions could reduce NVIDIA's addressable market in China, a region that contributed significantly to past revenues."
        ),
        // NVDA - event-cause items (referenced by evt-nvda-1)
        NewsItem(
            id = "news-nvda-4",
            tickerKey = "NVDA",
            title = "NVDA Q4 Data Center Revenue Hits \$35.6B, Doubles Year-Over-Year",
            source = NewsSource(name = "Bloomberg", url = "https://www.bloomberg.com/canada", reliabilityScore = 0.90),
            publishedAt = Instant.now().minus(14, ChronoUnit.DAYS),
            url = "https://www.bloomberg.com/canada",
            snippet = "NVIDIA's data center segment delivered \$35.6B in Q4, more than doubling year-over-year, as hyperscalers continued ramping Blackwell cluster deployments ahead of schedule."
        ),
        NewsItem(
            id = "news-nvda-5",
            tickerKey = "NVDA",
            title = "Blackwell GPU Backlog Extends to 12 Months as Demand Overwhelms Supply",
            source = NewsSource(name = "CNBC", url = "https://www.cnbc.com/", reliabilityScore = 0.85),
            publishedAt = Instant.now().minus(14, ChronoUnit.DAYS),
            url = "https://www.cnbc.com/",
            snippet = "TSMC and NVIDIA confirmed the Blackwell order backlog now stretches beyond 12 months, with allocations for 2025 fully committed and early 2026 slots already filling."
        ),
        NewsItem(
            id = "news-nvda-6",
            tickerKey = "NVDA",
            title = "Jensen Huang Unveils Rubin Next-Gen GPU Architecture at GTC",
            source = NewsSource(name = "Reuters", url = "https://www.reuters.com/", reliabilityScore = 0.92),
            publishedAt = Instant.now().minus(14, ChronoUnit.DAYS),
            url = "https://www.reuters.com/",
            snippet = "NVIDIA CEO Jensen Huang announced the Rubin GPU architecture at GTC, targeting a 2026 release and promising a 3x performance leap over Blackwell, extending NVIDIA's multi-year roadmap advantage."
        ),
        // GOOGL - general (stock screen)
        NewsItem(
            id = "news-googl-1",
            tickerKey = "GOOGL",
            title = "Google Search Ad Revenue Surges on AI-Enhanced Results",
            source = NewsSource(name = "Bloomberg", url = "https://www.bloomberg.com/canada", reliabilityScore = 0.90),
            publishedAt = Instant.now().minus(4, ChronoUnit.HOURS),
            url = "https://www.bloomberg.com/canada",
            snippet = "Search advertising revenue grew 14% year-over-year as AI Overviews increased user engagement and click-through rates on premium ad placements."
        ),
        NewsItem(
            id = "news-googl-2",
            tickerKey = "GOOGL",
            title = "Gemini Integration Drives Google Cloud Growth to 28%",
            source = NewsSource(name = "CNBC", url = "https://www.cnbc.com/", reliabilityScore = 0.85),
            publishedAt = Instant.now().minus(2, ChronoUnit.DAYS),
            url = "https://www.cnbc.com/",
            snippet = "Google Cloud revenue grew 28% year-over-year, with Gemini-powered enterprise offerings cited as a key driver of new contract wins against AWS and Azure."
        ),
        NewsItem(
            id = "news-googl-3",
            tickerKey = "GOOGL",
            title = "DOJ Antitrust Case Advances to Remedy Phase",
            source = NewsSource(name = "Reuters", url = "https://www.reuters.com/", reliabilityScore = 0.92),
            publishedAt = Instant.now().minus(6, ChronoUnit.DAYS),
            url = "https://www.reuters.com/",
            snippet = "A federal judge ruled against Google in the search monopoly case, advancing proceedings to the remedy phase where structural remedies including potential divestiture of Chrome remain on the table."
        ),
        NewsItem(
            id = "news-googl-4",
            tickerKey = "GOOGL",
            title = "Waymo Robotaxi Expansion to 10 New Cities Announced",
            source = NewsSource(name = "Financial Times", url = "https://www.ft.com/", reliabilityScore = 0.91),
            publishedAt = Instant.now().minus(8, ChronoUnit.DAYS),
            url = "https://www.ft.com/",
            snippet = "Alphabet's Waymo unit announced plans to expand autonomous ride-hailing operations to 10 additional U.S. cities by end of 2025, with international pilots planned for 2026."
        ),
        // AMD - general (stock screen)
        NewsItem(
            id = "news-amd-1",
            tickerKey = "AMD",
            title = "AMD Instinct MI300X Gains Traction in AI Inference Market",
            source = NewsSource(name = "Bloomberg", url = "https://www.bloomberg.com/canada", reliabilityScore = 0.90),
            publishedAt = Instant.now().minus(3, ChronoUnit.HOURS),
            url = "https://www.bloomberg.com/canada",
            snippet = "Several major cloud providers have begun deploying MI300X at scale for inference workloads, signalling AMD is starting to carve out a foothold in a market long dominated by NVIDIA."
        ),
        NewsItem(
            id = "news-amd-2",
            tickerKey = "AMD",
            title = "AMD CPU Market Share Hits Decade High Against Intel",
            source = NewsSource(name = "Reuters", url = "https://www.reuters.com/", reliabilityScore = 0.92),
            publishedAt = Instant.now().minus(1, ChronoUnit.DAYS),
            url = "https://www.reuters.com/",
            snippet = "AMD's Ryzen and EPYC product lines pushed x86 CPU market share to its highest level in over a decade, with server CPU gains particularly strong as hyperscalers diversify away from Intel."
        ),
        NewsItem(
            id = "news-amd-3",
            tickerKey = "AMD",
            title = "RDNA 4 GPU Launch Targets Mid-Range Gaming With Strong Reviews",
            source = NewsSource(name = "CNBC", url = "https://www.cnbc.com/", reliabilityScore = 0.85),
            publishedAt = Instant.now().minus(3, ChronoUnit.DAYS),
            url = "https://www.cnbc.com/",
            snippet = "The Radeon RX 9070 XT launched to strong critical reception, with reviewers praising its price-to-performance ratio and positioning it as a compelling alternative to NVIDIA's mid-range offerings."
        ),
        NewsItem(
            id = "news-amd-4",
            tickerKey = "AMD",
            title = "AMD Acquires Nod.ai to Strengthen Software Ecosystem",
            source = NewsSource(name = "Financial Times", url = "https://www.ft.com/", reliabilityScore = 0.91),
            publishedAt = Instant.now().minus(7, ChronoUnit.DAYS),
            url = "https://www.ft.com/",
            snippet = "The acquisition of AI compiler startup Nod.ai is expected to accelerate ROCm software maturity, addressing one of the key adoption barriers that has kept enterprise customers on NVIDIA's CUDA platform."
        ),
        // AMD - event-cause items (referenced by evt-amd-1)
        NewsItem(
            id = "news-amd-5",
            tickerKey = "AMD",
            title = "AMD Q4 Data Center GPU Revenue Misses as Hyperscalers Favour NVIDIA",
            source = NewsSource(name = "Financial Times", url = "https://www.ft.com/", reliabilityScore = 0.91),
            publishedAt = Instant.now().minus(25, ChronoUnit.DAYS),
            url = "https://www.ft.com/",
            snippet = "AMD's data center GPU segment posted \$2.3B in Q4 revenue, falling short of the \$2.8B analyst consensus, as hyperscalers continued to prioritise NVIDIA Blackwell allocations over MI300X deployments."
        ),
        NewsItem(
            id = "news-amd-6",
            tickerKey = "AMD",
            title = "AMD CFO: MI300X Ramp Slower Than Expected Due to Software Friction",
            source = NewsSource(name = "Bloomberg", url = "https://www.bloomberg.com/canada", reliabilityScore = 0.90),
            publishedAt = Instant.now().minus(25, ChronoUnit.DAYS),
            url = "https://www.bloomberg.com/canada",
            snippet = "In the post-earnings call, AMD's CFO acknowledged that MI300X adoption is being slowed by customers' reluctance to port CUDA-optimised workloads to ROCm, pushing meaningful revenue recognition into the second half of 2025."
        ),
        NewsItem(
            id = "news-amd-7",
            tickerKey = "AMD",
            title = "AMD Reduces Full-Year Data Center GPU Revenue Outlook",
            source = NewsSource(name = "Reuters", url = "https://www.reuters.com/", reliabilityScore = 0.92),
            publishedAt = Instant.now().minus(25, ChronoUnit.DAYS),
            url = "https://www.reuters.com/",
            snippet = "AMD trimmed its full-year data center GPU revenue forecast from \$5.0B to \$4.5B, citing a slower-than-expected transition of cloud workloads to its MI300X platform and intensifying competition from NVIDIA's Blackwell lineup."
        ),
        // SHOP - event-cause items (referenced by evt-shop-1)
        NewsItem(
            id = "news-shop-1",
            tickerKey = "SHOP",
            title = "Fed Signals Extended Rate Hikes Through Mid-2026",
            source = NewsSource(name = "Wall Street Journal", url = "https://www.wsj.com/", reliabilityScore = 0.93),
            publishedAt = Instant.now().minus(35, ChronoUnit.DAYS),
            url = "https://www.wsj.com/",
            snippet = "The Federal Reserve indicated potential rate hikes could extend through mid-2026, dampening enthusiasm for high-multiple growth stocks."
        ),
        NewsItem(
            id = "news-shop-2",
            tickerKey = "SHOP",
            title = "January Retail Sales Fall 0.8%, Largest Drop in a Year",
            source = NewsSource(name = "CNBC", url = "https://www.cnbc.com/", reliabilityScore = 0.85),
            publishedAt = Instant.now().minus(35, ChronoUnit.DAYS),
            url = "https://www.cnbc.com/",
            snippet = "January retail sales fell 0.8%, the largest monthly decline in over a year, raising concerns about consumer spending on e-commerce platforms."
        ),
        NewsItem(
            id = "news-shop-3",
            tickerKey = "SHOP",
            title = "Institutional Rotation Out of High-Growth Tech Names",
            source = NewsSource(name = "Bloomberg", url = "https://www.bloomberg.com/canada", reliabilityScore = 0.90),
            publishedAt = Instant.now().minus(35, ChronoUnit.DAYS),
            url = "https://www.bloomberg.com/canada",
            snippet = "Institutional investors rotated capital from high-growth tech into defensive sectors, with the growth-to-value ratio hitting a 6-month low."
        )
    )

    val events: MutableList<MarketEvent> = mutableListOf(
        MarketEvent(
            id = "evt-aapl-1",
            tickerKey = "AAPL",
            eventType = EventType.PRICE_SPIKE_DOWN,
            percentMove = -2.8,
            startTime = Instant.now().minus(35, ChronoUnit.DAYS),
            detectedAt = Instant.now().minus(35, ChronoUnit.DAYS),
            priceBefore = 195.0,
            priceAfter = 189.5,
            briefDescription = "Likely driven by earnings miss and market reaction"
        ),
        MarketEvent(
            id = "evt-shop-1",
            tickerKey = "SHOP",
            eventType = EventType.PRICE_SPIKE_DOWN,
            percentMove = -6.8,
            startTime = Instant.now().minus(35, ChronoUnit.DAYS),
            detectedAt = Instant.now().minus(35, ChronoUnit.DAYS),
            priceBefore = 93.6,
            priceAfter = 87.2,
            briefDescription = "Macroeconomic data impact and Fed policy signals"
        ),
        MarketEvent(
            id = "evt-msft-1",
            tickerKey = "MSFT",
            eventType = EventType.PRICE_SPIKE_UP,
            percentMove = 4.2,
            startTime = Instant.now().minus(20, ChronoUnit.DAYS),
            detectedAt = Instant.now().minus(20, ChronoUnit.DAYS),
            priceBefore = 394.0,
            priceAfter = 410.5,
            briefDescription = "Strong Q2 earnings and Azure re-acceleration drove the rally"
        ),
        MarketEvent(
            id = "evt-nvda-1",
            tickerKey = "NVDA",
            eventType = EventType.PRICE_SPIKE_UP,
            percentMove = 8.5,
            startTime = Instant.now().minus(14, ChronoUnit.DAYS),
            detectedAt = Instant.now().minus(14, ChronoUnit.DAYS),
            priceBefore = 783.0,
            priceAfter = 849.6,
            briefDescription = "Record data center revenue and next-gen GPU roadmap announcement"
        ),
        MarketEvent(
            id = "evt-amd-1",
            tickerKey = "AMD",
            eventType = EventType.PRICE_SPIKE_DOWN,
            percentMove = -5.1,
            startTime = Instant.now().minus(25, ChronoUnit.DAYS),
            detectedAt = Instant.now().minus(25, ChronoUnit.DAYS),
            priceBefore = 139.6,
            priceAfter = 132.5,
            briefDescription = "Data center GPU revenue miss and reduced full-year outlook"
        )
    )

    val eventCauses: MutableList<EventCause> = mutableListOf(
        // evt-aapl-1 causes
        EventCause(
            eventId = "evt-aapl-1",
            newsItemId = "news-aapl-5",
            rank = 1,
            title = "Earnings Miss",
            relevanceScore = 0.88,
            rationale = "Q4 earnings per share came in at \$2.10 vs. the expected \$2.18, representing a significant miss that triggered immediate selling pressure."
        ),
        EventCause(
            eventId = "evt-aapl-1",
            newsItemId = "news-aapl-6",
            rank = 2,
            title = "China Sales Decline",
            relevanceScore = 0.76,
            rationale = "iPhone sales in Greater China dropped 11% year-over-year amid increasing competition from domestic smartphone manufacturers."
        ),
        EventCause(
            eventId = "evt-aapl-1",
            newsItemId = "news-aapl-7",
            rank = 3,
            title = "Weak Forward Guidance",
            relevanceScore = 0.65,
            rationale = "Management projected Q1 revenue of \$117-122B, below the \$124B consensus, citing macroeconomic uncertainty and currency headwinds."
        ),
        // evt-shop-1 causes
        EventCause(
            eventId = "evt-shop-1",
            newsItemId = "news-shop-1",
            rank = 1,
            title = "Fed Policy Signals",
            relevanceScore = 0.82,
            rationale = "The Federal Reserve indicated potential rate hikes could extend through mid-2026, dampening enthusiasm for high-multiple growth stocks."
        ),
        EventCause(
            eventId = "evt-shop-1",
            newsItemId = "news-shop-2",
            rank = 2,
            title = "Retail Spending Data",
            relevanceScore = 0.74,
            rationale = "January retail sales fell 0.8%, the largest monthly decline in over a year, raising concerns about consumer spending on e-commerce platforms."
        ),
        EventCause(
            eventId = "evt-shop-1",
            newsItemId = "news-shop-3",
            rank = 3,
            title = "Sector Rotation",
            relevanceScore = 0.61,
            rationale = "Institutional investors rotated capital from high-growth tech into defensive sectors, with the growth-to-value ratio hitting a 6-month low."
        ),
        // evt-msft-1 causes
        EventCause(
            eventId = "evt-msft-1",
            newsItemId = "news-msft-4",
            rank = 1,
            title = "Earnings Beat",
            relevanceScore = 0.91,
            rationale = "Microsoft reported Q2 EPS of \$3.23 vs. the \$3.11 consensus, with total revenue of \$62.0B beating the \$60.9B estimate, triggering broad institutional buying."
        ),
        EventCause(
            eventId = "evt-msft-1",
            newsItemId = "news-msft-5",
            rank = 2,
            title = "Azure Re-Acceleration",
            relevanceScore = 0.84,
            rationale = "Azure growth re-accelerated to 33% year-over-year, well above the 30% consensus estimate, dispelling concerns about cloud spending slowdowns and confirming AI-driven demand."
        ),
        EventCause(
            eventId = "evt-msft-1",
            newsItemId = "news-msft-6",
            rank = 3,
            title = "Copilot Monetisation Inflection",
            relevanceScore = 0.72,
            rationale = "Enterprise Copilot seats tripling quarter-over-quarter signalled that AI-driven revenue is inflecting, raising analyst price targets and improving the long-term earnings outlook."
        ),
        // evt-nvda-1 causes
        EventCause(
            eventId = "evt-nvda-1",
            newsItemId = "news-nvda-4",
            rank = 1,
            title = "Record Data Center Revenue",
            relevanceScore = 0.94,
            rationale = "Q4 data center revenue of \$35.6B more than doubled year-over-year and materially exceeded the \$33.5B consensus, reinforcing NVIDIA's dominance in AI infrastructure spending."
        ),
        EventCause(
            eventId = "evt-nvda-1",
            newsItemId = "news-nvda-5",
            rank = 2,
            title = "Blackwell Supply Constraint Signals Sustained Demand",
            relevanceScore = 0.81,
            rationale = "A 12-month order backlog for Blackwell GPUs confirmed that demand far outstrips supply, reducing near-term revenue risk and supporting elevated ASPs well into 2026."
        ),
        EventCause(
            eventId = "evt-nvda-1",
            newsItemId = "news-nvda-6",
            rank = 3,
            title = "Rubin Architecture Roadmap",
            relevanceScore = 0.69,
            rationale = "Announcing the next-generation Rubin GPU architecture extended NVIDIA's technology lead and reinforced the investment thesis that the company will maintain pricing power through successive platform cycles."
        ),
        // evt-amd-1 causes
        EventCause(
            eventId = "evt-amd-1",
            newsItemId = "news-amd-5",
            rank = 1,
            title = "Data Center GPU Revenue Miss",
            relevanceScore = 0.85,
            rationale = "Q4 data center GPU revenue of \$2.3B fell \$0.5B short of the \$2.8B consensus, directly disappointing the highest-growth segment that investors had been pricing in, and triggering broad-based selling."
        ),
        EventCause(
            eventId = "evt-amd-1",
            newsItemId = "news-amd-6",
            rank = 2,
            title = "MI300X Ramp Delayed by Software Friction",
            relevanceScore = 0.78,
            rationale = "Management's admission that ROCm software incompatibility is slowing MI300X adoption pushed expected revenue recognition further into the future, increasing uncertainty around the AI GPU growth narrative."
        ),
        EventCause(
            eventId = "evt-amd-1",
            newsItemId = "news-amd-7",
            rank = 3,
            title = "Full-Year Outlook Cut",
            relevanceScore = 0.67,
            rationale = "Reducing the full-year data center GPU forecast from \$5.0B to \$4.5B signalled that the gap between AMD and NVIDIA in AI infrastructure is widening rather than closing, dampening medium-term investor confidence."
        )
    )

    val alertRules: MutableList<AlertRule> = mutableListOf(
        AlertRule(id = "alert-AAPL-PRICE_CHANGE", tickerKey = "AAPL", alertType = AlertType.PRICE_CHANGE, threshold = 5.0, enabled = true),
        AlertRule(id = "alert-NVDA-PRICE_CHANGE", tickerKey = "NVDA", alertType = AlertType.PRICE_CHANGE, threshold = 7.5, enabled = false),
        AlertRule(id = "alert-MSFT-VOLUME_SPIKE", tickerKey = "MSFT", alertType = AlertType.VOLUME_SPIKE, threshold = 3.0, enabled = true),
        AlertRule(id = "alert-GOOGL-PRICE_CHANGE", tickerKey = "GOOGL", alertType = AlertType.PRICE_CHANGE, threshold = 5.0, enabled = true),
        AlertRule(id = "alert-AAPL-EARNINGS", tickerKey = "AAPL", alertType = AlertType.EARNINGS_ANNOUNCEMENT, threshold = 7.0, enabled = true),
        AlertRule(id = "alert-AMD-PRICE_CHANGE", tickerKey = "AMD", alertType = AlertType.PRICE_CHANGE, threshold = 6.0, enabled = true)
    )

    val aiExplanations: MutableMap<String, AiExplanation> = mutableMapOf(
        "evt-aapl-1" to AiExplanation(
            eventId = "evt-aapl-1",
            summary = "Apple Inc. experienced a notable decline following its latest quarterly earnings report, which fell short of analyst expectations. The miss was primarily driven by weaker-than-anticipated iPhone sales in the Chinese market, compounded by broader macroeconomic headwinds affecting consumer spending on premium electronics. Investor sentiment shifted bearish as guidance for the upcoming quarter also came in below consensus estimates.",
            bullets = listOf(
                "EPS miss of \$0.08 drove immediate institutional selling",
                "China revenue weakness signals structural competitive pressure",
                "Conservative Q1 guidance raised concerns about demand trajectory"
            ),
            sentiment = Sentiment.BEARISH,
            confidence = 0.93
        ),
        "evt-shop-1" to AiExplanation(
            eventId = "evt-shop-1",
            summary = "Shopify shares saw a sharp selloff driven by a combination of macroeconomic concerns and sector-wide rotation out of high-growth tech names. The Federal Reserve's latest policy signals suggested a more hawkish stance than previously anticipated, leading to a repricing of growth stocks. Additionally, weaker-than-expected retail spending data raised concerns about the e-commerce sector's near-term growth trajectory.",
            bullets = listOf(
                "Hawkish Fed pivot compressed valuations for high-multiple growth names",
                "Weakest retail spending reading in over a year raised e-commerce growth doubts",
                "Broad tech rotation amplified the move beyond company-specific factors"
            ),
            sentiment = Sentiment.BEARISH,
            confidence = 0.87
        ),
        "evt-msft-1" to AiExplanation(
            eventId = "evt-msft-1",
            summary = "Microsoft surged following a blowout Q2 earnings report that exceeded expectations across every major segment. Azure cloud growth re-accelerated to 33% year-over-year, well above the 30% consensus, driven by AI workload demand from hyperscalers and enterprise customers. The rapid monetisation of Copilot raised the long-term earnings growth outlook, prompting widespread analyst upgrades and significant institutional buying.",
            bullets = listOf(
                "EPS of \$3.23 beat the \$3.11 consensus, with revenue 1.8% ahead of estimates",
                "Azure re-acceleration to 33% growth dispelled cloud spending slowdown fears",
                "Copilot seat tripling quarter-over-quarter signals AI monetisation inflection point"
            ),
            sentiment = Sentiment.BULLISH,
            confidence = 0.91
        ),
        "evt-nvda-1" to AiExplanation(
            eventId = "evt-nvda-1",
            summary = "NVIDIA posted another record-breaking quarter with data center revenue of \$35.6B doubling year-over-year, far exceeding analyst estimates. CEO Jensen Huang's announcement of the next-generation Rubin GPU architecture at GTC reinforced the company's multi-year technology roadmap advantage. The combination of sustained supply constraints and an expanding order backlog underscored that demand for NVIDIA's AI infrastructure remains structurally elevated.",
            bullets = listOf(
                "Data center revenue of \$35.6B doubled YoY and beat the \$33.5B consensus by 6%",
                "12-month Blackwell backlog confirms demand visibility and supports elevated ASPs",
                "Rubin architecture announcement extended technology lead and raised long-term price targets"
            ),
            sentiment = Sentiment.BULLISH,
            confidence = 0.94
        ),
        "evt-amd-1" to AiExplanation(
            eventId = "evt-amd-1",
            summary = "AMD fell sharply after reporting Q4 data center GPU revenue that missed the consensus by nearly 18%, while simultaneously cutting its full-year outlook. The CFO's comments that ROCm software friction is slowing MI300X adoption reinforced the market's concern that AMD's AI GPU ramp is significantly lagging NVIDIA's Blackwell cycle. While AMD's CPU business remains strong, investors rotated out of the stock as the AI GPU growth narrative — the primary valuation driver — lost credibility.",
            bullets = listOf(
                "Data center GPU revenue of \$2.3B missed the \$2.8B consensus by 18%, the largest shortfall in three quarters",
                "ROCm software friction delaying MI300X adoption pushes meaningful GPU revenue into H2 2025 at the earliest",
                "Full-year GPU forecast cut from \$5.0B to \$4.5B signals the competitive gap with NVIDIA is widening"
            ),
            sentiment = Sentiment.BEARISH,
            confidence = 0.89
        )
    )

    val stockAnalyses: MutableMap<String, StockAnalysis> = mutableMapOf(
        "AAPL" to StockAnalysis(
            tickerKey = "AAPL",
            summary = "Apple is navigating a transitional period marked by softening iPhone demand in China and cautious near-term guidance, offset by steady services revenue growth and a resilient installed base. Technical indicators point to mild bearish pressure following the recent earnings miss, though the long-term fundamental thesis remains intact. Analysts expect a recovery in the second half driven by a product refresh cycle and continued App Store momentum.",
            sentiment = Sentiment.BEARISH,
            confidence = 0.79
        ),
        "MSFT" to StockAnalysis(
            tickerKey = "MSFT",
            summary = "Microsoft is firing on all cylinders, with Azure re-accelerating, Copilot monetisation inflecting, and earnings consistently beating consensus. The company's deep integration of AI across its productivity and cloud suites creates durable competitive moats that are difficult for rivals to replicate. Analysts broadly maintain Buy ratings with price targets averaging \$480, citing a multi-year AI-driven earnings growth runway.",
            sentiment = Sentiment.BULLISH,
            confidence = 0.84
        ),
        "NVDA" to StockAnalysis(
            tickerKey = "NVDA",
            summary = "NVIDIA remains the dominant supplier of AI training and inference infrastructure, with Blackwell demand far outpacing supply and the Rubin roadmap extending its technology lead. Record data center revenues and a 12-month order backlog provide strong near-term earnings visibility, while expanding software and services revenues add recurring income streams. The stock commands a premium valuation that is well-supported by the pace of AI capital expenditure across hyperscalers.",
            sentiment = Sentiment.BULLISH,
            confidence = 0.88
        ),
        "GOOGL" to StockAnalysis(
            tickerKey = "GOOGL",
            summary = "Alphabet is demonstrating that its core Search franchise is more resilient than feared, with AI Overviews expanding engagement rather than cannibalising ad revenue. Google Cloud is gaining share against AWS and Azure as Gemini integrations differentiate its enterprise offerings. The DOJ antitrust remedy phase introduces headline risk, but the market is pricing in a manageable outcome with no structural breakup expected.",
            sentiment = Sentiment.BULLISH,
            confidence = 0.81
        ),
        "AMD" to StockAnalysis(
            tickerKey = "AMD",
            summary = "AMD presents a mixed picture: its CPU business is executing extremely well with EPYC server share at decade highs, but the AI GPU ramp is falling materially short of investor expectations. The MI300X faces a structural software moat in CUDA that is proving harder to overcome than management initially guided. Near-term catalysts are limited until ROCm matures and MI350 ships, though the long-term competitive position in x86 compute remains a genuine strength.",
            sentiment = Sentiment.BEARISH,
            confidence = 0.76
        )
    )
}
