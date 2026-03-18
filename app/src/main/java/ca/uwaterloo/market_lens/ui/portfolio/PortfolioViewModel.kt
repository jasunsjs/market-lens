package ca.uwaterloo.market_lens.ui.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.market_lens.di.AppGraph
import ca.uwaterloo.market_lens.domain.model.PortfolioPosition
import ca.uwaterloo.market_lens.domain.model.StockQuote
import ca.uwaterloo.market_lens.domain.service.MarketLensModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PortfolioUiState(
    val positions: List<PortfolioPosition> = emptyList(),
    val quotes: Map<String, StockQuote> = emptyMap(),
    val totalValue: String = "$0.00",
    val netChange: String = "+$0.00",
    val netChangePercent: String = "(+0.00%)",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class PortfolioViewModel(
    private val model: MarketLensModel = AppGraph.model
) : ViewModel() {

    private val _uiState = MutableStateFlow(PortfolioUiState())
    val uiState: StateFlow<PortfolioUiState> = _uiState.asStateFlow()

    init {
        loadPortfolio()
    }

    private fun loadPortfolio() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                val portfolio = model.getPortfolio()
                val quotes = portfolio.positions.associate { it.tickerKey to model.getQuote(it.tickerKey) }

                var totalVal = 0.0
                var totalChange = 0.0

                portfolio.positions.forEach { pos ->
                    val quote = quotes[pos.tickerKey]
                    if (quote != null) {
                        val weight = pos.weight ?: 0.0
                        // Using weight as shares for simplicity in mock calculations
                        val posValue = weight * quote.price
                        totalVal += posValue
                        totalChange += posValue * (quote.changePercent / 100.0)
                    }
                }

                val netChangePercent = if (totalVal != totalChange) (totalChange / (totalVal - totalChange)) * 100.0 else 0.0

                _uiState.value = _uiState.value.copy(
                    positions = portfolio.positions,
                    quotes = quotes,
                    totalValue = String.format("$%,.2f", totalVal),
                    netChange = String.format("%s$%,.2f", if (totalChange >= 0) "+" else "-", Math.abs(totalChange)),
                    netChangePercent = String.format("(%s%.2f%%)", if (netChangePercent >= 0) "+" else "", netChangePercent),
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Unable to load portfolio."
                )
            }
        }
    }

    fun addStock(ticker: String) {
        viewModelScope.launch {
            try {
                model.addTickerToPortfolio(ticker)
                loadPortfolio()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Unable to add ticker."
                )
            }
        }
    }

    fun removeStock(ticker: String) {
        viewModelScope.launch {
            try {
                model.removeTickerFromPortfolio(ticker)
                loadPortfolio()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Unable to remove ticker."
                )
            }
        }
    }

    fun navigateToStockPage(ticker: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            onSuccess()
        }
    }
}
