package ca.uwaterloo.market_lens.ui.components

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.market_lens.di.AppGraph
import ca.uwaterloo.market_lens.domain.service.MarketLensModel
import ca.uwaterloo.market_lens.ui.events.SimulationManager
import kotlinx.coroutines.launch

class SimulateEventViewModel(
    private val model: MarketLensModel = AppGraph.model
) : ViewModel() {
    private val TAG = "SimulateEventVM"

    fun simulateRandomEvent() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting random event simulation...")
                val portfolio = model.getPortfolio()
                val portfolioTickers = portfolio.positions.map { it.tickerKey.uppercase() }.toSet()
                Log.d(TAG, "Portfolio tickers: $portfolioTickers")

                // Get all possible events from domain model
                val allPossibleEvents = model.getEvents()
                Log.d(TAG, "Fetched ${allPossibleEvents.size} total events from Supabase")

                // Filter for tickers in portfolio that aren't already simulated
                val currentSimulated = SimulationManager.simulatedEventIds.value
                val availableEvents = allPossibleEvents.filter {
                    val tickerMatch = it.tickerKey.uppercase() in portfolioTickers
                    val notSimulated = it.id !in currentSimulated
                    tickerMatch && notSimulated
                }

                Log.d(TAG, "Available events for simulation: ${availableEvents.size}")

                if (availableEvents.isNotEmpty()) {
                    val nextEvent = availableEvents.random()
                    Log.d(TAG, "Triggering simulation for event: ${nextEvent.id} (${nextEvent.tickerKey})")
                    SimulationManager.triggerEvent(nextEvent)
                } else {
                    Log.w(TAG, "No available events to simulate for portfolio $portfolioTickers")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during event simulation", e)
            }
        }
    }
}
