package ca.uwaterloo.market_lens.ui.events

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material3.*
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ca.uwaterloo.market_lens.ui.theme.*
import androidx.core.net.toUri

@Composable
fun EventOverviewScreen(
    eventId: String,
    navController: NavController,
    viewModel: EventsViewModel = viewModel()
) {
    val data = viewModel.getEventData(eventId)

    if (data == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            BackToTimelineRow(navController)
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Event not found", color = TextMuted)
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 24.dp)
    ) {
        BackToTimelineRow(navController)
        Spacer(modifier = Modifier.height(16.dp))
        EventHeaderCard(data)
        Spacer(modifier = Modifier.height(16.dp))
        AiExplanationCard(data.aiExplanation)
        Spacer(modifier = Modifier.height(16.dp))
        ContributingFactorsCard(data.contributingFactors)
        Spacer(modifier = Modifier.height(16.dp))
        OverallConfidenceCard(data.overallConfidence)
    }
}

@Composable
private fun BackToTimelineRow(navController: NavController) {
    Row(
        modifier = Modifier.clickable { navController.popBackStack() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = MarketGreen,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Back to Timeline",
            color = MarketGreen,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EventHeaderCard(data: EventData) {
    val isNegative = data.percentChange < 0
    val trendColor = if (isNegative) MarketRed else MarketGreen
    val trendIcon =
        if (isNegative) Icons.AutoMirrored.Filled.TrendingDown else Icons.AutoMirrored.Filled.TrendingUp

    Card(
        colors = CardDefaults.cardColors(containerColor = MarketCardBlack),
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
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(trendColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = trendIcon,
                    contentDescription = null,
                    tint = trendColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = data.ticker,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Surface(
                        color = trendColor,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${data.percentChange}%",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Text(
                    text = data.timestamp,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun AiExplanationCard(explanation: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MarketCardBlack),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "AI-Generated Explanation",
                style = MaterialTheme.typography.titleLarge,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = explanation,
                style = MaterialTheme.typography.bodyLarge,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun ContributingFactorsCard(factors: List<ContributingFactor>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MarketCardBlack),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Contributing Factors",
                style = MaterialTheme.typography.titleLarge,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(16.dp))

            factors.forEachIndexed { index, factor ->
                ContributingFactorItem(factor)
                if (index < factors.lastIndex) {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun ContributingFactorItem(factor: ContributingFactor) {
    Column {
        // Number circle + title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MarketGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = factor.rank.toString(),
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = factor.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Progress bar + percentage
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MarketDarkGray)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(factor.percentage / 100f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MarketGreen)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "${factor.percentage}%",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Description
        Text(
            text = factor.description,
            style = MaterialTheme.typography.bodyLarge,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Source
        val context = LocalContext.current
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                context.startActivity(Intent(Intent.ACTION_VIEW, factor.sourceUrl.toUri()))
            }
        ) {
            Text(
                text = "Source: ${factor.source}",
                style = MaterialTheme.typography.labelMedium,
                color = MarketGreen
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                contentDescription = null,
                tint = MarketGreen,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun OverallConfidenceCard(confidence: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MarketCardBlack),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Overall Confidence",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Based on source reliability and data correlation",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$confidence%",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MarketGreen
                )
                Text(
                    text = "Confidence Score",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted
                )
            }
        }
    }
}
