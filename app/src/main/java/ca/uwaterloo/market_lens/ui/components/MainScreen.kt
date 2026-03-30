package ca.uwaterloo.market_lens.ui.components

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import ca.uwaterloo.market_lens.navigation.Routes
import ca.uwaterloo.market_lens.ui.events.NotificationHelper
import ca.uwaterloo.market_lens.ui.events.SimulationManager
import kotlinx.coroutines.flow.collectLatest
import android.util.Log


@Composable
fun MainScreen(
    navController: NavController,
    content: @Composable (Modifier) -> Unit
) {
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showTopBar = currentRoute in listOf(Routes.PORTFOLIO, Routes.ALERTS, Routes.EVENTS)
            || currentRoute?.startsWith("event_overview") == true
    val showBottomBar = currentRoute in listOf(Routes.PORTFOLIO, Routes.ALERTS, Routes.EVENTS)

    // Permission Launcher for Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                NotificationHelper.onPermissionGranted()
            }
        }
    )

    LaunchedEffect(Unit) {
        // Request notification permission if on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationHelper.hasPermission(context)) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        SimulationManager.latestTriggeredEvent.collectLatest { event ->
            NotificationHelper.showEventNotification(context, event)
            Log.d("MainScreen", "Showing notification for event: $event")
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (showTopBar) {
                TopBar(navController)
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
