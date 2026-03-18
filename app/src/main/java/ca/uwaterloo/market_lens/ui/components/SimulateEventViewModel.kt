package ca.uwaterloo.market_lens.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.market_lens.di.AppGraph
import ca.uwaterloo.market_lens.domain.service.MarketLensModel
import ca.uwaterloo.market_lens.ui.events.SimulationManager
import kotlinx.coroutines.launch

class SimulateEventViewModel(
    private val model: MarketLensModel = AppGraph.model
) : ViewModel() {

    fun simulateRandomEvent(onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                val portfolio = model.getPortfolio()
                val portfolioTickers = portfolio.positions.map { it.tickerKey }.toSet()

                // Get all possible events from domain model
                val allPossibleEvents = model.getEvents()

                // Filter for tickers in portfolio that aren't already simulated
                val currentSimulated = SimulationManager.simulatedEventIds.value
                val availableEvents = allPossibleEvents.filter {
                    it.tickerKey in portfolioTickers && it.id !in currentSimulated
                }

                if (availableEvents.isNotEmpty()) {
                    val nextEvent = availableEvents.random()
                    SimulationManager.triggerEvent(nextEvent.id)
                }
            } catch (_: Exception) {
            }

            onComplete()
        }
    }
}
