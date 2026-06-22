package dev.flsrg.water.feature.water.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.flsrg.water.database.WaterDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class SqlDelightWaterRepository(
    database: WaterDatabase,
) : WaterRepository {
    private val queries = database.drinkLogQueries

    override val drinks =
        currentDayPeriodMillis().let { period ->
            queries
                .selectFromPeriod(
                    startEpochMillis = period.startInclusive,
                    endEpochMillis = period.endExclusive,
                ) { id, drinkType, volumeMl, hydrationMl, createdAtEpochMillis ->
                    DrinkLogItem(
                        id = id,
                        drinkType = DrinkType.valueOf(drinkType),
                        volumeMl = volumeMl.toInt(),
                        hydrationMl = hydrationMl.toInt(),
                        createdAtEpochMillis = createdAtEpochMillis,
                    )
                }.asFlow()
                .mapToList(Dispatchers.Default)
        }

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

    override suspend fun deleteDrink(id: Long) {
        queries.deleteById(id = id)
    }
}

private fun currentDayPeriodMillis(timeZone: TimeZone = TimeZone.currentSystemDefault()): EpochMillisPeriod {
    val today =
        Clock.System
            .now()
            .toLocalDateTime(timeZone)
            .date

    val startOfToday =
        today
            .atStartOfDayIn(timeZone)
            .toEpochMilliseconds()

    val startOfTomorrow =
        today
            .plus(1, DateTimeUnit.DAY)
            .atStartOfDayIn(timeZone)
            .toEpochMilliseconds()

    return EpochMillisPeriod(
        startInclusive = startOfToday,
        endExclusive = startOfTomorrow,
    )
}

private data class EpochMillisPeriod(
    val startInclusive: Long,
    val endExclusive: Long,
)
