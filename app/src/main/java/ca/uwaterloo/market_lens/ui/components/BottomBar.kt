package ca.uwaterloo.market_lens.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import ca.uwaterloo.market_lens.navigation.Routes
import ca.uwaterloo.market_lens.ui.theme.*

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val badgeCount: Int? = null
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
        badgeCount = 2
    )
)

@Composable
fun BottomBar(currentRoute: String?, navController: NavController) {
    NavigationBar(
        containerColor = MarketBarBlack
    ) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Routes.PORTFOLIO) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    if (item.badgeCount != null) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = MarketRed,
                                    contentColor = TextWhite
                                ) {
                                    Text(text = item.badgeCount.toString())
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
