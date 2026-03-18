package ca.uwaterloo.market_lens.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import java.time.Instant

internal suspend fun SupabaseClient.requireCurrentUserId(): String {
    auth.awaitInitialization()
    return auth.currentUserOrNull()?.id
        ?: throw IllegalStateException("This operation requires an authenticated Supabase user.")
}

internal fun parseInstant(value: String): Instant = Instant.parse(value)
