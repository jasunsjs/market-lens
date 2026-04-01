package ca.uwaterloo.market_lens.ui.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ca.uwaterloo.market_lens.navigation.Routes
import ca.uwaterloo.market_lens.ui.theme.*
import coil.compose.AsyncImage

@Composable
fun PortfolioScreen(
    navController: NavController,
    viewModel: PortfolioViewModel = viewModel()
) {
    var tickerInput by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    var editingTicker by remember { mutableStateOf<String?>(null) }

    if (editingTicker != null) {
        val ticker = editingTicker!!
        val position = uiState.positions.find { it.tickerKey == ticker }
        EditSharesDialog(
            ticker = ticker,
            currentShares = position?.shares,
            currentAvgCost = position?.avgCost,
            onConfirm = { shares, avgCost ->
                viewModel.updateShares(ticker, shares, avgCost)
                editingTicker = null
            },
            onDismiss = { editingTicker = null }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            Text(
                text = "Portfolio",
                style = MaterialTheme.typography.headlineLarge,
                color = TextWhite
            )
            Text(
                text = "Manage your tracked stocks",
                style = MaterialTheme.typography.bodyLarge,
                color = TextMuted,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            PortfolioSummaryCard(
                totalValue = uiState.totalValue,
                netChange = uiState.netChange,
                netChangePercent = uiState.netChangePercent
            )

            val errorMessage = uiState.errorMessage
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            AddStockSection(
                ticker = tickerInput,
                onTickerChange = { tickerInput = it },
                onAddClick = {
                    if (tickerInput.isNotEmpty()) {
                        viewModel.addStock(tickerInput.uppercase())
                        tickerInput = ""
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MarketGreen)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(uiState.positions) { position ->
                        val quote = uiState.quotes[position.tickerKey]
                        val holdingValue = uiState.positionValues[position.tickerKey]
                        val unrealizedGain = if (position.shares != null && position.shares > 0
                            && position.avgCost != null && position.avgCost > 0 && quote != null)
                            position.shares * (quote.price - position.avgCost) else null
                        val unrealizedGainPercent = if (position.avgCost != null && position.avgCost > 0 && quote != null)
                            ((quote.price - position.avgCost) / position.avgCost) * 100.0 else null
                        StockCard(
                            ticker = position.tickerKey,
                            shares = position.shares,
                            avgCost = position.avgCost,
                            price = if (quote != null) String.format("$%,.2f", quote.price) else "...",
                            change = if (quote != null) quote.changePercent else null,
                            logoUrl = quote?.logoUrl,
                            holdingValue = holdingValue,
                            unrealizedGain = unrealizedGain,
                            unrealizedGainPercent = unrealizedGainPercent,
                            onDelete = { viewModel.removeStock(position.tickerKey) },
                            onEdit = { editingTicker = position.tickerKey },
                            onCardClick = {
                                navController.navigate(Routes.stockDetail(position.tickerKey))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditSharesDialog(
    ticker: String,
    currentShares: Double?,
    currentAvgCost: Double?,
    onConfirm: (Double, Double?) -> Unit,
    onDismiss: () -> Unit
) {
    fun Double?.toFieldString() = if (this == null || this == 0.0) "" else toBigDecimal().stripTrailingZeros().toPlainString()

    var sharesInput by remember { mutableStateOf(currentShares.toFieldString()) }
    var avgCostInput by remember { mutableStateOf(currentAvgCost.toFieldString()) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MarketGreen,
        unfocusedBorderColor = TextMuted,
        focusedTextColor = TextWhite,
        unfocusedTextColor = TextWhite,
        cursorColor = MarketGreen,
        focusedLabelColor = MarketGreen,
        unfocusedLabelColor = TextMuted
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MarketCardBlack,
        title = { Text("Edit Position - $ticker", color = TextWhite) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = sharesInput,
                    onValueChange = { sharesInput = it },
                    label = { Text("Number of shares") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = avgCostInput,
                    onValueChange = { avgCostInput = it },
                    label = { Text("Avg cost per share ($)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val shares = sharesInput.toDoubleOrNull() ?: 0.0
                onConfirm(shares, avgCostInput.toDoubleOrNull())
            }) {
                Text("Save", color = MarketGreen)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}

@Composable
fun PortfolioSummaryCard(totalValue: String, netChange: String, netChangePercent: String) {
    val isPositive = !netChange.startsWith("-")
    val changeColor = if (isPositive) MarketGreen else MarketRed

    Card(
        colors = CardDefaults.cardColors(containerColor = MarketCardBlack),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Total Value", style = MaterialTheme.typography.bodyLarge, color = TextMuted)
                Text(totalValue, style = MaterialTheme.typography.headlineLarge)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Total Return", style = MaterialTheme.typography.bodyLarge, color = TextMuted)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                        null, tint = changeColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        "$netChange $netChangePercent",
                        color = changeColor,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun AddStockSection(
    ticker: String,
    onTickerChange: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Ticker Symbol",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = ticker,
                onValueChange = onTickerChange,
                placeholder = { Text("e.g., AAPL", color = TextMuted) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MarketDarkGray,
                    unfocusedContainerColor = MarketDarkGray,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = MarketGreen,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column {
            Spacer(modifier = Modifier.height(22.dp))
            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MarketGreen,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add", fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

@Composable
fun StockCard(
    ticker: String,
    shares: Double?,
    avgCost: Double?,
    price: String,
    change: Double?,
    logoUrl: String?,
    holdingValue: Double?,
    unrealizedGain: Double?,
    unrealizedGainPercent: Double?,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onCardClick: () -> Unit
) {
    val changeColor = when {
        change == null -> TextMuted
        change >= 0 -> MarketGreen
        else -> MarketRed
    }
    val changePrefix = if ((change ?: 0.0) >= 0) "+" else ""

    Card(
        colors = CardDefaults.cardColors(containerColor = MarketCardBlack),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth().clickable { onCardClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(MarketDarkGray),
                contentAlignment = Alignment.Center
            ) {
                if (logoUrl != null && logoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = logoUrl,
                        contentDescription = "$ticker logo",
                        modifier = Modifier.fillMaxSize().padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(text = ticker, color = MarketGreen, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = ticker, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = if (change != null) "$price  ${changePrefix}${String.format("%.2f", change)}%" else "Price: $price",
                    style = MaterialTheme.typography.bodyLarge,
                    color = changeColor,
                    fontSize = 14.sp
                )
                if (holdingValue != null && holdingValue > 0) {
                    val sharesLabel = shares?.let { String.format("%.4g shares", it) } ?: ""
                    Text(
                        text = "${String.format("$%,.2f", holdingValue)}  ·  $sharesLabel",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                } else if (shares == null || shares == 0.0) {
                    Text(
                        text = "No shares recorded",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
                
                if (unrealizedGain != null) {
                    val gainColor = if (unrealizedGain >= 0) MarketGreen else MarketRed
                    val gainPrefix = if (unrealizedGain >= 0) "+" else ""
                    val pctLabel = unrealizedGainPercent?.let { " (${gainPrefix}${String.format("%.2f", it)}%)" } ?: ""
                    Text(
                        text = "${gainPrefix}${String.format("$%,.2f", unrealizedGain)}$pctLabel unrealized",
                        style = MaterialTheme.typography.bodySmall,
                        color = gainColor
                    )
                }
            }
            IconButton(onClick = onEdit) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit position", tint = MarketGreen)
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Delete", tint = MarketRed)
            }
        }
    }
}
