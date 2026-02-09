package ca.uwaterloo.market_lens.ui.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PortfolioViewModel : ViewModel() {
    fun navigateToAlertPage(onSuccess: () -> Unit) {
        viewModelScope.launch {
            onSuccess()
        }
    }
}