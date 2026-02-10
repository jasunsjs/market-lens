package ca.uwaterloo.market_lens.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ca.uwaterloo.market_lens.ui.components.MainScreen
import ca.uwaterloo.market_lens.ui.ai_event.AIEventScreen
import ca.uwaterloo.market_lens.ui.alerts.AlertsScreen
import ca.uwaterloo.market_lens.ui.events.EventsScreen
import ca.uwaterloo.market_lens.ui.login.LoginScreen
import ca.uwaterloo.market_lens.ui.portfolio.PortfolioScreen
import ca.uwaterloo.market_lens.ui.portfolio.PortfolioViewModel
import ca.uwaterloo.market_lens.ui.stock.StockScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val portfolioViewModel: PortfolioViewModel = viewModel()
    MainScreen(navController = navController) { contentModifier ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = contentModifier
        ) {
            composable(Routes.LOGIN) { LoginScreen(navController) }
            composable(Routes.PORTFOLIO) { PortfolioScreen(navController, portfolioViewModel) }
            composable(Routes.ALERTS) { AlertsScreen() }
            composable(Routes.STOCK) { StockScreen("AAPL", navController) }
            composable(Routes.EVENTS) { EventsScreen() }
            composable(Routes.AI) { AIEventScreen() }
        }
    }
}
