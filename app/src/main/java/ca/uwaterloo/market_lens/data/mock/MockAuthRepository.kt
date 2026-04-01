package ca.uwaterloo.market_lens.data.mock

import ca.uwaterloo.market_lens.domain.model.AuthState
import ca.uwaterloo.market_lens.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockAuthRepository : AuthRepository {
    private val _authState = MutableStateFlow<AuthState>(AuthState.SignedOut)

    override fun observeAuthState(): Flow<AuthState> = _authState.asStateFlow()

    override suspend fun login(email: String, password: String): AuthState {
        _authState.value = AuthState.SignedIn(userId = "mock-user-1", email = email)
        return _authState.value
    }

    override suspend fun signUp(email: String, password: String): AuthState {
        _authState.value = AuthState.SignedIn(userId = "mock-user-1", email = email)
        return _authState.value
    }

    override suspend fun logout() {
        _authState.value = AuthState.SignedOut
    }
}
