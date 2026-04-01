package ca.uwaterloo.market_lens.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import java.time.Instant
import java.time.OffsetDateTime
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
        // 1. Try standard ISO Instant (handles 'Z')
        Instant.parse(value)
    } catch (e: Exception) {
        try {
            // 2. Try OffsetDateTime (handles '+00:00', '+00' etc.)
            OffsetDateTime.parse(value).toInstant()
        } catch (e2: Exception) {
            try {
                // 3. Try normalizing space to T (Supabase often uses 'YYYY-MM-DD HH:MM:SS')
                val normalized = value.replace(" ", "T")
                try {
                    OffsetDateTime.parse(normalized).toInstant()
                } catch (e3: Exception) {
                    // Handle cases where Instant.parse might still work with Z substitution
                    val finalValue = if (normalized.endsWith("+00")) normalized.replace("+00", "Z") else normalized
                    Instant.parse(finalValue)
                }
            } catch (e4: Exception) {
                // 4. Fallback to custom formatter for complex cases
                val accessor = try {
                    supabaseFormatter.parse(value)
                } catch (e5: Exception) {
                    // Try parsing with space replaced if it failed due to 'T'
                    supabaseFormatter.parse(value.replace("T", " "))
                }
                Instant.from(accessor)
            }
        }
    }
}
