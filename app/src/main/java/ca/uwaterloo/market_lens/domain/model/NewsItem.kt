package ca.uwaterloo.market_lens.domain.model

import java.time.Instant

data class NewsItem(
    val id: String,
    val tickerKey: String,
    val title: String,
    val source: NewsSource,
    val publishedAt: Instant,
    val url: String,
    val snippet: String? = null
)
