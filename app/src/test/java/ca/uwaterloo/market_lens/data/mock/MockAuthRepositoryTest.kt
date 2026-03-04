package ca.uwaterloo.market_lens.data.mock

import ca.uwaterloo.market_lens.domain.model.AuthState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class MockAuthRepositoryTest {

    private val repository = MockAuthRepository()

    @Test
    fun observeAuthState_starts_signedOut() = runBlocking {

        val initial = repository.observeAuthState().first()

        assertEquals(AuthState.SignedOut, initial)
    }

    @Test
    fun login_returns_signedIn_and_updates_observed_state() = runBlocking {

        val result = repository.login(email = "user@example.com", password = "password")
        val observed = repository.observeAuthState().first()

        assertTrue(result is AuthState.SignedIn)
        assertTrue(observed is AuthState.SignedIn)

        val signedInResult = result as AuthState.SignedIn
        val signedInObserved = observed as AuthState.SignedIn

        assertEquals("mock-user-1", signedInResult.userId)
        assertEquals("user@example.com", signedInResult.email)
        assertEquals(signedInResult, signedInObserved)
    }

    @Test
    fun logout_sets_state_to_signedOut() = runBlocking {
        repository.login(email = "user@example.com", password = "password")

        repository.logout()

        val observed = repository.observeAuthState().first()
        assertEquals(AuthState.SignedOut, observed)
    }
}
