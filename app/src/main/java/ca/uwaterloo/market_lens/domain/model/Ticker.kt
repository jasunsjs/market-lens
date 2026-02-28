package ca.uwaterloo.market_lens.domain.model

data class Ticker(
    val symbol: String,
    val name: String? = null
) {
    val tickerKey: String = symbol
}