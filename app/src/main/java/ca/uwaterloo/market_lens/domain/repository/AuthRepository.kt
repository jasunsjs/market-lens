package ca.uwaterloo.market_lens.domain.repository

import ca.uwaterloo.market_lens.domain.model.AuthState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observeAuthState(): Flow<AuthState>
    suspend fun login(email: String, password: String): AuthState
    suspend fun logout()
}