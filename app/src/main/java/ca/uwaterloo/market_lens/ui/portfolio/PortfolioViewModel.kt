package ca.uwaterloo.market_lens.ui.portfolio

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PortfolioViewModel : ViewModel() {

    val stockList = mutableStateListOf<StockItemInfo>()

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