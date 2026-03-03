package ca.uwaterloo.market_lens.domain.repository

import ca.uwaterloo.market_lens.domain.model.NewsItem

interface NewsRepository {
    suspend fun getNewsByTicker(tickerKey: String): List<NewsItem>
    suspend fun getNewsItem(newsItemId: String): NewsItem
}
