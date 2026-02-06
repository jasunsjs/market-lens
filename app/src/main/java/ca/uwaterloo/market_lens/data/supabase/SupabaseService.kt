package ca.uwaterloo.market_lens.data.supabase

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow

class SupabaseService {

    private val client = SupabaseClientProvider.client

    /**
     * Signs in the user with email and password.
     * Returns a Result containing Unit on success, or the Exception on failure.
     */
    suspend fun login(email: String, password: String): Result<Unit> {
        return runCatching {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
        }
    }

    /**
     * Signs up a new user.
     */
    suspend fun signUp(email: String, password: String): Result<Unit> {
        return runCatching {
            client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
        }
    }

    /**
     * Signs out the current user.
     */
    suspend fun logout() {
        try {
            client.auth.signOut()
        } catch (e: Exception) {
            // Log error if needed, but usually safe to ignore on logout
            e.printStackTrace()
        }
    }

    /**
     * Checks if a user is currently logged in.
     */
    fun isUserLoggedIn(): Boolean {
        return client.auth.currentSessionOrNull() != null
    }

    /**
     * Returns a Flow that emits the current authentication status.
     * Use this in your ViewModels to react to login/logout events automatically.
     */
    fun getSessionStatusFlow(): Flow<SessionStatus> {
        return client.auth.sessionStatus
    }

    suspend fun fetchPortfolio() {
        // We will implement this once we define database schema
    }
}