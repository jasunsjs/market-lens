package ca.uwaterloo.market_lens.ui.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.market_lens.di.AppGraph
import ca.uwaterloo.market_lens.domain.model.*
import ca.uwaterloo.market_lens.domain.service.MarketLensModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StockUiState(
    val ticker: String = "",
    val quote: StockQuote? = null,
    val priceSeries: PriceSeries? = null,
    val newsItems: List<NewsItem> = emptyList(),
    val analysis: StockAnalysis? = null,
    val shares: Double? = null,
    val avgCost: Double? = null,
    val holdingValue: Double? = null,
    val unrealizedGain: Double? = null,
    val unrealizedGainPercent: Double? = null,
    val isLoading: Boolean = false
)

class StockViewModel(
    private val model: MarketLensModel = AppGraph.model
) : ViewModel() {

    private val _uiState = MutableStateFlow(StockUiState())
    val uiState: StateFlow<StockUiState> = _uiState.asStateFlow()

    fun loadStockData(tickerKey: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(ticker = tickerKey, isLoading = true)
                val quote = model.getQuote(tickerKey)
                val series = model.getPriceSeries(tickerKey, PriceRange.ONE_MONTH)
                val news = model.getNewsByTicker(tickerKey)
                val analysis = model.getStockAnalysis(tickerKey)
                val portfolio = model.getPortfolio()
                val position = portfolio.positions.find { it.tickerKey == tickerKey }
                val shares = position?.shares
                val avgCost = position?.avgCost
                val holdingValue = if (shares != null && shares > 0) shares * quote.price else null
                val unrealizedGain = if (shares != null && shares > 0 && avgCost != null && avgCost > 0)
                    shares * (quote.price - avgCost) else null
                val unrealizedGainPercent = if (avgCost != null && avgCost > 0)
                    ((quote.price - avgCost) / avgCost) * 100.0 else null

                // Append live quote price as final point so today's price is always shown
                val seriesWithToday = series.copy(
                    points = series.points + PricePoint(timestamp = quote.asOf, close = quote.price)
                )

                _uiState.value = _uiState.value.copy(
                    quote = quote,
                    priceSeries = seriesWithToday,
                    newsItems = news,
                    analysis = analysis,
                    shares = shares,
                    avgCost = avgCost,
                    holdingValue = holdingValue,
                    unrealizedGain = unrealizedGain,
                    unrealizedGainPercent = unrealizedGainPercent,
                    isLoading = false
                )
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun navigateToPortfolioPage(onSuccess: () -> Unit) {
        viewModelScope.launch {
            onSuccess()
        }
    }
}
