package ca.uwaterloo.market_lens.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ca.uwaterloo.market_lens.navigation.Routes

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.error.collectAsState()

    AuthFormScreen(
        subtitle = "Log in to view your portfolio",
        primaryButtonText = "LOG IN",
        isLoading = isLoading,
        errorMessage = errorMessage,
        onPrimaryClick = { email, password ->
            viewModel.login(email, password) {
                navController.navigate(Routes.PORTFOLIO) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
        },
        secondaryButtonText = "SIGN UP",
        onSecondaryClick = { navController.navigate(Routes.SIGNUP) }
    )
}
