package ca.uwaterloo.market_lens.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Map your custom colors to Material slots
private val DarkColorScheme = darkColorScheme(
    primary = MarketGreen,
    onPrimary = Color.Black, // Text on green buttons should be black
    primaryContainer = MarketGreen.copy(alpha = 0.2f),
    onPrimaryContainer = MarketGreen,

    secondary = MarketDarkGray,
    onSecondary = TextWhite,

    background = MarketBlack,
    onBackground = TextWhite,

    surface = MarketCardBlack, // Cards are slightly lighter than background
    onSurface = TextWhite,
    onSurfaceVariant = TextMuted, // Subtext on cards

    error = MarketRed,
    onError = TextWhite,

    // Border colors usually map to Outline
    outline = Color(0x1AFFFFFF)
)

@Composable
fun MarketLensTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
