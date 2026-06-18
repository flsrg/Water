package dev.flsrg.water.feature.water.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AddDrinkDialog(
    state: AddDrinkDialogState,
    onIntent: (WaterIntent) -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            onIntent(WaterIntent.AddDrinkDialogDismissed)
        },
        title = {
            Text(text = "Add drink")
        },
        text = {
            AddDrinkDialogContent(
                state = state,
                onIntent = onIntent,
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onIntent(WaterIntent.AddDrinkConfirmed)
                },
            ) {
                Text(text = "Add")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onIntent(WaterIntent.AddDrinkDialogDismissed)
                },
            ) {
                Text(text = "Cancel")
            }
        },
    )
}

@Composable
private fun AddDrinkDialogContent(
    state: AddDrinkDialogState,
    onIntent: (WaterIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "Drink type",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            state.drinkTypes.forEach { drinkType ->
                FilterChip(
                    selected = drinkType == state.selectedDrinkType,
                    onClick = {
                        onIntent(WaterIntent.DialogDrinkTypeSelected(drinkType))
                    },
                    label = {
                        Text(text = drinkType.label)
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Volume",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            state.volumeOptionsMl.forEach { volumeMl ->
                FilterChip(
                    selected = volumeMl == state.selectedAmountMl,
                    onClick = {
                        onIntent(WaterIntent.DialogVolumeSelected(volumeMl))
                    },
                    label = {
                        Text(text = "$volumeMl ml")
                    },
                )
            }
        }
    }
}

@Preview
@Composable
fun AddDrinkDialogPreview() {
    MaterialTheme {
        Surface {
            AddDrinkDialogContent(
                state = AddDrinkDialogState(),
                onIntent = { },
            )
        }
    }
}

@Preview
@Composable
fun AddDrinkDialogPreview2() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface {
            AddDrinkDialogContent(
                state = AddDrinkDialogState(),
                onIntent = { },
            )
        }
    }
}
