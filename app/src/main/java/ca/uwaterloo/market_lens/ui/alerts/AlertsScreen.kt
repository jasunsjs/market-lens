package ca.uwaterloo.market_lens.ui.alerts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.uwaterloo.market_lens.ui.theme.*

@Composable
fun AlertsScreen(viewModel: AlertsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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

        AlertConfigCard(
            uiState = uiState,
            onAlertTypeSelected = viewModel::onAlertTypeSelected,
            onThresholdChanged = viewModel::onThresholdChanged,
            onAlertsEnabledChanged = viewModel::onAlertsEnabledChanged,
            onSaveClick = viewModel::saveConfiguration
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertConfigCard(
    uiState: AlertConfigUiState,
    onAlertTypeSelected: (AlertType) -> Unit,
    onThresholdChanged: (Float) -> Unit,
    onAlertsEnabledChanged: (Boolean) -> Unit,
    onSaveClick: () -> Unit
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    var thresholdInput by remember(uiState.threshold, uiState.alertType) {
        mutableStateOf(formatThresholdValue(uiState.threshold))
    }

    val whiteInputTextStyle = TextStyle(color = TextWhite)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MarketCardBlack),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Alert Type",
                style = MaterialTheme.typography.labelLarge,
                color = TextWhite,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = !dropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.alertType.label,
                    onValueChange = {},
                    readOnly = true,
                    textStyle = whiteInputTextStyle,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MarketDarkGray,
                        unfocusedContainerColor = MarketDarkGray,
                        focusedBorderColor = MaterialTheme.colorScheme.outline,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        disabledTextColor = TextWhite,
                        focusedTrailingIconColor = TextWhite,
                        unfocusedTrailingIconColor = TextWhite
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    AlertType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(text = type.label) },
                            onClick = {
                                onAlertTypeSelected(type)
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = uiState.alertType.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Threshold",
                style = MaterialTheme.typography.labelLarge,
                color = TextWhite,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = thresholdInput,
                    onValueChange = { input ->
                        val cleaned = input.filter { it.isDigit() || it == '.' }
                        thresholdInput = cleaned
                        cleaned.toFloatOrNull()?.let(onThresholdChanged)
                    },
                    textStyle = whiteInputTextStyle,
                    modifier = Modifier.width(160.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MarketDarkGray,
                        unfocusedContainerColor = MarketDarkGray,
                        focusedBorderColor = MaterialTheme.colorScheme.outline,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = MarketGreen
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = uiState.alertType.unitFormatter(uiState.threshold),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextWhite
                )
            }

            Slider(
                value = uiState.threshold,
                onValueChange = onThresholdChanged,
                valueRange = uiState.alertType.minThreshold..uiState.alertType.maxThreshold,
                colors = SliderDefaults.colors(
                    thumbColor = MarketGreen,
                    activeTrackColor = MarketGreen,
                    inactiveTrackColor = TextMuted.copy(alpha = 0.4f),
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent
                ),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MarketDarkGray, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Alerts toggle",
                        tint = if (uiState.alertsEnabled) MarketGreen else TextMuted
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (uiState.alertsEnabled) "Alerts Enabled" else "Alerts Disabled",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (uiState.alertsEnabled) {
                                "You will receive notifications"
                            } else {
                                "No notifications will be sent"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    }

                    Switch(
                        checked = uiState.alertsEnabled,
                        onCheckedChange = onAlertsEnabledChanged,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MarketGreen,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = MarketCardBlack,
                            uncheckedBorderColor = TextMuted.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val saveContentColor = if (uiState.isSavedFeedbackVisible) TextWhite else Color.Black
            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MarketGreen,
                    contentColor = saveContentColor
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Save configuration",
                    tint = saveContentColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (uiState.isSavedFeedbackVisible) "Saved!" else "Save Configuration",
                    color = saveContentColor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
