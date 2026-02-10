package ca.uwaterloo.market_lens.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ca.uwaterloo.market_lens.ui.components.MainScreen
import ca.uwaterloo.market_lens.ui.ai_event.AIEventScreen
import ca.uwaterloo.market_lens.ui.alerts.AlertsScreen
import ca.uwaterloo.market_lens.ui.events.EventOverviewScreen
import ca.uwaterloo.market_lens.ui.events.EventsScreen
import ca.uwaterloo.market_lens.ui.login.LoginScreen
import ca.uwaterloo.market_lens.ui.portfolio.PortfolioScreen
import ca.uwaterloo.market_lens.ui.stock.StockScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    MainScreen(navController = navController) { contentModifier ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = contentModifier
        ) {
            composable(Routes.LOGIN) { LoginScreen(navController) }
            composable(Routes.PORTFOLIO) { PortfolioScreen(navController) }
            composable(Routes.ALERTS) { AlertsScreen() }
            composable(Routes.STOCK) { StockScreen() }
            composable(Routes.EVENTS) { EventsScreen(navController) }
            composable(
                Routes.EVENT_OVERVIEW,
                arguments = listOf(navArgument("eventId") { type = NavType.StringType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
                EventOverviewScreen(eventId = eventId, navController = navController)
            }
            composable(Routes.AI) { AIEventScreen() }
        }
    }
}
