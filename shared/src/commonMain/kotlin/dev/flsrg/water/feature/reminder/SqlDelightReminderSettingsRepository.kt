package dev.flsrg.water.feature.reminder

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import dev.flsrg.water.database.WaterDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SqlDelightReminderSettingsRepository(
    database: WaterDatabase,
) : ReminderSettingsRepository {
    private val queries = database.drinkLogQueries

    override val settings: Flow<ReminderSettings> =
        queries
            .selectReminderSettings()
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { settings ->
                settings?.let {
                    ReminderSettings(
                        enabled = it.enabled != 0L,
                        intervalMinutes = it.interval_minutes,
                    )
                } ?: ReminderSettings()
            }

    override suspend fun save(settings: ReminderSettings) {
        queries.upsertReminderSettings(
            enabled = if (settings.enabled) 1L else 0L,
            interval_minutes = settings.intervalMinutes,
        )
    }
}
