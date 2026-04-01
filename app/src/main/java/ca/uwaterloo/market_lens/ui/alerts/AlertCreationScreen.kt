package ca.uwaterloo.market_lens.ui.alerts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ca.uwaterloo.market_lens.domain.model.AlertType
import ca.uwaterloo.market_lens.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAlertScreen(
    navController: NavController,
    viewModel: AlertsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var tickerExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Create Alert", color = TextWhite) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MarketGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MarketBarBlack)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MarketCardBlack),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    val errorMessage = uiState.errorMessage
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Ticker Selector
                    Text(text = "Select Stock from Portfolio", style = MaterialTheme.typography.labelLarge, color = TextWhite)
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = tickerExpanded,
                        onExpandedChange = { tickerExpanded = !tickerExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.selectedTicker,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tickerExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MarketDarkGray,
                                unfocusedContainerColor = MarketDarkGray,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(expanded = tickerExpanded, onDismissRequest = { tickerExpanded = false }) {
                            uiState.portfolioTickers.forEach { ticker ->
                                DropdownMenuItem(text = { Text(ticker) }, onClick = { viewModel.onTickerSelected(ticker); tickerExpanded = false })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Alert Type Selector
                    Text(text = "Alert Type", style = MaterialTheme.typography.labelLarge, color = TextWhite)
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = !typeExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = when(uiState.selectedType) {
                                AlertType.PRICE_CHANGE -> "Price Change"
                                AlertType.VOLUME_SPIKE -> "Volume Spike"
                                AlertType.EARNINGS_ANNOUNCEMENT -> "Earnings Announcement"
                            },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MarketDarkGray,
                                unfocusedContainerColor = MarketDarkGray,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                            AlertType.entries.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(when(type) {
                                        AlertType.PRICE_CHANGE -> "Price Change"
                                        AlertType.VOLUME_SPIKE -> "Volume Spike"
                                        AlertType.EARNINGS_ANNOUNCEMENT -> "Earnings Announcement"
                                    }) },
                                    onClick = { viewModel.onTypeSelected(type); typeExpanded = false }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Threshold Slider
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Threshold", style = MaterialTheme.typography.labelLarge, color = TextWhite)
                        Text(text = formatRuleLabel(uiState.selectedType, uiState.threshold), style = MaterialTheme.typography.labelLarge, color = MarketGreen)
                    }
                    Slider(
                        value = uiState.threshold.toFloat(),
                        onValueChange = { viewModel.onThresholdChanged(it.toDouble()) },
                        valueRange = 1f..20f,
                        colors = SliderDefaults.colors(thumbColor = MarketGreen, activeTrackColor = MarketGreen)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { 
                            viewModel.addAlert { success ->
                                if (success) {
                                    navController.popBackStack()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MarketGreen, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Save Alert Configuration", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
