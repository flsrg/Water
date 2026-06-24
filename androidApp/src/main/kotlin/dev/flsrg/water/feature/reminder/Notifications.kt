package dev.flsrg.water.feature.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dev.flsrg.water.MainActivity
import dev.flsrg.water.R

object WaterNotificationChannels {
    const val REMINDERS = "water_reminders"

    fun create(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    REMINDERS,
                    "Water reminders",
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply {
                    description = "Reminders to drink water"
                }

            context
                .getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }
}

object WaterNotificationIntents {
    const val EXTRA_ACTION = ReminderNotificationSpec.ANDROID_ACTION_EXTRA
    const val ACTION_OPEN_ADD_DRINK = ReminderNotificationSpec.ANDROID_OPEN_ADD_DRINK_ACTION
}

object WaterNotificationIds {
    const val DRINK_REMINDER = ReminderNotificationSpec.ANDROID_DRINK_REMINDER_NOTIFICATION_ID
}

fun openAddDrinkPendingIntent(context: Context): PendingIntent {
    val intent =
        Intent(context, MainActivity::class.java).apply {
            putExtra(WaterNotificationIntents.EXTRA_ACTION, WaterNotificationIntents.ACTION_OPEN_ADD_DRINK)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

    return PendingIntent.getActivity(
        context,
        ReminderNotificationSpec.ANDROID_OPEN_ADD_DRINK_REQUEST_CODE,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}

fun showDrinkReminder(context: Context) {
    if (!canPostNotifications(context)) {
        return
    }

    val pendingIntent = openAddDrinkPendingIntent(context)

    val notification =
        NotificationCompat
            .Builder(context, WaterNotificationChannels.REMINDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(ReminderNotificationSpec.TITLE)
            .setContentText(ReminderNotificationSpec.BODY)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_add,
                "Add drink",
                pendingIntent,
            ).setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

    NotificationManagerCompat.from(context).notify(WaterNotificationIds.DRINK_REMINDER, notification)
}

fun dismissDrinkReminder(context: Context) {
    NotificationManagerCompat.from(context).cancel(WaterNotificationIds.DRINK_REMINDER)
}

fun canPostNotifications(context: Context): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
