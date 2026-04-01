package ca.uwaterloo.market_lens.data.finnhub

import ca.uwaterloo.market_lens.BuildConfig
import ca.uwaterloo.market_lens.domain.model.*
import ca.uwaterloo.market_lens.domain.repository.MarketDataRepository
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate

private const val MASSIVE_BASE_URL = "https://api.massive.com"

class FinnhubMarketDataRepository : MarketDataRepository {

    private val apiKey get() = BuildConfig.FINNHUB_API_KEY
    private val http = FinnhubClient.http

    override suspend fun getQuote(tickerKey: String): StockQuote {
        val quote = http.get("${FinnhubClient.BASE_URL}/quote") {
            parameter("symbol", tickerKey)
            parameter("token", apiKey)
        }.body<FinnhubQuoteResponse>()

        val profile = runCatching {
            http.get("${FinnhubClient.BASE_URL}/stock/profile2") {
                parameter("symbol", tickerKey)
                parameter("token", apiKey)
            }.body<FinnhubProfileResponse>()
        }.getOrNull()

        val metrics = runCatching {
            http.get("${FinnhubClient.BASE_URL}/stock/metric") {
                parameter("symbol", tickerKey)
                parameter("metric", "all")
                parameter("token", apiKey)
            }.body<FinnhubMetricsResponse>()
        }.getOrNull()

        // marketCapitalization is in millions of dollars
        val marketCap = profile?.marketCapitalization?.let { (it * 1_000_000).toLong() }
        // 10DayAverageTradingVolume is in millions of shares
        val volume = metrics?.metric?.tenDayAverageTradingVolume?.let { (it * 1_000_000).toLong() }
        val peRatio = metrics?.metric?.peRatio

        return StockQuote(
            tickerKey = tickerKey,
            price = quote.currentPrice,
            changePercent = quote.percentChange,
            asOf = if (quote.timestamp > 0) Instant.ofEpochSecond(quote.timestamp) else Instant.now(),
            volume = volume,
            marketCap = marketCap,
            peRatio = peRatio
        )
    }

    override suspend fun getPriceSeries(tickerKey: String, range: PriceRange): PriceSeries {
        val toDate = LocalDate.now()
        val fromDate = when (range) {
            PriceRange.ONE_DAY -> toDate.minusDays(1)
            PriceRange.FIVE_DAYS -> toDate.minusDays(5)
            PriceRange.ONE_MONTH -> toDate.minusDays(30)
            PriceRange.SIX_MONTHS -> toDate.minusDays(180)
            PriceRange.ONE_YEAR -> toDate.minusDays(365)
        }
        val timespan = when (range) {
            PriceRange.ONE_DAY -> "hour"
            PriceRange.FIVE_DAYS -> "hour"
            else -> "day"
        }

        val points = runCatching {
            http.get("$MASSIVE_BASE_URL/v2/aggs/ticker/$tickerKey/range/1/$timespan/$fromDate/$toDate") {
                parameter("adjusted", "true")
                parameter("sort", "asc")
                parameter("limit", "120")
                parameter("apiKey", BuildConfig.MASSIVE_API_KEY)
            }.body<MassiveAggResponse>()
                .results
                .map { bar ->
                    // Massive timestamps are in milliseconds
                    PricePoint(timestamp = Instant.ofEpochMilli(bar.timestamp), close = bar.close)
                }
        }.getOrDefault(emptyList())

        return PriceSeries(tickerKey = tickerKey, range = range, points = points)
    }
}

@Serializable
private data class FinnhubQuoteResponse(
    @SerialName("c") val currentPrice: Double = 0.0,
    @SerialName("dp") val percentChange: Double = 0.0,
    @SerialName("t") val timestamp: Long = 0L,
)

@Serializable
private data class FinnhubProfileResponse(
    @SerialName("marketCapitalization") val marketCapitalization: Double? = null,
)

@Serializable
private data class FinnhubMetricsResponse(
    @SerialName("metric") val metric: FinnhubMetric = FinnhubMetric()
)

@Serializable
private data class FinnhubMetric(
    @SerialName("peBasicExclExtraTTM") val peRatio: Double? = null,
    @SerialName("10DayAverageTradingVolume") val tenDayAverageTradingVolume: Double? = null
)

@Serializable
private data class MassiveAggResponse(
    @SerialName("results") val results: List<MassiveBar> = emptyList()
)

@Serializable
private data class MassiveBar(
    @SerialName("c") val close: Double = 0.0,
    @SerialName("t") val timestamp: Long = 0L,
)
