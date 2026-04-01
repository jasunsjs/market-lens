package ca.uwaterloo.market_lens.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

private val supabaseFormatter = DateTimeFormatterBuilder()
    .appendPattern("yyyy-MM-dd HH:mm:ss")
    .optionalStart()
    .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
    .optionalEnd()
    .appendPattern("[X][XXXX][XXXXX]")
    .toFormatter()

internal suspend fun SupabaseClient.requireCurrentUserId(): String {
    auth.awaitInitialization()
    return auth.currentUserOrNull()?.id
        ?: throw IllegalStateException("This operation requires an authenticated Supabase user.")
}

internal fun parseInstant(value: String): Instant {
    return try {
        // Try standard ISO first
        Instant.parse(value)
    } catch (e: Exception) {
        try {
            // Try Supabase specific format (replacing space with T and handling timezone)
            val normalized = value.replace(" ", "T")
            // Handle +00 suffix if present
            val finalValue = if (normalized.endsWith("+00")) normalized.replace("+00", "Z") else normalized
            Instant.parse(finalValue)
        } catch (e2: Exception) {
            // Fallback to formatter for complex cases
            val accessor = supabaseFormatter.parse(value)
            Instant.from(accessor)
        }
    }
}
