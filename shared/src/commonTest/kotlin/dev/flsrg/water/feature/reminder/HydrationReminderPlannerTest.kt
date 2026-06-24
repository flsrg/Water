package dev.flsrg.water.feature.reminder

import dev.flsrg.water.feature.water.DefaultDailyGoalMl
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HydrationReminderPlannerTest {
    private val timeZone = TimeZone.of("UTC")
    private val now = Instant.parse("2026-06-24T10:00:00Z")

    @Test
    fun `planner schedules reminders while daily goal is not reached`() {
        val reminders =
            planHydrationReminderInstants(
                settings = ReminderSettings(enabled = true, intervalMinutes = 30),
                now = now,
                consumedTodayMl = DefaultDailyGoalMl - 1,
                timeZone = timeZone,
                maxRequests = 2,
            )

        assertEquals(
            listOf(
                Instant.parse("2026-06-24T10:30:00Z"),
                Instant.parse("2026-06-24T11:00:00Z"),
            ),
            reminders,
        )
    }

    @Test
    fun `planner schedules no same day reminders when daily goal is reached`() {
        val reminders =
            planHydrationReminderInstants(
                settings = ReminderSettings(enabled = true, intervalMinutes = 60),
                now = now,
                consumedTodayMl = DefaultDailyGoalMl,
                timeZone = timeZone,
                maxRequests = 1,
            )

        val firstReminderDate =
            reminders
                .single()
                .toLocalDateTime(timeZone)
                .date

        assertEquals("2026-06-25", firstReminderDate.toString())
    }

    @Test
    fun `planner resumes scheduling on next day after goal is reached`() {
        val reminders =
            planHydrationReminderInstants(
                settings = ReminderSettings(enabled = true, intervalMinutes = 120),
                now = now,
                consumedTodayMl = DefaultDailyGoalMl + 1,
                timeZone = timeZone,
                maxRequests = 2,
            )

        assertEquals(
            listOf(
                Instant.parse("2026-06-25T02:00:00Z"),
                Instant.parse("2026-06-25T04:00:00Z"),
            ),
            reminders,
        )
    }

    @Test
    fun `planner respects disabled settings`() {
        val reminders =
            planHydrationReminderInstants(
                settings = ReminderSettings(enabled = false, intervalMinutes = 30),
                now = now,
                consumedTodayMl = 0,
                timeZone = timeZone,
            )

        assertTrue(reminders.isEmpty())
    }

    @Test
    fun `planner respects interval changes`() {
        val reminders =
            planHydrationReminderInstants(
                settings = ReminderSettings(enabled = true, intervalMinutes = 15),
                now = now,
                consumedTodayMl = 0,
                timeZone = timeZone,
                maxRequests = 2,
            )

        assertEquals(
            listOf(
                Instant.parse("2026-06-24T10:15:00Z"),
                Instant.parse("2026-06-24T10:30:00Z"),
            ),
            reminders,
        )
    }
}
