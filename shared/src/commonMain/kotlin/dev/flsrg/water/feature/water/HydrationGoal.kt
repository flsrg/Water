package dev.flsrg.water.feature.water

const val DefaultDailyGoalMl = 2000

fun shouldSendHydrationReminder(
    consumedMl: Int,
    dailyGoalMl: Int = DefaultDailyGoalMl,
): Boolean = consumedMl < dailyGoalMl
