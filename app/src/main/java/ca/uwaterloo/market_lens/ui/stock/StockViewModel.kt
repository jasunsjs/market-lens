package ca.uwaterloo.market_lens.ui.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


data class StockDetails(
    val ticker: String,
    val price: String,
    val priceChange: String,
    val priceChangePercent: String
)

data class NewsItem(
    val title: String,
    val source: String,
    val time: String,
    val description: String,
    val sentiment: Sentiment
)

data class Metric(val label: String, val value: String)

data class Analysis(
    val summary: String,
    val signal: MarketSignal,
    val confidence: Float
)

enum class MarketSignal {
    BULLISH, BEARISH
}

enum class Sentiment {
    POSITIVE, NEGATIVE, NEUTRAL
}

class StockViewModel : ViewModel() {
    private val _stockDetails = MutableStateFlow(
        StockDetails(
            ticker = "AAPL",
            price = "$162.24",
            priceChange = "+3.02",
            priceChangePercent = "(+1.90%)"
        )
    )
    val stockDetails: StateFlow<StockDetails> = _stockDetails

    private val _metrics = MutableStateFlow(
        listOf(
            Metric("Market Cap", "$380.5B"),
            Metric("Volume", "20.2M"),
            Metric("P/E Ratio", "31.4")
        )
    )
    val metrics: StateFlow<List<Metric>> = _metrics

    private val _analysis = MutableStateFlow(
        Analysis(
            summary = "Based on recent market activity, %s is showing positive momentum with a 0.02%% change. Technical indicators suggest bullish sentiment, while fundamental analysis reveals strong market positioning. Analysts maintain a generally favorable outlook with an average price target of $139.70.",
            signal = MarketSignal.BULLISH,
            confidence = 0.79f
        )
    )
    val analysis: StateFlow<Analysis> = _analysis

    // placeholder news data
    val newsItems = listOf(
        NewsItem(
            title = "AAPL Announces New Product Line",
            time = "2 hours ago",
            description = "Company reveals innovative solutions targeting enterprise customers, expected to drive Q4 revenue growth.",
            source = "Bloomberg",
            sentiment = Sentiment.POSITIVE
        ),
        NewsItem(
            title = "Analyst Upgrades Price Target",
            time = "5 hours ago",
            description = "Major investment bank raises price target by 15% citing strong fundamentals and market position.",
            source = "Reuters",
            sentiment = Sentiment.POSITIVE
        ),
        NewsItem(
            title = "Sector Faces Regulatory Scrutiny",
            time = "1 day ago",
            description = "New proposed regulations could impact operations, though long-term effects remain uncertain.",
            source = "Financial Times",
            sentiment = Sentiment.NEGATIVE
        ),
        NewsItem(
            title = "Q3 Earnings Beat Expectations",
            time = "3 days ago",
            description = "Revenue exceeded analyst forecasts with strong performance across all business segments.",
            source = "CNBC",
            sentiment = Sentiment.POSITIVE
        )
    )
    val dates = listOf("1/13", "1/19", "1/26", "2/1", "2/9")

    val chartData = listOf(
        150f, 155f, 152f, 158f, 165f, 162f, 170f, 175f, 172f, 180f,
        150f, 100f, 110f, 60f, 50f, 100f, 150f, 155f, 152f, 158f,
        180f, 200f, 190f, 195f, 210f, 220f, 200f, 240f, 220f
    )

    fun navigateToPortfolioPage(onSuccess: () -> Unit) {
        viewModelScope.launch {
            onSuccess()
        }
    }
}