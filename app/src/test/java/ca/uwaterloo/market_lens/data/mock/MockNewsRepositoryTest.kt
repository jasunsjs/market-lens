package ca.uwaterloo.market_lens.data.mock

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class MockNewsRepositoryTest {

    private val repository = MockNewsRepository()

    @Test
    fun getNewsByTicker_returns_items_for_matching_ticker() = runBlocking {
        val tickerKey = MockDb.newsItems.first().tickerKey
        val expected = MockDb.newsItems.filter { it.tickerKey == tickerKey }

        val items = repository.getNewsByTicker(tickerKey)

        assertEquals(expected, items)
        assertTrue(items.all { it.tickerKey == tickerKey })
    }

    @Test
    fun getNewsItem_returns_matching_news_item_by_id() = runBlocking {
        val expected = MockDb.newsItems.first()

        val item = repository.getNewsItem(expected.id)

        assertEquals(expected, item)
    }
}
