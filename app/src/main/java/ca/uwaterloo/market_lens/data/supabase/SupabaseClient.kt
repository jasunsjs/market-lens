package ca.uwaterloo.market_lens.data.supabase

import ca.uwaterloo.market_lens.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import kotlin.time.Duration.Companion.seconds // Make sure to add this import

object SupabaseClientProvider {
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        requestTimeout = 30.seconds

        install(Auth)
        install(Postgrest)
    }
}