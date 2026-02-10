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
import androidx.compose.ui.text.input.KeyboardType
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
    var weightInput by remember { mutableStateOf("") }

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
            // page header
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
            AddStockSection(
                ticker = tickerInput,
                weight = weightInput,
                onTickerChange = { tickerInput = it },
                onWeightChange = { weightInput = it },
                onAddClick = {
                    if (tickerInput.isNotEmpty()) {
                        viewModel.addStock(
                            StockItemInfo(
                                id = System.currentTimeMillis().toString(),
                                ticker = tickerInput.uppercase(),
                                weight = weightInput.ifEmpty { "0" }
                            )
                        )
                        tickerInput = ""
                        weightInput = ""
                    }
                }
            )
            //stock list
            Spacer(modifier = Modifier.height(24.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f) // Takes up remaining space
            ) {
                items(viewModel.stockList) { stock ->
                    StockCard(
                        stock = stock,
                        onDelete = { viewModel.removeStock(stock) },
                        navController,
                        viewModel
                    )
                }
            }
        }
    }
}

// -- PORTFOLIO STOCK ADD/DELETE --//
@Composable
fun AddStockSection(
    ticker: String,
    weight: String,
    onTickerChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // stock name
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

        // stock weight
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Weight %",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = weight,
                onValueChange = onWeightChange,
                placeholder = { Text("Optional", color = TextMuted) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

        // add stocks
        Column {
            Spacer(modifier = Modifier.height(22.dp)) // Align with text fields
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

// -- INDIVIDUAL STOCK CARDS --//
@Composable
fun StockCard(
    stock: StockItemInfo,
    onDelete: () -> Unit,
    navController: NavController,
    viewModel: PortfolioViewModel
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MarketCardBlack
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth().clickable() {
            viewModel.navigateToStockPage(stock) {
                navController.navigate(Routes.STOCK)
            }
        }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MarketDarkGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stock.ticker,
                    color = MarketGreen,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stock.ticker,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Weight: ${stock.weight}%",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextMuted,
                    fontSize = 14.sp
                )
            }

            // delete stocks
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = MarketRed
                )
            }
        }
    }
}
