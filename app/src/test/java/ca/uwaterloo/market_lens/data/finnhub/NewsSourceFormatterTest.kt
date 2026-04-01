package ca.uwaterloo.market_lens.data.finnhub

import org.junit.Assert.assertEquals
import org.junit.Test

class NewsSourceFormatterTest {

    @Test
    fun known_sources_map_to_display_names() {
        assertEquals("Yahoo Finance", formatNewsSourceName("yahoo"))
        assertEquals("Reuters", formatNewsSourceName("reuters"))
        assertEquals("CNBC", formatNewsSourceName("cnbc"))
        assertEquals("Bloomberg", formatNewsSourceName("bloomberg"))
        assertEquals("MarketWatch", formatNewsSourceName("marketwatch"))
        assertEquals("Seeking Alpha", formatNewsSourceName("seekingalpha"))
        assertEquals("Benzinga", formatNewsSourceName("benzinga"))
        assertEquals("Motley Fool", formatNewsSourceName("motleyfool"))
        assertEquals("Motley Fool", formatNewsSourceName("fool"))
        assertEquals("Wall Street Journal", formatNewsSourceName("wsj"))
        assertEquals("Financial Times", formatNewsSourceName("ft"))
        assertEquals("Barron's", formatNewsSourceName("barrons"))
        assertEquals("The Street", formatNewsSourceName("thestreet"))
        assertEquals("Forbes", formatNewsSourceName("forbes"))
        assertEquals("Forbes", formatNewsSourceName("forbesdigitalmedia"))
        assertEquals("Business Insider", formatNewsSourceName("businessinsider"))
        assertEquals("Associated Press", formatNewsSourceName("ap"))
        assertEquals("Investopedia", formatNewsSourceName("investopedia"))
    }

    @Test
    fun source_matching_is_case_insensitive() {
        assertEquals("Yahoo Finance", formatNewsSourceName("Yahoo"))
        assertEquals("Yahoo Finance", formatNewsSourceName("YAHOO"))
        assertEquals("Reuters", formatNewsSourceName("Reuters"))
        assertEquals("CNBC", formatNewsSourceName("CNBC"))
    }

    @Test
    fun unknown_source_is_capitalized() {
        assertEquals("Thehill", formatNewsSourceName("thehill"))
        assertEquals("Axios", formatNewsSourceName("axios"))
        assertEquals("Techcrunch", formatNewsSourceName("techcrunch"))
    }

    @Test
    fun empty_source_returns_empty() {
        assertEquals("", formatNewsSourceName(""))
    }
}
