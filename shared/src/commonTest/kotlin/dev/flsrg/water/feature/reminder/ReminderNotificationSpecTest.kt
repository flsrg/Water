package dev.flsrg.water.feature.reminder

import dev.flsrg.water.feature.water.DefaultDailyGoalMl
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant

class ReminderNotificationSpecTest {
    private val timeZone = TimeZone.of("UTC")
    private val now = Instant.parse("2026-06-24T10:00:00Z")

    @Test
    fun `iOS pending request identifiers are stable unique and ordered`() {
        val requestIds = ReminderNotificationSpec.iosPendingRequestIdentifiers

        assertEquals(ReminderNotificationSpec.IOS_MAX_PENDING_REQUESTS, requestIds.size)
        assertEquals(requestIds.size, requestIds.toSet().size)
        assertEquals("drink-water-reminder-0", requestIds.first())
        assertEquals("drink-water-reminder-63", requestIds.last())
    }

    @Test
    fun `platform notification identifiers are stable`() {
        assertEquals("drink-water-reminder", ReminderNotificationSpec.ANDROID_UNIQUE_WORK_NAME)
        assertEquals("dev.flsrg.water.extra.ACTION", ReminderNotificationSpec.ANDROID_ACTION_EXTRA)
        assertEquals("OPEN_ADD_DRINK", ReminderNotificationSpec.ANDROID_OPEN_ADD_DRINK_ACTION)
        assertEquals(1001, ReminderNotificationSpec.ANDROID_OPEN_ADD_DRINK_REQUEST_CODE)
        assertEquals(2001, ReminderNotificationSpec.ANDROID_DRINK_REMINDER_NOTIFICATION_ID)
        assertEquals("DRINK_REMINDER", ReminderNotificationSpec.IOS_CATEGORY_IDENTIFIER)
        assertEquals("OPEN_ADD_DRINK", ReminderNotificationSpec.IOS_OPEN_ADD_DRINK_ACTION)
    }

    @Test
    fun `iOS request planning returns no requests when reminders are disabled`() {
        val requests =
            buildIosReminderRequests(
                settings = ReminderSettings(enabled = false, intervalMinutes = 30),
                now = now,
                consumedTodayMl = 0,
                canSchedule = true,
                timeZone = timeZone,
            )

        assertTrue(requests.isEmpty())
    }

    @Test
    fun `iOS request planning returns no requests when notifications are unavailable`() {
        val requests =
            buildIosReminderRequests(
                settings = ReminderSettings(enabled = true, intervalMinutes = 30),
                now = now,
                consumedTodayMl = 0,
                canSchedule = false,
                timeZone = timeZone,
            )

        assertTrue(requests.isEmpty())
    }

    @Test
    fun `iOS request planning schedules from now plus interval when goal is not reached`() {
        val requests =
            buildIosReminderRequests(
                settings = ReminderSettings(enabled = true, intervalMinutes = 30),
                now = now,
                consumedTodayMl = DefaultDailyGoalMl - 1,
                canSchedule = true,
                timeZone = timeZone,
                maxRequests = 2,
            )

        assertEquals(
            listOf(
                IosReminderRequestSpec(
                    identifier = "drink-water-reminder-0",
                    categoryIdentifier = "DRINK_REMINDER",
                    title = "Time to drink water",
                    body = "Add your next drink",
                    delaySeconds = 1_800.0,
                ),
                IosReminderRequestSpec(
                    identifier = "drink-water-reminder-1",
                    categoryIdentifier = "DRINK_REMINDER",
                    title = "Time to drink water",
                    body = "Add your next drink",
                    delaySeconds = 3_600.0,
                ),
            ),
            requests,
        )
    }

    @Test
    fun `iOS request planning resumes on next day after goal is reached`() {
        val requests =
            buildIosReminderRequests(
                settings = ReminderSettings(enabled = true, intervalMinutes = 60),
                now = now,
                consumedTodayMl = DefaultDailyGoalMl,
                canSchedule = true,
                timeZone = timeZone,
                maxRequests = 1,
            )

        assertEquals("drink-water-reminder-0", requests.single().identifier)
        assertEquals("DRINK_REMINDER", requests.single().categoryIdentifier)
        assertEquals(15 * 60 * 60.0, requests.single().delaySeconds)
    }
}
