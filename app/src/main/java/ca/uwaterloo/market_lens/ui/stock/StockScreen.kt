package ca.uwaterloo.market_lens.ui.stock

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ca.uwaterloo.market_lens.navigation.Routes
import ca.uwaterloo.market_lens.ui.portfolio.PortfolioTopBar
import ca.uwaterloo.market_lens.ui.theme.*

@Composable
fun StockScreen(
    ticker: String,
    navController: NavController,
    viewModel: StockViewModel = viewModel()
) {
    Scaffold(
        containerColor = MarketBlack,
        topBar = { PortfolioTopBar(navController = navController, viewModel()) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Back Button
                TextButton(
                    onClick = {
                        viewModel.navigateToPortfolioPage {
                            navController.navigate(Routes.PORTFOLIO)
                        }
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = MarketGreen)
                    Spacer(Modifier.width(8.dp))
                    Text("Back to Portfolio", color = MarketGreen)
                }
            }

            // Header Section
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MarketCardBlack),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(ticker, style = MaterialTheme.typography.headlineLarge)
                            Text("Real-time Stock Analysis", style = MaterialTheme.typography.bodyLarge)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("$162.24", style = MaterialTheme.typography.headlineLarge)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TrendingUp, null, tint = MarketGreen, modifier = Modifier.size(16.dp))
                                Text("+3.02 (+1.90%)", color = MarketGreen, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }
            // Metrics Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard("Market Cap", "$380.5B", modifier = Modifier.weight(1f))
                    MetricCard("Volume", "20.2M", modifier = Modifier.weight(1f))
                    MetricCard("P/E Ratio", "31.4", modifier = Modifier.weight(1f))
                }
            }
            // Chart and graphs
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MarketCardBlack),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("30-Day Price History", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(20.dp))

                        // Placeholder for your chart library/custom drawing
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .background(Color.DarkGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Chart Visualization Here", color = TextMuted)
                        }
                    }
                }
            }
            item {
                Text(
                    "Recent News & Events",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            // keep as extra scroll room
            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

@Composable
fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MarketCardBlack),
        modifier = modifier.height(120.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = TextMuted)
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}