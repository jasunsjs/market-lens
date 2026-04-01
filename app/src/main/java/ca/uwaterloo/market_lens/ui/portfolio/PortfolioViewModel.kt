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
    val positionValues: Map<String, Double> = emptyMap(), // ticker -> market value
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
                var totalCostBasis = 0.0
                var hasValidCostBasis = false
                val positionValues = mutableMapOf<String, Double>()

                portfolio.positions.forEach { pos ->
                    val quote = quotes[pos.tickerKey]
                    if (quote != null) {
                        val shares = pos.shares ?: 0.0
                        val posValue = shares * quote.price
                        positionValues[pos.tickerKey] = posValue
                        totalVal += posValue
                        
                        // Only add to cost basis if we have BOTH shares and avgCost
                        if (shares > 0 && pos.avgCost != null && pos.avgCost > 0) {
                            totalCostBasis += shares * pos.avgCost
                            hasValidCostBasis = true
                        }
                    }
                }

                // Calculate return only if we have at least one position with a cost basis
                val totalChange = if (hasValidCostBasis) totalVal - totalCostBasis else 0.0
                val netChangePercent = if (hasValidCostBasis && totalCostBasis > 0) (totalChange / totalCostBasis) * 100.0 else 0.0

                _uiState.value = _uiState.value.copy(
                    positions = portfolio.positions,
                    quotes = quotes,
                    positionValues = positionValues,
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

    fun updateShares(ticker: String, shares: Double, avgCost: Double?) {
        viewModelScope.launch {
            try {
                model.updateShares(ticker, shares, avgCost)
                loadPortfolio()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Unable to update shares."
                )
            }
        }
    }
}
