package ca.uwaterloo.market_lens.ui.stock

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ca.uwaterloo.market_lens.navigation.Routes
import ca.uwaterloo.market_lens.ui.theme.*

// hardcoded news items, can remove later
data class NewsItem(
    val title: String,
    val source: String,
    val time: String,
    val description: String,
    val sentiment: Sentiment
)

//determines color of sidebar
enum class Sentiment {
    POSITIVE, NEGATIVE, NEUTRAL
}

@Composable
fun StockScreen(
    ticker: String,
    navController: NavController,
    viewModel: StockViewModel = viewModel()
) {
    // placeholder news data
    val newsItems = listOf(
        NewsItem(
            title = "AAPL Announces New Product Line",
            time = "2 hours ago",
            description = "Company reveals innovative solutions targeting enterprise customers, expected to drive Q4 revenue growth.",
            source = "Bloomberg",
            sentiment = Sentiment.POSITIVE
        ),
        NewsItem(
            title = "Analyst Upgrades Price Target",
            time = "5 hours ago",
            description = "Major investment bank raises price target by 15% citing strong fundamentals and market position.",
            source = "Reuters",
            sentiment = Sentiment.POSITIVE
        ),
        NewsItem(
            title = "Sector Faces Regulatory Scrutiny",
            time = "1 day ago",
            description = "New proposed regulations could impact operations, though long-term effects remain uncertain.",
            source = "Financial Times",
            sentiment = Sentiment.NEGATIVE
        ),
        NewsItem(
            title = "Q3 Earnings Beat Expectations",
            time = "3 days ago",
            description = "Revenue exceeded analyst forecasts with strong performance across all business segments.",
            source = "CNBC",
            sentiment = Sentiment.POSITIVE
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
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

        // header
        item {
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
                        Text(ticker, style = MaterialTheme.typography.headlineLarge)
                        Text("Real-time Stock Analysis", style = MaterialTheme.typography.bodyLarge)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("$162.24", style = MaterialTheme.typography.headlineLarge)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.AutoMirrored.Filled.TrendingUp,
                                null, tint = MarketGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "+3.02 (+1.90%)",
                                color = MarketGreen,
                                style = MaterialTheme.typography.bodyLarge
                            )
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
                    //placeholder chart
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
        //news and events
        item {
            Text("Recent News and Events", style = MaterialTheme.typography.titleLarge)
        }
        item {
            NewsCard(newsItems = newsItems)
        }
        item {
            AnalysisCard(ticker = ticker)
        }
        item { Spacer(Modifier.height(40.dp)) }
    }
}

@Composable
fun NewsCard(newsItems: List<NewsItem>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MarketCardBlack),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            newsItems.forEachIndexed { index, newsItem ->
                NewsItemRow(newsItem)
                if (index < newsItems.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 1.dp,
                        color = Color.White.copy(alpha = 0.1f)
                    )
                }
            }
        }
    }
}

@Composable
fun NewsItemRow(news: NewsItem) {
    val sentimentColor = when (news.sentiment) {
        Sentiment.POSITIVE -> MarketGreen
        Sentiment.NEGATIVE -> Color.Gray
        Sentiment.NEUTRAL -> Color.Gray
    }

    Row(
        modifier = Modifier
            .padding(16.dp)
            .height(IntrinsicSize.Min)
    ) { // stretch items to fill intrinsic space
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(sentimentColor, shape = RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                Text(
                    text = news.time,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = news.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = "Read more",
                    tint = MarketGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Read more on ${news.source}",
                    color = MarketGreen,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun AnalysisCard(ticker: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MarketCardGreen
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "AI Analysis Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Text(
                text = "Based on recent market activity, $ticker is showing positive" +
                        " momentum with a 0.02% change. Technical indicators suggest" +
                        " bullish sentiment, while fundamental analysis reveals strong market positioning. Analysts maintain a generally favorable outlook with an average price target of $139.70.",
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                color = TextWhite.copy(alpha = 0.9f)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // bear or bull signal
                Surface(
                    color = MarketGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MarketGreen.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "Bullish Signal",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MarketGreen
                    )
                }
                //confidence metric
                Surface(
                    color = MarketDarkGray,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Confidence: 79%",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = TextMuted
                    )
                }
            }
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
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = TextMuted)
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}
