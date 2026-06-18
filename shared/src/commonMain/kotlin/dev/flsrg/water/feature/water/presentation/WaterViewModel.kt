package dev.flsrg.water.feature.water.presentation

import androidx.lifecycle.ViewModel
import dev.flsrg.water.feature.water.data.DrinkLogItem
import dev.flsrg.water.feature.water.data.DrinkType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WaterViewModel : ViewModel() {
    private val _state = MutableStateFlow(WaterUiState())
    val state: StateFlow<WaterUiState> = _state.asStateFlow()

    private var nextDrinkId = 0L

    fun onIntent(intent: WaterIntent) {
        when (intent) {
            WaterIntent.AddDrinkClicked -> openAddDrinkDialog()
            WaterIntent.AddDrinkDialogDismissed -> dismissAddDrinkDialog()
            is WaterIntent.DialogDrinkTypeSelected -> selectDialogDrinkType(intent.drinkType)
            is WaterIntent.DialogVolumeSelected -> selectDialogVolume(intent.volumeMl)
            WaterIntent.AddDrinkConfirmed -> confirmAddDrink()
            WaterIntent.ResetClicked -> reset()
        }
    }

    private fun openAddDrinkDialog() {
        _state.update {
            it.copy(
                addDrinkDialog = AddDrinkDialogState(),
            )
        }
    }

    private fun dismissAddDrinkDialog() {
        _state.update {
            it.copy(
                addDrinkDialog = null,
            )
        }
    }

    private fun selectDialogDrinkType(drinkType: DrinkType) {
        _state.update {
            val dialogState = it.addDrinkDialog ?: return@update it
            it.copy(
                addDrinkDialog =
                    dialogState.copy(
                        selectedDrinkType = drinkType,
                    ),
            )
        }
    }

    private fun selectDialogVolume(volumeMl: Int) {
        _state.update { currentState ->
            val dialogState = currentState.addDrinkDialog ?: return@update currentState

            currentState.copy(
                addDrinkDialog =
                    dialogState.copy(
                        selectedAmountMl = volumeMl,
                    ),
            )
        }
    }

    private fun confirmAddDrink() {
        _state.update {
            val dialogState = it.addDrinkDialog ?: return@update it

            val hydrationMl =
                (dialogState.selectedAmountMl * dialogState.selectedDrinkType.hydrationMultiplier).toInt()

            val drinkLogItem =
                DrinkLogItem(
                    id = nextDrinkId++,
                    drinkType = dialogState.selectedDrinkType,
                    volumeMl = dialogState.selectedAmountMl,
                    hydrationMl = hydrationMl,
                )

            it.copy(
                summary =
                    it.summary.copy(
                        consumedMl = it.summary.consumedMl + hydrationMl,
                    ),
                recentDrinks = listOf(drinkLogItem) + it.recentDrinks,
                addDrinkDialog = null,
            )
        }
    }

    private fun reset() {
        nextDrinkId = 0L
        _state.update {
            WaterUiState()
        }
    }
}
