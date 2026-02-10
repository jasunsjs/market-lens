package ca.uwaterloo.market_lens.ui.events

import androidx.lifecycle.ViewModel

class EventsViewModel : ViewModel() {
    val events = mapOf(
        "1" to EventData(
            id = "1",
            ticker = "AAPL",
            percentChange = -2.8,
            briefDescription = "Likely driven by earning miss and market reaction",
            timestamp = "Jan 27, 2026 - 2:31 PM",
            aiExplanation = "Apple Inc. experienced a notable decline following its latest quarterly earnings report, which fell short of analyst expectations. The miss was primarily driven by weaker-than-anticipated iPhone sales in the Chinese market, compounded by broader macroeconomic headwinds affecting consumer spending on premium electronics. Investor sentiment shifted bearish as guidance for the upcoming quarter also came in below consensus estimates.",
            contributingFactors = listOf(
                ContributingFactor(
                    rank = 1,
                    title = "Earnings Miss",
                    percentage = 88,
                    description = "Q4 earnings per share came in at \$2.10 vs. the expected \$2.18, representing a significant miss that triggered immediate selling pressure.",
                    source = "Financial Times",
                    sourceUrl = "https://www.ft.com/"
                ),
                ContributingFactor(
                    rank = 2,
                    title = "China Sales Decline",
                    percentage = 76,
                    description = "iPhone sales in Greater China dropped 11% year-over-year amid increasing competition from domestic smartphone manufacturers.",
                    source = "Bloomberg",
                    sourceUrl = "https://www.bloomberg.com/canada"
                ),
                ContributingFactor(
                    rank = 3,
                    title = "Weak Forward Guidance",
                    percentage = 65,
                    description = "Management projected Q1 revenue of \$117-122B, below the \$124B consensus, citing macroeconomic uncertainty and currency headwinds.",
                    source = "Reuters",
                    sourceUrl = "https://www.reuters.com/"
                )
            ),
            overallConfidence = 93
        ),
        "2" to EventData(
            id = "2",
            ticker = "SHOP",
            percentChange = -6.8,
            briefDescription = "Macroeconomic data impact and Fed policy signals",
            timestamp = "Jan 27, 2026 - 2:31 PM",
            aiExplanation = "Shopify shares saw a sharp selloff driven by a combination of macroeconomic concerns and sector-wide rotation out of high-growth tech names. The Federal Reserve's latest policy signals suggested a more hawkish stance than previously anticipated, leading to a repricing of growth stocks. Additionally, weaker-than-expected retail spending data raised concerns about the e-commerce sector's near-term growth trajectory.",
            contributingFactors = listOf(
                ContributingFactor(
                    rank = 1,
                    title = "Fed Policy Signals",
                    percentage = 82,
                    description = "The Federal Reserve indicated potential rate hikes could extend through mid-2026, dampening enthusiasm for high-multiple growth stocks.",
                    source = "Wall Street Journal",
                    sourceUrl = "https://www.wsj.com/"
                ),
                ContributingFactor(
                    rank = 2,
                    title = "Retail Spending Data",
                    percentage = 74,
                    description = "January retail sales fell 0.8%, the largest monthly decline in over a year, raising concerns about consumer spending on e-commerce platforms.",
                    source = "CNBC",
                    sourceUrl = "https://www.cnbc.com/"
                ),
                ContributingFactor(
                    rank = 3,
                    title = "Sector Rotation",
                    percentage = 61,
                    description = "Institutional investors rotated capital from high-growth tech into defensive sectors, with the growth-to-value ratio hitting a 6-month low.",
                    source = "Bloomberg",
                    sourceUrl = "https://www.bloomberg.com/canada"
                )
            ),
            overallConfidence = 87
        )
    )

    fun getEventData(eventId: String): EventData? {
        return events[eventId]
    }
}
