package ca.uwaterloo.market_lens

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform