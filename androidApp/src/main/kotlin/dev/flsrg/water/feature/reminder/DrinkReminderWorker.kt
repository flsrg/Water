package dev.flsrg.water.feature.reminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.flsrg.water.database.DatabaseDriverFactory
import dev.flsrg.water.database.WaterDatabase

class DrinkReminderWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val driver =
            DatabaseDriverFactory(
                context = applicationContext,
            ).createDriver()

        try {
            val database = WaterDatabase(driver = driver)
            val reminderPolicy = HydrationReminderPolicy(database = database)

            if (reminderPolicy.shouldShowReminder()) {
                showDrinkReminder(applicationContext)
            }
        } finally {
            driver.close()
        }

        return Result.success()
    }
}
