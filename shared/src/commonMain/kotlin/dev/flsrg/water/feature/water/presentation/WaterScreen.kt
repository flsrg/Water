package dev.flsrg.water.feature.water.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.flsrg.water.feature.reminder.ReminderSettings
import dev.flsrg.water.feature.water.presentation.dialog.AddDrinkButtonHeight
import dev.flsrg.water.feature.water.presentation.dialog.AddDrinkButtonWidth
import dev.flsrg.water.feature.water.presentation.dialog.AddDrinkMorphingSurface
import dev.flsrg.water.feature.water.presentation.dialog.AddDrinkScrim
import dev.flsrg.water.feature.water.presentation.list.DrinksLog
import kotlin.math.roundToInt

@Composable
fun WaterScreen(
    state: WaterUiState,
    onIntent: (WaterIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDialogOpen = state.addDrinkDialog != null
    var addButtonCenter by remember { mutableStateOf<IntOffset?>(null) }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            WaterSummary(
                summary = state.summary,
            )

            Spacer(modifier = Modifier.height(24.dp))

            ReminderSettingsSection(
                settings = state.reminderSettings,
                onIntent = onIntent,
            )

            Spacer(modifier = Modifier.height(32.dp))

            DrinksLog(
                drinks = state.recentDrinks,
                onDeleteDrink = {
                    onIntent(WaterIntent.DeleteDrinkClicked(it.id))
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
            )
        }

        AddDrinkButtonAnchor(
            onPosition = { center ->
                addButtonCenter = center
            },
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(
                        end = 24.dp,
                        bottom = 24.dp,
                    ),
        )

        AddDrinkScrim(
            visible = isDialogOpen,
            onDismiss = {
                onIntent(WaterIntent.AddDrinkDialogDismissed)
            },
            modifier = Modifier.zIndex(1f),
        )

        AddDrinkMorphingSurface(
            anchorCenter = addButtonCenter,
            dialogState = state.addDrinkDialog,
            onIntent = onIntent,
            modifier =
                Modifier
                    .fillMaxSize()
                    .zIndex(2f),
        )
    }
}

@Composable
private fun AddDrinkButtonAnchor(
    onPosition: (IntOffset) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(
                    width = AddDrinkButtonWidth,
                    height = AddDrinkButtonHeight,
                ).onGloballyPositioned { coordinates ->
                    val bounds = coordinates.boundsInRoot()

                    onPosition(
                        IntOffset(
                            x = (bounds.left + bounds.width / 2f).roundToInt(),
                            y = (bounds.top + bounds.height / 2f).roundToInt(),
                        ),
                    )
                },
    )
}

@Composable
private fun ReminderSettingsSection(
    settings: ReminderSettings,
    onIntent: (WaterIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val intervalOptions = listOf(15L, 30L, 60L, 120L)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = "Reminders",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Every ${settings.intervalMinutes} min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Switch(
                checked = settings.enabled,
                onCheckedChange = {
                    onIntent(WaterIntent.RemindersEnabledChanged(it))
                },
            )
        }

        FlowRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .alpha(if (settings.enabled) 1f else 0.48f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            intervalOptions.forEach { minutes ->
                FilterChip(
                    selected = settings.intervalMinutes == minutes,
                    enabled = settings.enabled,
                    onClick = {
                        onIntent(WaterIntent.ReminderIntervalChanged(minutes))
                    },
                    label = {
                        Text(text = "$minutes min")
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun WaterScreenPreviewLight() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        Surface {
            WaterScreen(
                state = WaterUiState(),
                onIntent = {},
            )
        }
    }
}

@Preview(uiMode = 32, name = "Dark Mode")
@Composable
private fun WaterScreenPreviewDark() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface {
            WaterScreen(
                state = WaterUiState(),
                onIntent = {},
            )
        }
    }
}
