package dev.flsrg.water.feature.reminder

import dev.flsrg.water.database.DatabaseDriverFactory
import dev.flsrg.water.database.WaterDatabase
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import platform.Foundation.NSError
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Clock

private const val ReminderRequestPrefix = "drink-water-reminder"
private const val MaxPendingReminderRequests = 64
private const val ReminderCategory = "DRINK_REMINDER"

class IosWaterReminderScheduler(
    private val databaseDriverFactory: DatabaseDriverFactory = DatabaseDriverFactory(),
    private val notificationCenter: UNUserNotificationCenter = UNUserNotificationCenter.currentNotificationCenter(),
) : WaterReminderScheduler {
    private val reminderRequestIds =
        List(MaxPendingReminderRequests) { index ->
            "$ReminderRequestPrefix-$index"
        }

    override suspend fun canSchedule(): Boolean {
        val settings =
            suspendCoroutine { continuation ->
                notificationCenter.getNotificationSettingsWithCompletionHandler { notificationSettings ->
                    continuation.resume(notificationSettings)
                }
            }

        return settings?.authorizationStatus == UNAuthorizationStatusAuthorized
    }

    override suspend fun schedule(settings: ReminderSettings) {
        rebuildPendingReminders(settings)
    }

    override suspend fun refreshAfterHydrationChanged(settings: ReminderSettings) {
        rebuildPendingReminders(settings)
    }

    override suspend fun cancel() {
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(reminderRequestIds)
    }

    private suspend fun rebuildPendingReminders(settings: ReminderSettings) {
        cancel()

        if (!settings.enabled || !canSchedule()) {
            return
        }

        val now = Clock.System.now()
        val consumedTodayMl = consumedTodayMl()
        val plannedReminders =
            planHydrationReminderInstants(
                settings = settings,
                now = now,
                consumedTodayMl = consumedTodayMl,
                maxRequests = MaxPendingReminderRequests,
            )

        plannedReminders.forEachIndexed { index, reminderTime ->
            val secondsFromNow =
                ((reminderTime.toEpochMilliseconds() - now.toEpochMilliseconds()).coerceAtLeast(1_000L))
                    .toDouble() / 1_000.0

            val trigger =
                UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
                    timeInterval = secondsFromNow,
                    repeats = false,
                )
            val request =
                UNNotificationRequest.requestWithIdentifier(
                    identifier = reminderRequestIds[index],
                    content = reminderContent(),
                    trigger = trigger,
                )

            notificationCenter.addNotificationRequest(request) { error ->
                logSchedulingError(error)
            }
        }
    }

    private fun consumedTodayMl(): Int {
        val driver = databaseDriverFactory.createDriver()

        try {
            val database = WaterDatabase(driver = driver)
            val period = currentDayPeriodMillis()

            return database.drinkLogQueries
                .selectHydrationTotalFromPeriod(
                    startEpochMillis = period.startInclusive,
                    endEpochMillis = period.endExclusive,
                ).executeAsOne()
                .toInt()
        } finally {
            driver.close()
        }
    }

    private fun reminderContent(): UNMutableNotificationContent =
        UNMutableNotificationContent().apply {
            setTitle("Time to drink water")
            setBody("Add your next drink")
            setSound(UNNotificationSound.defaultSound())
            setCategoryIdentifier(ReminderCategory)
        }

    private fun logSchedulingError(error: NSError?) {
        if (error != null) {
            println("Failed to schedule reminder: ${error.localizedDescription}")
        }
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
