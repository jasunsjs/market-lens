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

            try {
                model.login(email, password)
                handleAuthResult(onSuccess)
            } catch (e: Exception) {
                _error.value = mapErrorToMessage(e.message ?: "")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signUp(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _error.value = "Please fill in all fields"
            return
        }

        if (password.length < 6) {
            _error.value = "Password must be at least 6 characters"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                model.signUp(email, password)
                handleAuthResult(onSuccess)
            } catch (e: Exception) {
                _error.value = mapErrorToMessage(e.message ?: "")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun handleAuthResult(onSuccess: () -> Unit) {
        when (val authState = model.authState.value) {
            is AuthState.SignedIn -> onSuccess()
            is AuthState.Error -> _error.value = mapErrorToMessage(authState.message)
            else -> {} // Initial or Loading handled elsewhere
        }
    }

    private fun mapErrorToMessage(rawError: String): String {
        return when {
            rawError.contains("invalid login credentials", ignoreCase = true) -> 
                "Invalid email or password. Please try again."
            rawError.contains("user already exists", ignoreCase = true) -> 
                "An account with this email already exists."
            rawError.contains("network", ignoreCase = true) -> 
                "Network error. Please check your internet connection."
            rawError.contains("email not confirmed", ignoreCase = true) ->
                "Please confirm your email address before logging in."
            rawError.isBlank() -> "An unexpected error occurred. Please try again."
            else -> rawError // Fallback to raw error if it's already somewhat friendly
        }
    }
}
