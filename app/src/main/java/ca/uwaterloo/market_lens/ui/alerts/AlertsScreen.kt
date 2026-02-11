package ca.uwaterloo.market_lens.ui.alerts

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.uwaterloo.market_lens.ui.theme.*

@Composable
fun AlertsScreen(viewModel: AlertsViewModel = viewModel()) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp)
    ) {
        Text(
            text = "Alert Configuration",
            style = MaterialTheme.typography.headlineLarge,
            color = TextWhite
        )
        Text(
            text = "Control notification thresholds",
            style = MaterialTheme.typography.bodyLarge,
            color = TextMuted,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )
    }
}

@Composable
private fun AlertConfigCard() {

}
