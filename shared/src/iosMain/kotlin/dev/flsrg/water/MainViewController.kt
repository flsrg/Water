package dev.flsrg.water

import androidx.compose.ui.window.ComposeUIViewController
import dev.flsrg.water.database.DatabaseDriverFactory
import dev.flsrg.water.feature.reminder.IosWaterReminderScheduler

@Suppress("FunctionNaming", "ktlint:standard:function-naming")
fun MainViewController() =
    ComposeUIViewController {
        App(
            databaseDriverFactory = DatabaseDriverFactory(),
            reminderScheduler = IosWaterReminderScheduler(),
        )
    }
