package ca.uwaterloo.market_lens.ui.events

import androidx.lifecycle.ViewModel

class EventsViewModel : ViewModel() {
    val events = listOf(
        TimelineEvent(
            id = "1",
            ticker = "AAPL",
            percentChange = -2.8,
            description = "Likely driven by earning miss and market reaction",
            timestamp = "Jan 27, 2026 - 2:31 PM"
        ),
        TimelineEvent(
            id = "2",
            ticker = "SHOP",
            percentChange = -6.8,
            description = "Macroeconomic data impact and Fed policy signals",
            timestamp = "Jan 27, 2026 - 2:31 PM"
        )
    )
}
