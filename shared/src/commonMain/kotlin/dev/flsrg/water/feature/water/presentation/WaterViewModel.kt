package dev.flsrg.water.feature.water.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WaterViewModel : ViewModel() {
    private val _state = MutableStateFlow(WaterUiState())
    val state: StateFlow<WaterUiState> = _state.asStateFlow()

    fun onIntent(intent: WaterIntent) {
        when (intent) {
            WaterIntent.AddWaterClicked -> addWater()
            WaterIntent.ResetClicked -> reset()
        }
    }

    private fun addWater() {
        _state.update { currentState ->
            currentState.copy(
                consumedMl = currentState.consumedMl + currentState.addAmountMl,
            )
        }
    }

    private fun reset() {
        _state.update {
            WaterUiState()
        }
    }
}
