package dev.flsrg.water.feature.water.presentation

import dev.flsrg.water.feature.water.data.DrinkLogItem
import dev.flsrg.water.feature.water.data.DrinkType

data class WaterUiState(
    val summary: WaterSummaryState = WaterSummaryState(),
    val addDrinkDialog: AddDrinkDialogState? = null,
    val recentDrinks: List<DrinkLogItem> = emptyList(),
)

data class WaterSummaryState(
    val consumedMl: Int = 0,
    val addAmountMl: Int = 250,
    val dailyGoalMl: Int = 2000,
) {
    val remainingMl: Int
        get() = (dailyGoalMl - consumedMl).coerceAtLeast(0)

    val progress: Float
        get() =
            if (dailyGoalMl == 0) {
                0f
            } else {
                (consumedMl.toFloat() / dailyGoalMl.toFloat()).coerceIn(0f, 1f)
            }
}

data class AddDrinkDialogState(
    val selectedDrinkType: DrinkType = DrinkType.Water,
    val selectedAmountMl: Int = 250,
    val drinkTypes: List<DrinkType> = DrinkType.entries,
    val volumeOptionsMl: List<Int> = listOf(300, 500),
)

sealed interface WaterIntent {
    data object AddDrinkClicked : WaterIntent

    data object AddDrinkDialogDismissed : WaterIntent

    data class DialogDrinkTypeSelected(
        val drinkType: DrinkType,
    ) : WaterIntent

    data class DialogVolumeSelected(
        val volumeMl: Int,
    ) : WaterIntent

    data object AddDrinkConfirmed : WaterIntent

    data object ResetClicked : WaterIntent
}
