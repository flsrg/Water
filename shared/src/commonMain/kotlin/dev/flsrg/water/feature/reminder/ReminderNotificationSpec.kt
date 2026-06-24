package dev.flsrg.water.feature.reminder

import kotlinx.datetime.TimeZone
import kotlin.time.Instant

object ReminderNotificationSpec {
    const val TITLE = "Time to drink water"
    const val BODY = "Add your next drink"

    const val ANDROID_UNIQUE_WORK_NAME = "drink-water-reminder"
    const val ANDROID_ACTION_EXTRA = "dev.flsrg.water.extra.ACTION"
    const val ANDROID_OPEN_ADD_DRINK_ACTION = "OPEN_ADD_DRINK"
    const val ANDROID_OPEN_ADD_DRINK_REQUEST_CODE = 1001
    const val ANDROID_DRINK_REMINDER_NOTIFICATION_ID = 2001

    const val IOS_REQUEST_PREFIX = "drink-water-reminder"
    const val IOS_MAX_PENDING_REQUESTS = 64
    const val IOS_CATEGORY_IDENTIFIER = "DRINK_REMINDER"
    const val IOS_OPEN_ADD_DRINK_ACTION = "OPEN_ADD_DRINK"

    val iosPendingRequestIdentifiers: List<String> =
        List(IOS_MAX_PENDING_REQUESTS) { index ->
            iosPendingRequestIdentifier(index)
        }

    fun iosPendingRequestIdentifier(index: Int): String = "$IOS_REQUEST_PREFIX-$index"
}

data class IosReminderRequestSpec(
    val identifier: String,
    val categoryIdentifier: String,
    val title: String,
    val body: String,
    val delaySeconds: Double,
)

fun buildIosReminderRequests(
    settings: ReminderSettings,
    now: Instant,
    consumedTodayMl: Int,
    canSchedule: Boolean,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    maxRequests: Int = ReminderNotificationSpec.IOS_MAX_PENDING_REQUESTS,
): List<IosReminderRequestSpec> {
    if (!canSchedule || maxRequests <= 0) {
        return emptyList()
    }

    return planHydrationReminderInstants(
        settings = settings,
        now = now,
        consumedTodayMl = consumedTodayMl,
        timeZone = timeZone,
        maxRequests = maxRequests,
    ).mapIndexed { index, reminderTime ->
        IosReminderRequestSpec(
            identifier = ReminderNotificationSpec.iosPendingRequestIdentifier(index),
            categoryIdentifier = ReminderNotificationSpec.IOS_CATEGORY_IDENTIFIER,
            title = ReminderNotificationSpec.TITLE,
            body = ReminderNotificationSpec.BODY,
            delaySeconds =
                ((reminderTime.toEpochMilliseconds() - now.toEpochMilliseconds()).coerceAtLeast(1_000L))
                    .toDouble() / 1_000.0,
        )
    }
}
