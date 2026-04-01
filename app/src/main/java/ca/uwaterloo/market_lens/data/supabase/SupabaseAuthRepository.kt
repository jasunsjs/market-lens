package ca.uwaterloo.market_lens.data.supabase

import ca.uwaterloo.market_lens.domain.model.AuthState
import ca.uwaterloo.market_lens.domain.repository.AuthRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.CancellationException

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
        } // ...

    override suspend fun login(email: String, password: String): AuthState {
        return try {
            // Explicitly sign out to clear any stale session
            try { client.auth.signOut() } catch (e: Exception) { /* ignore */ }

            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            // Verify session was actually established
            val user = client.auth.currentUserOrNull()
            if (user != null) {
                AuthState.SignedIn(userId = user.id, email = user.email)
            } else {
                AuthState.Error("Login succeeded but no user session was available.")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Supabase throws here on invalid credentials (400 Bad Request)
            AuthState.Error(e.message ?: "Invalid login credentials")
        }
    }

    override suspend fun signUp(email: String, password: String): AuthState {
        return try {
            // Clear existing session before sign up
            try { client.auth.signOut() } catch (e: Exception) { /* ignore */ }

            client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            val user = client.auth.currentUserOrNull()
            if (user != null) {
                AuthState.SignedIn(userId = user.id, email = user.email)
            } else {
                AuthState.Error("Account created. Please check your email for a confirmation link.")
            }
        } catch (e: Exception) {
            AuthState.Error(e.message ?: "Sign up failed")
        }
    }

    override suspend fun logout() {
        client.auth.signOut()
    }
}
