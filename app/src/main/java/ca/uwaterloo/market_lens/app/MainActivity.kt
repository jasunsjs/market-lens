package ca.uwaterloo.market_lens.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import ca.uwaterloo.market_lens.navigation.NavGraph
import ca.uwaterloo.market_lens.ui.theme.MarketBarBlack
import ca.uwaterloo.market_lens.ui.theme.MarketLensTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(MarketBarBlack.toArgb())
        )
        super.onCreate(savedInstanceState)
        setContent {
            MarketLensTheme {
                NavGraph()
            }
        }
    }
}
