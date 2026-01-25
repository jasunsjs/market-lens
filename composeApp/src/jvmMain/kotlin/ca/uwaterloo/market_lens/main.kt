package ca.uwaterloo.market_lens

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "market_lens",
    ) {
        App()
    }
}