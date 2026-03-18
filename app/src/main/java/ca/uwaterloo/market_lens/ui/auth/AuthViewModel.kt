package ca.uwaterloo.market_lens.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.market_lens.di.AppGraph
import ca.uwaterloo.market_lens.domain.model.AuthState
import ca.uwaterloo.market_lens.domain.service.MarketLensModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val model: MarketLensModel = AppGraph.model
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _error.value = "Please fill in all fields"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            model.login(email, password)
            handleAuthResult(onSuccess)
        }
    }

    fun signUp(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _error.value = "Please fill in all fields"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            model.signUp(email, password)
            handleAuthResult(onSuccess)
        }
    }

    private fun handleAuthResult(onSuccess: () -> Unit) {
        when (val authState = model.authState.value) {
            is AuthState.SignedIn -> onSuccess()
            is AuthState.Error -> _error.value = authState.message
            else -> _error.value = "Authentication failed"
        }

        _isLoading.value = false
    }
}
