package dev.flsrg.water.feature.water

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HydrationGoalTest {
    @Test
    fun `reminder is sent while daily goal is not reached`() {
        assertTrue(
            shouldSendHydrationReminder(
                consumedMl = DefaultDailyGoalMl - 1,
            ),
        )
    }

    @Test
    fun `reminder is not sent when daily goal is reached`() {
        assertFalse(
            shouldSendHydrationReminder(
                consumedMl = DefaultDailyGoalMl,
            ),
        )
    }

    @Test
    fun `reminder is not sent when daily goal is exceeded`() {
        assertFalse(
            shouldSendHydrationReminder(
                consumedMl = DefaultDailyGoalMl + 1,
            ),
        )
    }
}
