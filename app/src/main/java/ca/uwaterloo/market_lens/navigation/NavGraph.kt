package ca.uwaterloo.market_lens.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
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

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    MainScreen(navController = navController) { contentModifier ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = contentModifier
        ) {
            composable(Routes.LOGIN) { LoginScreen(navController) }
            composable(Routes.SIGNUP) { SignupScreen(navController) }
            composable(Routes.PORTFOLIO) {
                val portfolioViewModel: PortfolioViewModel = viewModel()
                PortfolioScreen(navController, portfolioViewModel)
            }
            composable(Routes.ALERTS) {
                val alertsViewModel: AlertsViewModel = viewModel()
                AlertsScreen(alertsViewModel, navController)
            }
            composable(Routes.CREATE_ALERT) {
                val alertsBackStackEntry = remember(navController) {
                    navController.getBackStackEntry(Routes.ALERTS)
                }
                val alertsViewModel: AlertsViewModel = viewModel(alertsBackStackEntry)
                CreateAlertScreen(navController, alertsViewModel)
            }
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
