package dev.flsrg.water.feature.water.data

import kotlinx.coroutines.flow.Flow

interface WaterRepository {
    val drinks: Flow<List<DrinkLogItem>>

    suspend fun addDrink(
        drinkType: DrinkType,
        volumeMl: Int,
    )

    suspend fun clear()
}
