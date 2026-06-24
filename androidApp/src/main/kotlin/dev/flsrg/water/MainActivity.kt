package dev.flsrg.water

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import dev.flsrg.water.database.DatabaseDriverFactory
import dev.flsrg.water.feature.reminder.AndroidWaterReminderScheduler
import dev.flsrg.water.feature.reminder.WaterNotificationChannels
import dev.flsrg.water.feature.reminder.WaterNotificationIntents
import dev.flsrg.water.feature.reminder.dismissDrinkReminder

class MainActivity : ComponentActivity() {
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            // Scheduling is guarded by AndroidWaterReminderScheduler.canSchedule().
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        WaterNotificationChannels.create(this)
        requestNotificationPermissionIfNeeded()
        handleNotificationIntent(intent)

        setContent {
            App(
                databaseDriverFactory =
                    DatabaseDriverFactory(
                        context = this@MainActivity,
                    ),
                reminderScheduler =
                    AndroidWaterReminderScheduler(
                        context = applicationContext,
                    ),
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        val action = intent?.getStringExtra(WaterNotificationIntents.EXTRA_ACTION)

        if (action == WaterNotificationIntents.ACTION_OPEN_ADD_DRINK) {
            dismissDrinkReminder(this)
            AppActionStore.openAddDrinkDialogFromPlatform()
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        val permission = Manifest.permission.POST_NOTIFICATIONS
        val isGranted =
            ContextCompat.checkSelfPermission(
                this,
                permission,
            ) == PackageManager.PERMISSION_GRANTED

        if (!isGranted) {
            notificationPermissionLauncher.launch(permission)
        }
    }
}
