package ca.uwaterloo.market_lens.ui.portfolio

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
class PortfolioViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var model: MarketLensModel
    private lateinit var viewModel: PortfolioViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        model = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun quote(ticker: String, price: Double, changePercent: Double) = StockQuote(
        tickerKey = ticker,
        price = price,
        changePercent = changePercent,
        asOf = Instant.now()
    )

    private fun position(ticker: String, shares: Double?, avgCost: Double? = null) =
        PortfolioPosition(tickerKey = ticker, shares = shares, avgCost = avgCost)

    private fun portfolio(vararg positions: PortfolioPosition) =
        Portfolio(id = "p1", ownerUserId = "u1", positions = positions.toList())

    @Test
    fun totalValue_is_sum_of_shares_times_price() = runTest {
        coEvery { model.getPortfolio() } returns portfolio(
            position("AAPL", 10.0),
            position("MSFT", 5.0)
        )
        coEvery { model.getQuote("AAPL") } returns quote("AAPL", 200.0, 0.0)
        coEvery { model.getQuote("MSFT") } returns quote("MSFT", 400.0, 0.0)

        viewModel = PortfolioViewModel(model)
        advanceUntilIdle()

        // 10 * 200 + 5 * 400 = 2000 + 2000 = 4000
        assertEquals("$4,000.00", viewModel.uiState.value.totalValue)
    }

    @Test
    fun netChange_reflects_all_time_unrealized_gain() = runTest {
        coEvery { model.getPortfolio() } returns portfolio(
            position("AAPL", 10.0, avgCost = 80.0),
        )
        coEvery { model.getQuote("AAPL") } returns quote("AAPL", 100.0, 2.0)

        viewModel = PortfolioViewModel(model)
        advanceUntilIdle()

        // 10 * (100 - 80) = $200 total return
        assertTrue(viewModel.uiState.value.netChange.contains("200"))
    }

    @Test
    fun netChange_is_zero_when_no_avgCost_set() = runTest {
        coEvery { model.getPortfolio() } returns portfolio(
            position("AAPL", 10.0, avgCost = null),
        )
        coEvery { model.getQuote("AAPL") } returns quote("AAPL", 100.0, 2.0)

        viewModel = PortfolioViewModel(model)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.netChange.contains("0.00"))
    }

    @Test
    fun position_with_zero_shares_contributes_nothing_to_total() = runTest {
        coEvery { model.getPortfolio() } returns portfolio(
            position("AAPL", 0.0),
            position("MSFT", 4.0)
        )
        coEvery { model.getQuote("AAPL") } returns quote("AAPL", 999.0, 0.0)
        coEvery { model.getQuote("MSFT") } returns quote("MSFT", 250.0, 0.0)

        viewModel = PortfolioViewModel(model)
        advanceUntilIdle()

        // only MSFT: 4 * 250 = 1000
        assertEquals("$1,000.00", viewModel.uiState.value.totalValue)
    }

    @Test
    fun position_with_null_shares_contributes_nothing_to_total() = runTest {
        coEvery { model.getPortfolio() } returns portfolio(
            position("AAPL", null)
        )
        coEvery { model.getQuote("AAPL") } returns quote("AAPL", 500.0, 1.0)

        viewModel = PortfolioViewModel(model)
        advanceUntilIdle()

        assertEquals("$0.00", viewModel.uiState.value.totalValue)
    }

    @Test
    fun positionValues_map_contains_correct_value_per_ticker() = runTest {
        coEvery { model.getPortfolio() } returns portfolio(
            position("AAPL", 3.0),
            position("NVDA", 2.0)
        )
        coEvery { model.getQuote("AAPL") } returns quote("AAPL", 100.0, 0.0)
        coEvery { model.getQuote("NVDA") } returns quote("NVDA", 500.0, 0.0)

        viewModel = PortfolioViewModel(model)
        advanceUntilIdle()

        val values = viewModel.uiState.value.positionValues
        assertEquals(300.0, values["AAPL"]!!, 0.001)
        assertEquals(1000.0, values["NVDA"]!!, 0.001)
    }

    @Test
    fun error_state_is_set_when_portfolio_load_fails() = runTest {
        coEvery { model.getPortfolio() } throws RuntimeException("network error")

        viewModel = PortfolioViewModel(model)
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.errorMessage)
    }
}
