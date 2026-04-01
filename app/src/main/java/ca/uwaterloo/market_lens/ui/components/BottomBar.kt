package ca.uwaterloo.market_lens.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import ca.uwaterloo.market_lens.navigation.Routes
import ca.uwaterloo.market_lens.ui.events.EventsViewModel
import ca.uwaterloo.market_lens.ui.theme.*

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val hasBadge: Boolean = false
)

private val bottomNavItems = listOf(
    BottomNavItem(
        route = Routes.PORTFOLIO,
        label = "Portfolio",
        icon = Icons.AutoMirrored.Filled.ShowChart
    ),
    BottomNavItem(
        route = Routes.ALERTS,
        label = "Alerts",
        icon = Icons.Default.Notifications
    ),
    BottomNavItem(
        route = Routes.EVENTS,
        label = "Timeline",
        icon = Icons.Default.Schedule,
        hasBadge = true
    )
)

@Composable
fun BottomBar(
    currentRoute: String?, 
    navController: NavController,
    eventsViewModel: EventsViewModel = viewModel()
) {
    val eventsUiState by eventsViewModel.uiState.collectAsState()
    val eventCount = eventsUiState.events.size

    NavigationBar(
        containerColor = MarketBarBlack
    ) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    if (item.hasBadge && eventCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = MarketRed,
                                    contentColor = TextWhite
                                ) {
                                    Text(text = eventCount.toString())
                                }
                            }
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        }
                    } else {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label
                        )
                    }
                },
                label = { Text(text = item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MarketGreen,
                    selectedTextColor = MarketGreen,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted,
                    indicatorColor = MarketGreen.copy(alpha = 0.12f)
                )
            )
        }
    }
}
