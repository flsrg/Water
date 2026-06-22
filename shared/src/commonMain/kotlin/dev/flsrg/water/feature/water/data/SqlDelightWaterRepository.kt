package dev.flsrg.water.feature.water.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.flsrg.water.database.WaterDatabase
import kotlinx.coroutines.Dispatchers
import kotlin.time.Clock

class SqlDelightWaterRepository(
    database: WaterDatabase,
) : WaterRepository {
    private val queries = database.drinkLogQueries

    override val drinks =
        queries
            .selectAll { id, drinkType, volumeMl, hydrationMl, createdAtEpochMillis ->
                DrinkLogItem(
                    id = id,
                    drinkType = DrinkType.valueOf(drinkType),
                    volumeMl = volumeMl.toInt(),
                    hydrationMl = hydrationMl.toInt(),
                    createdAtEpochMillis = createdAtEpochMillis,
                )
            }.asFlow()
            .mapToList(Dispatchers.Default)

    override suspend fun addDrink(
        drinkType: DrinkType,
        volumeMl: Int,
    ) {
        val hydrationMl = (volumeMl * drinkType.hydrationMultiplier).toInt()

        queries.insert(
            drink_type = drinkType.name,
            volume_ml = volumeMl.toLong(),
            hydration_ml = hydrationMl.toLong(),
            created_at_epoch_millis = Clock.System.now().toEpochMilliseconds(),
        )
    }

    override suspend fun clear() {
        queries.clear()
    }
}
