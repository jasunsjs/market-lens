package ca.uwaterloo.market_lens.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ca.uwaterloo.market_lens.ui.components.MainScreen
import ca.uwaterloo.market_lens.ui.auth.LoginScreen
import ca.uwaterloo.market_lens.ui.auth.SignupScreen
import ca.uwaterloo.market_lens.ui.ai_event.AIEventScreen
import ca.uwaterloo.market_lens.ui.alerts.AlertsScreen
import ca.uwaterloo.market_lens.ui.alerts.AlertsViewModel
import ca.uwaterloo.market_lens.ui.alerts.CreateAlertScreen
import ca.uwaterloo.market_lens.ui.events.EventOverviewScreen
import ca.uwaterloo.market_lens.ui.events.EventsScreen
import ca.uwaterloo.market_lens.ui.portfolio.PortfolioScreen
import ca.uwaterloo.market_lens.ui.portfolio.PortfolioViewModel
import ca.uwaterloo.market_lens.ui.stock.StockScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val portfolioViewModel : PortfolioViewModel = viewModel()
    val alertsViewModel : AlertsViewModel = viewModel()

    MainScreen(navController = navController) { contentModifier ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = contentModifier
        ) {
            composable(Routes.LOGIN) { LoginScreen(navController) }
            composable(Routes.SIGNUP) { SignupScreen(navController) }
            composable(Routes.PORTFOLIO) { PortfolioScreen(navController, portfolioViewModel) }
            composable(Routes.ALERTS) { AlertsScreen(alertsViewModel, navController) }
            composable(Routes.CREATE_ALERT) { CreateAlertScreen(navController, alertsViewModel) }
            composable(
                Routes.STOCK,
                arguments = listOf(navArgument("ticker") { type = NavType.StringType })
            ) { backStackEntry ->
                val ticker = backStackEntry.arguments?.getString("ticker") ?: "AAPL"
                StockScreen(ticker, navController)
            }
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
