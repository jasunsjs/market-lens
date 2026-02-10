package ca.uwaterloo.market_lens.ui.stock

import android.graphics.Paint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ca.uwaterloo.market_lens.navigation.Routes
import ca.uwaterloo.market_lens.ui.theme.*
import java.util.Locale
import kotlin.math.pow

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

    val chartData = remember {
        listOf(150f, 155f, 152f, 158f, 165f, 162f, 170f, 175f, 172f, 180f,
            150f, 100f, 110f, 60f, 50f, 100f, 150f, 155f, 152f, 158f,
            180f, 200f, 190f, 195f, 210f, 220f, 200f, 240f, 220f)
    }

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
            PriceChart(chartData)
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

@Composable
fun PriceChart(chartData: List<Float>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MarketCardBlack),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("30-Day Price History", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(20.dp))

            Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (chartData.size < 2) {
                        return@Canvas
                    }
                    val yAxisWidth = 120f
                    val xAxisHeight = 60f
                    val chartAreaWidth = size.width - yAxisWidth
                    val chartAreaHeight = size.height - xAxisHeight
                    val rawMax = chartData.maxOrNull() ?: 0f
                    val rawMin = chartData.minOrNull() ?: 0f
                    val rawRange = rawMax - rawMin

                    // determine appropriate price steps and drawing range
                    val step = calculateNiceStep(rawRange)
                    val displayMin = kotlin.math.floor(rawMin / step) * step
                    val displayMax = kotlin.math.ceil(rawMax / step) * step
                    val range = displayMax - displayMin
                    val paint = Paint().apply {
                        color = TextMuted.toArgb()
                        textSize = 30f
                        textAlign = Paint.Align.RIGHT
                    }

                    //draw y axis
                    val safeRange = if (range <= 0) 1f else range
                    val stepsCount = (safeRange / step).toInt()
                    for (i in 0..stepsCount) {
                        val labelPrice = displayMin + (i * step)
                        val numberOfLabel = ((labelPrice - displayMin) / safeRange)
                        val yPos = chartAreaHeight - numberOfLabel * chartAreaHeight
                        // draw label line
                        drawLine(
                            color = Color.White.copy(alpha = 0.05f),
                            start = androidx.compose.ui.geometry.Offset(yAxisWidth, yPos),
                            end = androidx.compose.ui.geometry.Offset(size.width, yPos),
                            strokeWidth = 1.dp.toPx()
                        )
                        // price labels
                        drawContext.canvas.nativeCanvas.drawText(
                            "$${String.format(Locale.ENGLISH, "%.0f", labelPrice)}",
                            yAxisWidth - 20f,
                            yPos + 10f,
                            paint
                        )
                    }

                    //draw x axis
                    val dateLabels = listOf("1/13", "1/19", "1/26", "2/1", "2/9")
                    paint.textAlign = Paint.Align.CENTER
                    dateLabels.forEachIndexed { index, date ->
                        val xPos = yAxisWidth + (index * (chartAreaWidth / (dateLabels.size - 1)))
                        drawContext.canvas.nativeCanvas.drawText(date, xPos, size.height - 5f, paint)
                    }

                    //draw graph in clipRect to force line within axes
                    clipRect(left = yAxisWidth, top = 0f,
                        right = size.width,
                        bottom = chartAreaHeight
                    ) {
                        val spacing = chartAreaWidth / (chartData.size - 1)
                        val points = chartData.indices.map { i ->
                            val x = yAxisWidth + (i * spacing)
                            val y = chartAreaHeight - ((chartData[i] - displayMin) / safeRange) * chartAreaHeight
                            androidx.compose.ui.geometry.Offset(x, y)
                        }
                        val strokePath = Path().apply {
                            moveTo(points.first().x, points.first().y)
                            points.forEach { lineTo(it.x, it.y) }
                        }
                        val fillPath = Path().apply {
                            addPath(strokePath)
                            lineTo(points.last().x, chartAreaHeight)
                            lineTo(yAxisWidth, chartAreaHeight)
                            close()
                        }
                        //gradient under stock line
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(MarketGreen.copy(alpha = 0.3f), Color.Transparent),
                                endY = chartAreaHeight
                            )
                        )
                        //stock line
                        drawPath(
                            path = strokePath,
                            color = MarketGreen,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
            }
        }
    }
}

// fix price labels to ensure steps are multiples 1, 2, 5, or 10
private fun calculateNiceStep(range: Float): Float {
    val targetSteps = 4f
    val rawStep = range / targetSteps
    if (rawStep == 0f) {
        return 1f
    }
    val exponent = kotlin.math.floor(kotlin.math.log10(rawStep.toDouble())).toFloat()
    val magnitude = 10f.pow(exponent)
    val fraction = rawStep / magnitude
    val niceFraction = when {
        fraction < 1.5f -> 1f
        fraction < 3f -> 2f
        fraction < 7f -> 5f
        else -> 10f
    }
    return niceFraction * magnitude
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
