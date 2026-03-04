package ca.uwaterloo.market_lens.ui.alerts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ca.uwaterloo.market_lens.domain.model.AlertRule
import ca.uwaterloo.market_lens.navigation.Routes
import ca.uwaterloo.market_lens.ui.theme.*

@Composable
fun AlertsScreen(
    viewModel: AlertsViewModel = viewModel(),
    navController: NavController? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Portfolio Alerts", style = MaterialTheme.typography.headlineLarge, color = TextWhite)
                Text(text = "Manage your notification rules", style = MaterialTheme.typography.bodyLarge, color = TextMuted)
            }
            
            Button(
                onClick = { navController?.navigate(Routes.CREATE_ALERT) },
                colors = ButtonDefaults.buttonColors(containerColor = MarketGreen, contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add", fontWeight = FontWeight.Bold, color = MarketBlack)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MarketGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                if (uiState.alertRules.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                            Text("No active alerts for your portfolio", color = TextMuted)
                        }
                    }
                } else {
                    items(uiState.alertRules) { rule ->
                        AlertRuleCard(
                            rule = rule,
                            onEnabledChange = { enabled -> viewModel.onAlertEnabledChanged(rule.id, enabled) },
                            onDelete = { viewModel.deleteAlertRule(rule.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AlertRuleCard(rule: AlertRule, onEnabledChange: (Boolean) -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(), 
        shape = RoundedCornerShape(12.dp), 
        colors = CardDefaults.cardColors(containerColor = MarketCardBlack), 
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = rule.tickerKey, style = MaterialTheme.typography.titleLarge, color = MarketGreen, fontWeight = FontWeight.Bold)
                    Text(text = formatRuleLabel(rule.alertType, rule.threshold), style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                }
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MarketRed)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth().background(MarketDarkGray, RoundedCornerShape(12.dp)).padding(horizontal = 14.dp, vertical = 12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = if (rule.enabled) MarketGreen else TextMuted)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = if (rule.enabled) "Alerts Enabled" else "Alerts Disabled", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, color = TextWhite, fontWeight = FontWeight.SemiBold)
                    Switch(checked = rule.enabled, onCheckedChange = onEnabledChange, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MarketGreen, uncheckedThumbColor = TextMuted, uncheckedTrackColor = MarketCardBlack, uncheckedBorderColor = TextMuted.copy(alpha = 0.5f)))
                }
            }
        }
    }
}
