package ca.uwaterloo.market_lens.domain.model

data class User(
    val id: String,
    val email: String,
    val displayName: String? = null
)
