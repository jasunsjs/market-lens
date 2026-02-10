package ca.uwaterloo.market_lens.ui.events

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.uwaterloo.market_lens.ui.theme.*

data class TimelineEvent(
    val id: String,
    val ticker: String,
    val percentChange: Double,
    val description: String,
    val timestamp: String
)

@Composable
fun EventsScreen(viewModel: EventsViewModel = viewModel()) {
    val events = viewModel.events

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp)
    ) {
        Text(
            text = "Event Timeline",
            style = MaterialTheme.typography.headlineLarge,
            color = TextWhite
        )
        Text(
            text = "Review triggered alerts",
            style = MaterialTheme.typography.bodyLarge,
            color = TextMuted,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(events) { event ->
                EventCard(event = event)
            }
        }
    }
}

@Composable
private fun EventCard(event: TimelineEvent) {
    val isNegative = event.percentChange < 0
    val trendColor = if (isNegative) MarketRed else MarketGreen
    val trendIcon = if (isNegative) Icons.AutoMirrored.Filled.TrendingDown else Icons.AutoMirrored.Filled.TrendingUp

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MarketCardBlack
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Trend icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(trendColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = trendIcon,
                    contentDescription = null,
                    tint = trendColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Middle: Event details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = event.ticker,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Surface(
                        color = trendColor,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${event.percentChange}%",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = event.timestamp,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Right: Chevron
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextMuted
            )
        }
    }
}
