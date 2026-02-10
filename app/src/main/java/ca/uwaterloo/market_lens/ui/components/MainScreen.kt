package ca.uwaterloo.market_lens.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import ca.uwaterloo.market_lens.navigation.Routes

@Composable
fun MainScreen(
    navController: NavController,
    content: @Composable (Modifier) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showTopBar = currentRoute in listOf(Routes.PORTFOLIO, Routes.ALERTS, Routes.EVENTS)
            || currentRoute?.startsWith("event_overview") == true
    val showBottomBar = currentRoute in listOf(Routes.PORTFOLIO, Routes.ALERTS, Routes.EVENTS)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (showTopBar) {
                TopBar()
            }
        },
        bottomBar = {
            if (showBottomBar) {
                BottomBar(currentRoute = currentRoute, navController = navController)
            }
        }
    ) { paddingValues ->
        content(Modifier.padding(paddingValues))
    }
}
