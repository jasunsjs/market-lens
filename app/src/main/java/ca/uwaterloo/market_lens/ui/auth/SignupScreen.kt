package ca.uwaterloo.market_lens.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ca.uwaterloo.market_lens.navigation.Routes

@Composable
fun SignupScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.error.collectAsState()

    AuthFormScreen(
        subtitle = "Sign up for a new account",
        primaryButtonText = "SIGN UP",
        isLoading = isLoading,
        errorMessage = errorMessage,
        onPrimaryClick = { email, password ->
            viewModel.signUp(email, password) {
                navController.navigate(Routes.PORTFOLIO) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
        }
    )
}
