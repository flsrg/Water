package dev.flsrg.water.feature.water.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.flsrg.water.feature.water.data.DrinkType
import dev.flsrg.water.feature.water.data.WaterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WaterViewModel(
    private val repository: WaterRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(WaterUiState())
    val state: StateFlow<WaterUiState> = _state.asStateFlow()

    init {
        observeDrinks()
    }

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

    private fun observeDrinks() {
        viewModelScope.launch {
            repository.drinks.collect { drinks ->
                val consumedMl = drinks.sumOf { it.hydrationMl }

                _state.update { currentState ->
                    currentState.copy(
                        summary =
                            currentState.summary.copy(
                                consumedMl = consumedMl,
                            ),
                        recentDrinks = drinks,
                    )
                }
            }
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
        val dialogState = _state.value.addDrinkDialog ?: return

        _state.update {
            it.copy(addDrinkDialog = null)
        }

        viewModelScope.launch {
            repository.addDrink(
                drinkType = dialogState.selectedDrinkType,
                volumeMl = dialogState.selectedAmountMl,
            )
        }
    }

    private fun reset() {
        viewModelScope.launch {
            repository.clear()
        }
    }
}
