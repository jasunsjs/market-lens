package ca.uwaterloo.market_lens.data.supabase

import ca.uwaterloo.market_lens.domain.model.AuthState
import ca.uwaterloo.market_lens.domain.repository.AuthRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SupabaseAuthRepository : AuthRepository {
    private val client = SupabaseClientProvider.client

    override fun observeAuthState(): Flow<AuthState> =
        client.auth.sessionStatus.map { status ->
            when (status) {
                SessionStatus.Initializing -> AuthState.Loading
                is SessionStatus.NotAuthenticated -> AuthState.SignedOut
                is SessionStatus.Authenticated -> {
                    val user = status.session.user
                    AuthState.SignedIn(
                        userId = user?.id.orEmpty(),
                        email = user?.email
                    )
                }

                is SessionStatus.RefreshFailure -> AuthState.Error("Session refresh failed")
            }
        }

    override suspend fun login(email: String, password: String): AuthState {
        return try {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val user = client.auth.currentUserOrNull()
            if (user != null) {
                AuthState.SignedIn(userId = user.id, email = user.email)
            } else {
                AuthState.Error("Login succeeded but no user session was available.")
            }
        } catch (e: Exception) {
            AuthState.Error(e.message ?: "Login failed")
        }
    }

    override suspend fun signUp(email: String, password: String): AuthState {
        return try {
            client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            val user = client.auth.currentUserOrNull()
            if (user != null) {
                AuthState.SignedIn(userId = user.id, email = user.email)
            } else {
                AuthState.Error("Account created. Confirm your email, then log in.")
            }
        } catch (e: Exception) {
            AuthState.Error(e.message ?: "Sign up failed")
        }
    }

    override suspend fun logout() {
        client.auth.signOut()
    }
}
