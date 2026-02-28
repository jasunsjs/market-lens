package ca.uwaterloo.market_lens.domain.model

sealed class AuthState {
    object Loading : AuthState()
    object SignedOut : AuthState()

    data class SignedIn(
        val userId: String,
        val email: String?
    ) : AuthState()

    data class Error(val message: String) : AuthState()
}