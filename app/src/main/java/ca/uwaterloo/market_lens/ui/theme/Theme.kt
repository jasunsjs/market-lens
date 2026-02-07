package ca.uwaterloo.market_lens.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}