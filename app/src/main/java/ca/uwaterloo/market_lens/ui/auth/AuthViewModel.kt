package ca.uwaterloo.market_lens.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun submit(_email: String, _password: String, onSuccess: () -> Unit) {
        //        if (email.isBlank() || pass.isBlank()) {
        //            _error.value = "Please fill in all fields"
        //            return
        //        }

        //        viewModelScope.launch {
        //            _isLoading.value = true
        //            _error.value = null
        //
        //            val result = supabaseService.login(email, pass)
        //
        //            result.onSuccess {
        //                _isLoading.value = false
        //                onSuccess() // Navigate only on success
        //            }.onFailure { exception ->
        //                _isLoading.value = false
        //                _error.value = exception.message ?: "Login failed"
        //            }
        //        }

        viewModelScope.launch {
            onSuccess()
        }
    }
}
