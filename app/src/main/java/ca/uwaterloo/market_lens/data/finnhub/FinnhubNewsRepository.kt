package ca.uwaterloo.market_lens.data.finnhub

import ca.uwaterloo.market_lens.BuildConfig
import ca.uwaterloo.market_lens.domain.model.NewsItem
import ca.uwaterloo.market_lens.domain.model.NewsSource
import ca.uwaterloo.market_lens.domain.repository.NewsRepository
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate

class FinnhubNewsRepository : NewsRepository {

    private val apiKey get() = BuildConfig.FINNHUB_API_KEY
    private val http = FinnhubClient.http

    override suspend fun getNewsByTicker(tickerKey: String): List<NewsItem> {
        val to = LocalDate.now().toString()
        val from = LocalDate.now().minusDays(7).toString()

        return runCatching {
            http.get("${FinnhubClient.BASE_URL}/company-news") {
                parameter("symbol", tickerKey)
                parameter("from", from)
                parameter("to", to)
                parameter("token", apiKey)
            }.body<List<FinnhubNewsArticle>>()
                .take(10)
                .map { it.toDomain(tickerKey) }
        }.getOrDefault(emptyList())
    }

    override suspend fun getNewsItem(newsItemId: String): NewsItem {
        // This is not needed for the current features
        throw UnsupportedOperationException("Fetching individual news items is not supported by Finnhub")
    }
}

@Serializable
private data class FinnhubNewsArticle(
    @SerialName("id") val id: Long = 0L,
    @SerialName("headline") val headline: String = "",
    @SerialName("source") val source: String = "",
    @SerialName("datetime") val datetime: Long = 0L,
    @SerialName("url") val url: String = "",
    @SerialName("summary") val summary: String = "",
) {
    fun toDomain(tickerKey: String) = NewsItem(
        id = id.toString(),
        tickerKey = tickerKey,
        title = headline,
        source = NewsSource(name = formatNewsSourceName(source)),
        publishedAt = Instant.ofEpochSecond(datetime),
        url = url,
        snippet = summary.takeIf { it.isNotBlank() }
    )
}

internal fun formatNewsSourceName(source: String): String = when (source.lowercase()) {
        "yahoo" -> "Yahoo Finance"
        "reuters" -> "Reuters"
        "cnbc" -> "CNBC"
        "bloomberg" -> "Bloomberg"
        "marketwatch" -> "MarketWatch"
        "seekingalpha" -> "Seeking Alpha"
        "benzinga" -> "Benzinga"
        "motleyfool" -> "Motley Fool"
        "wsj" -> "Wall Street Journal"
        "ft" -> "Financial Times"
        "barrons" -> "Barron's"
        "thestreet" -> "The Street"
        "investopedia" -> "Investopedia"
        "fool" -> "Motley Fool"
        "businessinsider" -> "Business Insider"
        "forbesdigitalmedia" -> "Forbes"
        "forbes" -> "Forbes"
        "ap" -> "Associated Press"
        else -> source.replaceFirstChar { it.uppercaseChar() }
    }
