package dev.flsrg.water.feature.reminder

import dev.flsrg.water.database.WaterDatabase
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class HydrationReminderPolicy(
    private val database: WaterDatabase,
) {
    fun shouldShowReminder(): Boolean {
        val period = currentDayPeriodMillis()
        val consumedMl =
            database.drinkLogQueries
                .selectHydrationTotalFromPeriod(
                    startEpochMillis = period.startInclusive,
                    endEpochMillis = period.endExclusive,
                ).executeAsOne()
                .toInt()

        return dev.flsrg.water.feature.water.shouldSendHydrationReminder(
            consumedMl = consumedMl,
        )
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
