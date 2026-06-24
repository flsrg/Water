package dev.flsrg.water.feature.reminder

import dev.flsrg.water.feature.water.DefaultDailyGoalMl
import dev.flsrg.water.feature.water.shouldSendHydrationReminder
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

private const val MaxReminderRequests = 64

fun planHydrationReminderInstants(
    settings: ReminderSettings,
    now: Instant,
    consumedTodayMl: Int,
    dailyGoalMl: Int = DefaultDailyGoalMl,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    maxRequests: Int = MaxReminderRequests,
): List<Instant> {
    if (!settings.enabled || maxRequests <= 0) {
        return emptyList()
    }

    val firstReminder =
        if (shouldSendHydrationReminder(consumedTodayMl, dailyGoalMl)) {
            now.plus(settings.intervalMinutes, DateTimeUnit.MINUTE, timeZone)
        } else {
            val tomorrow =
                now
                    .toLocalDateTime(timeZone)
                    .date
                    .plus(1, DateTimeUnit.DAY)

            tomorrow
                .atStartOfDayIn(timeZone)
                .plus(settings.intervalMinutes, DateTimeUnit.MINUTE, timeZone)
        }

    return buildList {
        var reminderTime = firstReminder

        repeat(maxRequests) {
            add(reminderTime)
            reminderTime = reminderTime.plus(settings.intervalMinutes, DateTimeUnit.MINUTE, timeZone)
        }
    }
}
