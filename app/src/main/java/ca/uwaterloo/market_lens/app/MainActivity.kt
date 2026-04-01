package ca.uwaterloo.market_lens.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ca.uwaterloo.market_lens.di.AppGraph
import ca.uwaterloo.market_lens.domain.model.AuthState
import ca.uwaterloo.market_lens.navigation.NavGraph
import ca.uwaterloo.market_lens.navigation.Routes
import ca.uwaterloo.market_lens.ui.events.NotificationHelper
import ca.uwaterloo.market_lens.ui.events.SimulationManager
import ca.uwaterloo.market_lens.ui.theme.MarketBarBlack
import ca.uwaterloo.market_lens.ui.theme.MarketLensTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(MarketBarBlack.toArgb())
        )
        super.onCreate(savedInstanceState)
        
        NotificationHelper.createNotificationChannel(this)
        
        setContent {
            MarketLensTheme {
                val navController = rememberNavController()
                val authState by AppGraph.authRepository.observeAuthState().collectAsState(initial = AuthState.Loading)
                
                LaunchedEffect(intent, authState) {
                    if (authState is AuthState.Loading) return@LaunchedEffect
                    
                    val eventId = intent.getStringExtra("eventId")
                    if (eventId != null) {
                        // Clear the extra so we don't handle it again on configuration changes
                        intent.removeExtra("eventId")
                        
                        // Register the event so it shows up in the timeline
                        SimulationManager.registerEventId(eventId)
                        
                        if (authState is AuthState.SignedIn) {
                            // Clear backstack and build the hierarchy: Portfolio -> Events -> EventOverview
                            navController.navigate(Routes.PORTFOLIO) {
                                popUpTo(0) { inclusive = true }
                            }
                            navController.navigate(Routes.EVENTS)
                            navController.navigate("event_overview/$eventId")
                        } else {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                }

                NavGraph(navController = navController)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}
