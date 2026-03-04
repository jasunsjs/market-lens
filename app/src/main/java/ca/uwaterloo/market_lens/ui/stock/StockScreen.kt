package ca.uwaterloo.market_lens.ui.stock

import android.graphics.Paint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ca.uwaterloo.market_lens.domain.model.NewsItem
import ca.uwaterloo.market_lens.domain.model.Sentiment
import ca.uwaterloo.market_lens.domain.model.StockAnalysis
import ca.uwaterloo.market_lens.navigation.Routes
import ca.uwaterloo.market_lens.ui.theme.*
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.pow

@Composable
fun StockScreen(
    ticker: String,
    navController: NavController,
    viewModel: StockViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(ticker) {
        viewModel.loadStockData(ticker)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TextButton(
                onClick = { viewModel.navigateToPortfolioPage { navController.navigate(Routes.PORTFOLIO) } },
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = MarketGreen)
                Spacer(Modifier.width(8.dp))
                Text("Back to Portfolio", color = MarketGreen)
            }
        }

        if (uiState.isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MarketGreen)
                }
            }
        } else {
            uiState.quote?.let { quote ->
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MarketCardBlack), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(quote.tickerKey, style = MaterialTheme.typography.headlineLarge)
                                Text("Real-time Stock Analysis", style = MaterialTheme.typography.bodyLarge)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(String.format("$%,.2f", quote.price), style = MaterialTheme.typography.headlineLarge)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.AutoMirrored.Filled.TrendingUp, null, tint = MarketGreen, modifier = Modifier.size(16.dp))
                                    Text(String.format("%.2f%%", quote.changePercent), color = MarketGreen, style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricCard("Market Cap", formatLargeNumber(quote.marketCap ?: 0, isCurrency = true), modifier = Modifier.weight(1f))
                        MetricCard("Volume", formatLargeNumber(quote.volume ?: 0, isCurrency = false), modifier = Modifier.weight(1f))
                        MetricCard("P/E Ratio", String.format("%.1f", quote.peRatio ?: 0.0), modifier = Modifier.weight(1f))
                    }
                }
            }

            uiState.priceSeries?.let { series ->
                item {
                    PriceChart(chartData = series.points.map { it.close.toFloat() }, 
                               dates = series.points.map { DateTimeFormatter.ofPattern("MM/dd").withZone(ZoneId.systemDefault()).format(it.timestamp) })
                }
            }

            item { Text("Recent News and Events", style = MaterialTheme.typography.titleLarge) }
            
            item { NewsCard(newsItems = uiState.newsItems) }

            uiState.analysis?.let { analysis ->
                item { AnalysisCard(ticker = ticker, analysis = analysis) }
            }
        }
        item { Spacer(Modifier.height(40.dp)) }
    }
}

private fun formatLargeNumber(number: Long, isCurrency: Boolean): String {
    val suffix = when {
        number >= 1_000_000_000_000L -> String.format("%.1fT", number / 1_000_000_000_000.0)
        number >= 1_000_000_000L -> String.format("%.1fB", number / 1_000_000_000.0)
        number >= 1_000_000L -> String.format("%.1fM", number / 1_000_000.0)
        else -> number.toString()
    }
    return if (isCurrency) "$$suffix" else suffix
}

@Composable
fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(colors = CardDefaults.cardColors(containerColor = MarketCardBlack), modifier = modifier.height(100.dp), border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))) {
        Column(modifier = Modifier.padding(12.dp).fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = TextMuted)
            Spacer(Modifier.height(4.dp))
            Text(
                text = value, 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PriceChart(chartData: List<Float>, dates: List<String>) {
    Card(colors = CardDefaults.cardColors(containerColor = MarketCardBlack), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("30-Day Price History", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (chartData.size < 2) return@Canvas
                    val yAxisWidth = 120f
                    val xAxisHeight = 60f
                    val chartAreaWidth = size.width - yAxisWidth
                    val chartAreaHeight = size.height - xAxisHeight
                    val rawMax = chartData.maxOrNull() ?: 0f
                    val rawMin = chartData.minOrNull() ?: 0f
                    val rawRange = rawMax - rawMin
                    val step = calculateNiceStep(rawRange)
                    val displayMin = kotlin.math.floor(rawMin / step) * step
                    val displayMax = kotlin.math.ceil(rawMax / step) * step
                    val range = displayMax - displayMin
                    val paint = Paint().apply { color = TextMuted.toArgb(); textSize = 30f; textAlign = Paint.Align.RIGHT }
                    val safeRange = if (range <= 0) 1f else range
                    val stepsCount = (safeRange / step).toInt()
                    for (i in 0..stepsCount) {
                        val labelPrice = displayMin + (i * step)
                        val numberOfLabel = ((labelPrice - displayMin) / safeRange)
                        val yPos = chartAreaHeight - numberOfLabel * chartAreaHeight
                        drawLine(color = Color.White.copy(alpha = 0.05f), start = androidx.compose.ui.geometry.Offset(yAxisWidth, yPos), end = androidx.compose.ui.geometry.Offset(size.width, yPos), strokeWidth = 1.dp.toPx())
                        drawContext.canvas.nativeCanvas.drawText(String.format("$%.0f", labelPrice), yAxisWidth - 20f, yPos + 10f, paint)
                    }
                    val xLabelsToDisplay = 5
                    val xStep = (dates.size - 1) / (xLabelsToDisplay - 1)
                    paint.textAlign = Paint.Align.CENTER
                    for (i in 0 until xLabelsToDisplay) {
                        val index = i * xStep
                        val xPos = yAxisWidth + (index * (chartAreaWidth / (dates.size - 1)))
                        drawContext.canvas.nativeCanvas.drawText(dates[index], xPos, size.height - 5f, paint)
                    }
                    clipRect(left = yAxisWidth, top = 0f, right = size.width, bottom = chartAreaHeight) {
                        val spacing = chartAreaWidth / (chartData.size - 1)
                        val points = chartData.indices.map { i ->
                            val x = yAxisWidth + (i * spacing)
                            val y = chartAreaHeight - ((chartData[i] - displayMin) / safeRange) * chartAreaHeight
                            androidx.compose.ui.geometry.Offset(x, y)
                        }
                        val strokePath = Path().apply { moveTo(points.first().x, points.first().y); points.forEach { lineTo(it.x, it.y) } }
                        val fillPath = Path().apply { addPath(strokePath); lineTo(points.last().x, chartAreaHeight); lineTo(yAxisWidth, chartAreaHeight); close() }
                        drawPath(path = fillPath, brush = Brush.verticalGradient(colors = listOf(MarketGreen.copy(alpha = 0.3f), Color.Transparent), endY = chartAreaHeight))
                        drawPath(path = strokePath, color = MarketGreen, style = Stroke(width = 2.dp.toPx()))
                    }
                }
            }
        }
    }
}

private fun calculateNiceStep(range: Float): Float {
    val targetSteps = 4f
    val rawStep = range / targetSteps
    if (rawStep == 0f) return 1f
    val exponent = kotlin.math.floor(kotlin.math.log10(rawStep.toDouble())).toFloat()
    val magnitude = 10f.pow(exponent)
    val fraction = rawStep / magnitude
    val niceFraction = when { fraction < 1.5f -> 1f; fraction < 3f -> 2f; fraction < 7f -> 5f; else -> 10f }
    return niceFraction * magnitude
}

@Composable
fun NewsCard(newsItems: List<NewsItem>) {
    Card(colors = CardDefaults.cardColors(containerColor = MarketCardBlack), modifier = Modifier.fillMaxWidth()) {
        Column {
            newsItems.forEachIndexed { index, newsItem ->
                NewsItemRow(newsItem)
                if (index < newsItems.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 1.dp, color = Color.White.copy(alpha = 0.1f))
                }
            }
        }
    }
}

@Composable
fun NewsItemRow(news: NewsItem) {
    Row(modifier = Modifier.padding(16.dp).height(IntrinsicSize.Min)) {
        Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(MarketGreen, shape = RoundedCornerShape(2.dp)))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Text(text = news.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f).padding(end = 8.dp))
                Text(text = DateTimeFormatter.ofPattern("MMM dd").withZone(ZoneId.systemDefault()).format(news.publishedAt), style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }
            Spacer(modifier = Modifier.height(4.dp))
            news.snippet?.let { Text(text = it, style = MaterialTheme.typography.bodyMedium, color = TextMuted) }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Read more", tint = MarketGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Read more on ${news.source.name}", color = MarketGreen, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun AnalysisCard(ticker: String, analysis: StockAnalysis) {
    Card(colors = CardDefaults.cardColors(containerColor = MarketCardGreen), border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "AI Analysis Summary", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextWhite)
            Text(text = analysis.summary, style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp, color = TextWhite.copy(alpha = 0.9f))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(color = MarketGreen.copy(alpha = 0.15f), shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, MarketGreen.copy(alpha = 0.3f))) {
                    Text(text = if (analysis.sentiment == Sentiment.BULLISH) "Bullish Signal" else "Bearish Signal", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelLarge, color = MarketGreen)
                }
                Surface(color = MarketDarkGray, shape = RoundedCornerShape(16.dp)) {
                    Text(text = String.format("Confidence: %.0f%%", analysis.confidence * 100), modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelLarge, color = TextMuted)
                }
            }
        }
    }
}
