package ca.uwaterloo.market_lens.ui.portfolio

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// class holding stock info for each UI element
data class StockItemInfo(
    val id: String,
    val ticker: String,
    val weight: String
)


class PortfolioViewModel : ViewModel() {
    val stockList = mutableStateListOf<StockItemInfo>()

    init {
        // Add some initial stocks
        stockList.addAll(
            listOf(
                StockItemInfo(id = "1", ticker = "AAPL", weight = "30"),
                StockItemInfo(id = "2", ticker = "MSFT", weight = "50"),
                StockItemInfo(id = "3", ticker = "NVDA", weight = "20"),
            )
        )
    }

    fun addStock(stock: StockItemInfo) {
        stockList.add(stock)
    }

    fun removeStock(stock: StockItemInfo) {
        stockList.remove(stock)
    }

    fun navigateToStockPage(
        stockItemInfo: StockItemInfo,
        onSuccess: () -> Unit) {
        viewModelScope.launch {
            onSuccess()
        }
    }
}