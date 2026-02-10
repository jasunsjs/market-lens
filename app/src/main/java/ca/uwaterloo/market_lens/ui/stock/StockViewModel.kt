package ca.uwaterloo.market_lens.ui.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class StockViewModel : ViewModel() {
    fun navigateToPortfolioPage (onSuccess: () -> Unit) {
        viewModelScope.launch {
            onSuccess()
        }
    }
}