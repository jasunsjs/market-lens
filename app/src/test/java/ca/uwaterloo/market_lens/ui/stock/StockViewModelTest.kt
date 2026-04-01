package ca.uwaterloo.market_lens.ui.stock

import ca.uwaterloo.market_lens.domain.model.*
import ca.uwaterloo.market_lens.domain.service.MarketLensModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class StockViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var model: MarketLensModel
    private lateinit var viewModel: StockViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        model = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun setupModel(
        ticker: String,
        price: Double,
        changePercent: Double,
        shares: Double?,
        avgCost: Double?
    ) {
        coEvery { model.getQuote(ticker) } returns StockQuote(
            tickerKey = ticker,
            price = price,
            changePercent = changePercent,
            asOf = Instant.now()
        )
        coEvery { model.getPriceSeries(ticker, PriceRange.ONE_MONTH) } returns PriceSeries(
            tickerKey = ticker, range = PriceRange.ONE_MONTH, points = emptyList()
        )
        coEvery { model.getNewsByTicker(ticker) } returns emptyList()
        coEvery { model.getStockAnalysis(ticker) } returns StockAnalysis(
            tickerKey = ticker, summary = "", sentiment = Sentiment.NEUTRAL, confidence = 0.0
        )
        coEvery { model.getPortfolio() } returns Portfolio(
            id = "p1", ownerUserId = "u1",
            positions = listOf(PortfolioPosition(tickerKey = ticker, shares = shares, avgCost = avgCost))
        )
    }

    @Test
    fun holdingValue_is_shares_times_price() = runTest {
        setupModel("AAPL", price = 200.0, changePercent = 1.0, shares = 5.0, avgCost = 150.0)
        viewModel = StockViewModel(model)
        viewModel.loadStockData("AAPL")
        advanceUntilIdle()

        assertEquals(1000.0, viewModel.uiState.value.holdingValue!!, 0.001)
    }

    @Test
    fun unrealizedGain_is_shares_times_price_minus_avgCost() = runTest {
        setupModel("AAPL", price = 200.0, changePercent = 0.0, shares = 10.0, avgCost = 150.0)
        viewModel = StockViewModel(model)
        viewModel.loadStockData("AAPL")
        advanceUntilIdle()

        // 10 * (200 - 150) = 500
        assertEquals(500.0, viewModel.uiState.value.unrealizedGain!!, 0.001)
    }

    @Test
    fun unrealizedGainPercent_is_price_change_over_avgCost() = runTest {
        setupModel("AAPL", price = 200.0, changePercent = 0.0, shares = 10.0, avgCost = 160.0)
        viewModel = StockViewModel(model)
        viewModel.loadStockData("AAPL")
        advanceUntilIdle()

        // (200 - 160) / 160 * 100 = 25%
        assertEquals(25.0, viewModel.uiState.value.unrealizedGainPercent!!, 0.001)
    }

    @Test
    fun unrealizedGain_is_negative_when_price_below_avgCost() = runTest {
        setupModel("AAPL", price = 100.0, changePercent = 0.0, shares = 10.0, avgCost = 150.0)
        viewModel = StockViewModel(model)
        viewModel.loadStockData("AAPL")
        advanceUntilIdle()

        // 10 * (100 - 150) = -500
        assertEquals(-500.0, viewModel.uiState.value.unrealizedGain!!, 0.001)
        assertTrue(viewModel.uiState.value.unrealizedGainPercent!! < 0)
    }

    @Test
    fun holdingValue_is_null_when_shares_is_null() = runTest {
        setupModel("AAPL", price = 200.0, changePercent = 0.0, shares = null, avgCost = null)
        viewModel = StockViewModel(model)
        viewModel.loadStockData("AAPL")
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.holdingValue)
        assertNull(viewModel.uiState.value.unrealizedGain)
        assertNull(viewModel.uiState.value.unrealizedGainPercent)
    }

    @Test
    fun holdingValue_is_null_when_shares_is_zero() = runTest {
        setupModel("AAPL", price = 200.0, changePercent = 0.0, shares = 0.0, avgCost = null)
        viewModel = StockViewModel(model)
        viewModel.loadStockData("AAPL")
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.holdingValue)
    }

    @Test
    fun unrealizedGain_is_null_when_avgCost_is_null() = runTest {
        setupModel("AAPL", price = 200.0, changePercent = 0.0, shares = 5.0, avgCost = null)
        viewModel = StockViewModel(model)
        viewModel.loadStockData("AAPL")
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.holdingValue)
        assertNull(viewModel.uiState.value.unrealizedGain)
        assertNull(viewModel.uiState.value.unrealizedGainPercent)
    }

    @Test
    fun priceSeries_includes_live_quote_as_last_point() = runTest {
        coEvery { model.getQuote("AAPL") } returns StockQuote(
            tickerKey = "AAPL", price = 199.99, changePercent = 0.5, asOf = Instant.now()
        )
        val historicalPoint = PricePoint(timestamp = Instant.now().minusSeconds(86400), close = 195.0)
        coEvery { model.getPriceSeries("AAPL", PriceRange.ONE_MONTH) } returns PriceSeries(
            tickerKey = "AAPL", range = PriceRange.ONE_MONTH, points = listOf(historicalPoint)
        )
        coEvery { model.getNewsByTicker("AAPL") } returns emptyList()
        coEvery { model.getStockAnalysis("AAPL") } returns StockAnalysis(
            tickerKey = "AAPL", summary = "", sentiment = Sentiment.NEUTRAL, confidence = 0.0
        )
        coEvery { model.getPortfolio() } returns Portfolio(
            id = "p1", ownerUserId = "u1", positions = emptyList()
        )

        viewModel = StockViewModel(model)
        viewModel.loadStockData("AAPL")
        advanceUntilIdle()

        val points = viewModel.uiState.value.priceSeries!!.points
        assertEquals(2, points.size)
        assertEquals(199.99, points.last().close, 0.001)
    }
}
