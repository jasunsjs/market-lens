package ca.uwaterloo.market_lens.data.mock

import ca.uwaterloo.market_lens.domain.model.NewsItem
import ca.uwaterloo.market_lens.domain.repository.NewsRepository

class MockNewsRepository : NewsRepository {
    override suspend fun getNewsByTicker(tickerKey: String): List<NewsItem> =
        MockDb.newsItems.filter { it.tickerKey == tickerKey }

    override suspend fun getNewsItem(newsItemId: String): NewsItem =
        MockDb.newsItems.first { it.id == newsItemId }
}
