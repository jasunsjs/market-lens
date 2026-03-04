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
    val isLoading: Boolean = false
)

class StockViewModel(
    private val model: MarketLensModel = AppGraph.model
) : ViewModel() {

    private val _uiState = MutableStateFlow(StockUiState())
    val uiState: StateFlow<StockUiState> = _uiState.asStateFlow()

    fun loadStockData(tickerKey: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(ticker = tickerKey, isLoading = true)
            val quote = model.getQuote(tickerKey)
            val series = model.getPriceSeries(tickerKey, PriceRange.ONE_MONTH)
            val news = model.getNewsByTicker(tickerKey)
            val analysis = model.getStockAnalysis(tickerKey)

            _uiState.value = _uiState.value.copy(
                quote = quote,
                priceSeries = series,
                newsItems = news,
                analysis = analysis,
                isLoading = false
            )
        }
    }

    fun navigateToPortfolioPage(onSuccess: () -> Unit) {
        viewModelScope.launch {
            onSuccess()
        }
    }
}
