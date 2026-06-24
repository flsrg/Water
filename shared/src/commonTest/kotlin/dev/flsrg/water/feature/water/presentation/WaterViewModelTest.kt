package dev.flsrg.water.feature.water.presentation

import dev.flsrg.water.feature.reminder.ReminderSettings
import dev.flsrg.water.feature.reminder.ReminderSettingsRepository
import dev.flsrg.water.feature.reminder.WaterReminderScheduler
import dev.flsrg.water.feature.water.data.DrinkLogItem
import dev.flsrg.water.feature.water.data.DrinkType
import dev.flsrg.water.feature.water.data.WaterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class WaterViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial settings are loaded into state`() =
        runTest {
            val settings = ReminderSettings(enabled = true, intervalMinutes = 30)
            val viewModel =
                createViewModel(
                    reminderSettingsRepository = FakeReminderSettingsRepository(settings),
                )

            assertEquals(settings, viewModel.state.value.reminderSettings)
        }

    @Test
    fun `enabling reminders persists settings and schedules`() =
        runTest {
            val reminderSettingsRepository = FakeReminderSettingsRepository()
            val scheduler = FakeWaterReminderScheduler()
            val viewModel =
                createViewModel(
                    reminderSettingsRepository = reminderSettingsRepository,
                    reminderScheduler = scheduler,
                )

            viewModel.onIntent(WaterIntent.RemindersEnabledChanged(enabled = true))

            assertTrue(reminderSettingsRepository.savedSettings.single().enabled)
            assertEquals(reminderSettingsRepository.savedSettings.single(), scheduler.scheduledSettings.single())
            assertEquals(0, scheduler.cancelCount)
        }

    @Test
    fun `disabling reminders persists settings and cancels`() =
        runTest {
            val reminderSettingsRepository =
                FakeReminderSettingsRepository(
                    ReminderSettings(enabled = true, intervalMinutes = 30),
                )
            val scheduler = FakeWaterReminderScheduler()
            val viewModel =
                createViewModel(
                    reminderSettingsRepository = reminderSettingsRepository,
                    reminderScheduler = scheduler,
                )

            viewModel.onIntent(WaterIntent.RemindersEnabledChanged(enabled = false))

            assertFalse(reminderSettingsRepository.savedSettings.single().enabled)
            assertEquals(1, scheduler.cancelCount)
            assertEquals(emptyList(), scheduler.scheduledSettings)
        }

    @Test
    fun `changing interval while enabled persists and reschedules`() =
        runTest {
            val reminderSettingsRepository =
                FakeReminderSettingsRepository(
                    ReminderSettings(enabled = true, intervalMinutes = 30),
                )
            val scheduler = FakeWaterReminderScheduler()
            val viewModel =
                createViewModel(
                    reminderSettingsRepository = reminderSettingsRepository,
                    reminderScheduler = scheduler,
                )

            viewModel.onIntent(WaterIntent.ReminderIntervalChanged(minutes = 120))

            assertEquals(
                ReminderSettings(enabled = true, intervalMinutes = 120),
                reminderSettingsRepository.savedSettings.single(),
            )
            assertEquals(reminderSettingsRepository.savedSettings.single(), scheduler.scheduledSettings.single())
        }

    @Test
    fun `changing interval while disabled only persists`() =
        runTest {
            val reminderSettingsRepository = FakeReminderSettingsRepository()
            val scheduler = FakeWaterReminderScheduler()
            val viewModel =
                createViewModel(
                    reminderSettingsRepository = reminderSettingsRepository,
                    reminderScheduler = scheduler,
                )

            viewModel.onIntent(WaterIntent.ReminderIntervalChanged(minutes = 15))

            assertEquals(
                ReminderSettings(enabled = false, intervalMinutes = 15),
                reminderSettingsRepository.savedSettings.single(),
            )
            assertEquals(emptyList(), scheduler.scheduledSettings)
            assertEquals(0, scheduler.cancelCount)
        }

    @Test
    fun `enabling reminders saves disabled settings when scheduling is unavailable`() =
        runTest {
            val reminderSettingsRepository = FakeReminderSettingsRepository()
            val scheduler = FakeWaterReminderScheduler(canSchedule = false)
            val viewModel =
                createViewModel(
                    reminderSettingsRepository = reminderSettingsRepository,
                    reminderScheduler = scheduler,
                )

            viewModel.onIntent(WaterIntent.RemindersEnabledChanged(enabled = true))

            assertFalse(reminderSettingsRepository.savedSettings.single().enabled)
            assertEquals(1, scheduler.cancelCount)
            assertEquals(emptyList(), scheduler.scheduledSettings)
        }

    private fun createViewModel(
        waterRepository: WaterRepository = FakeWaterRepository(),
        reminderSettingsRepository: ReminderSettingsRepository = FakeReminderSettingsRepository(),
        reminderScheduler: WaterReminderScheduler = FakeWaterReminderScheduler(),
    ): WaterViewModel =
        WaterViewModel(
            repository = waterRepository,
            reminderSettingsRepository = reminderSettingsRepository,
            reminderScheduler = reminderScheduler,
        )
}

private class FakeWaterRepository : WaterRepository {
    override val drinks = MutableStateFlow(emptyList<DrinkLogItem>())

    override suspend fun addDrink(
        drinkType: DrinkType,
        volumeMl: Int,
    ) = Unit

    override suspend fun deleteDrink(id: Long) = Unit
}

private class FakeReminderSettingsRepository(
    initialSettings: ReminderSettings = ReminderSettings(),
) : ReminderSettingsRepository {
    private val settingsFlow = MutableStateFlow(initialSettings)
    val savedSettings = mutableListOf<ReminderSettings>()

    override val settings: Flow<ReminderSettings> = settingsFlow

    override suspend fun save(settings: ReminderSettings) {
        savedSettings += settings
        settingsFlow.value = settings
    }
}

private class FakeWaterReminderScheduler(
    private val canSchedule: Boolean = true,
) : WaterReminderScheduler {
    val scheduledSettings = mutableListOf<ReminderSettings>()
    var cancelCount = 0

    override suspend fun canSchedule(): Boolean = canSchedule

    override suspend fun schedule(settings: ReminderSettings) {
        scheduledSettings += settings
    }

    override suspend fun cancel() {
        cancelCount++
    }
}
