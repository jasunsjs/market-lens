package ca.uwaterloo.market_lens.ui.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ca.uwaterloo.market_lens.navigation.Routes
import ca.uwaterloo.market_lens.ui.theme.*

@Composable
fun PortfolioScreen(
    navController: NavController,
    viewModel: PortfolioViewModel = viewModel()
) {
    var tickerInput by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

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
                        StockCard(
                            ticker = position.tickerKey,
                            price = if (quote != null) String.format("$%,.2f", quote.price) else "...",
                            change = if (quote != null) String.format("%.2f%%", quote.changePercent) else "...",
                            onDelete = { viewModel.removeStock(position.tickerKey) },
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PortfolioSummaryCard(totalValue: String, netChange: String, netChangePercent: String) {
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
                Text("Net Change", style = MaterialTheme.typography.bodyLarge, color = TextMuted)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.AutoMirrored.Filled.TrendingUp,
                        null, tint = MarketGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        "$netChange $netChangePercent",
                        color = MarketGreen,
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
    price: String,
    change: String,
    onDelete: () -> Unit,
    navController: NavController,
    viewModel: PortfolioViewModel
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MarketCardBlack),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth().clickable {
            viewModel.navigateToStockPage(ticker) {
                navController.navigate(Routes.STOCK)
            }
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(MarketDarkGray),
                contentAlignment = Alignment.Center
            ) {
                Text(text = ticker, color = MarketGreen, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = ticker, style = MaterialTheme.typography.titleMedium)
                Text(text = "Price: $price ($change)", style = MaterialTheme.typography.bodyLarge, color = TextMuted, fontSize = 14.sp)
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Delete", tint = MarketRed)
            }
        }
    }
}
