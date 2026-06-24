package dev.flsrg.water.feature.reminder

data class ReminderSettings(
    val enabled: Boolean = false,
    val intervalMinutes: Long = 60,
)

interface WaterReminderScheduler {
    suspend fun canSchedule(): Boolean

    suspend fun schedule(settings: ReminderSettings)

    suspend fun refreshAfterHydrationChanged(settings: ReminderSettings) = Unit

    suspend fun cancel()
}

object NoOpWaterReminderScheduler : WaterReminderScheduler {
    override suspend fun canSchedule(): Boolean = false

    override suspend fun schedule(settings: ReminderSettings) = Unit

    override suspend fun cancel() = Unit
}

interface ReminderSettingsRepository {
    val settings: kotlinx.coroutines.flow.Flow<ReminderSettings>

    suspend fun save(settings: ReminderSettings)
}
