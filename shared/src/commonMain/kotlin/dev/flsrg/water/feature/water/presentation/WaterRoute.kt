package dev.flsrg.water.feature.water.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.flsrg.water.AppAction
import dev.flsrg.water.AppActionStore
import dev.flsrg.water.feature.reminder.ReminderSettingsRepository
import dev.flsrg.water.feature.reminder.WaterReminderScheduler
import dev.flsrg.water.feature.water.data.WaterRepository

@Composable
fun WaterRoute(
    repository: WaterRepository,
    reminderSettingsRepository: ReminderSettingsRepository,
    reminderScheduler: WaterReminderScheduler,
    viewModel: WaterViewModel =
        viewModel {
            WaterViewModel(
                repository = repository,
                reminderSettingsRepository = reminderSettingsRepository,
                reminderScheduler = reminderScheduler,
            )
        },
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        AppActionStore.pendingAction.collect { action ->
            when (action) {
                AppAction.OpenAddDrinkDialog -> {
                    viewModel.onIntent(WaterIntent.AddDrinkClicked)
                    AppActionStore.consume(action)
                }

                null -> Unit
            }
        }
    }

    WaterScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}
