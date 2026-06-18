package dev.flsrg.water.feature.water.data

data class DrinkLogItem(
    val id: Long,
    val drinkType: DrinkType,
    val volumeMl: Int,
    val hydrationMl: Int,
)
