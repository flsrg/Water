package dev.flsrg.water.feature.reminder

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class AndroidWaterReminderScheduler(
    private val context: Context,
) : WaterReminderScheduler {
    override suspend fun canSchedule(): Boolean = canPostNotifications(context)

    override suspend fun schedule(settings: ReminderSettings) {
        if (!settings.enabled || !canSchedule()) {
            cancel()
            return
        }

        val safeInterval = settings.intervalMinutes.coerceAtLeast(15)

        val request =
            PeriodicWorkRequestBuilder<DrinkReminderWorker>(
                safeInterval,
                TimeUnit.MINUTES,
            ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            ReminderNotificationSpec.ANDROID_UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request,
        )
    }

    override suspend fun cancel() {
        WorkManager.getInstance(context).cancelUniqueWork(ReminderNotificationSpec.ANDROID_UNIQUE_WORK_NAME)
    }
}
