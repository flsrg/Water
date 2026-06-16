package dev.flsrg.water.feature.water.presentation

data class WaterUiState(
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

sealed interface WaterIntent {
    data object AddWaterClicked : WaterIntent

    data object ResetClicked : WaterIntent
}
