package dev.flsrg.water.feature.water.presentation

import dev.flsrg.water.feature.water.data.DrinkLogItem
import dev.flsrg.water.feature.water.data.DrinkType

private object DrinkVolumeOption {
    const val DEFAULT_ML = 300
    const val LARGE_ML = 500
}

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
    val selectedAmountMl: Int = DrinkVolumeOption.DEFAULT_ML,
    val drinkTypes: List<DrinkType> = DrinkType.entries,
    val volumeOptionsMl: List<Int> = listOf(DrinkVolumeOption.DEFAULT_ML, DrinkVolumeOption.LARGE_ML),
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
