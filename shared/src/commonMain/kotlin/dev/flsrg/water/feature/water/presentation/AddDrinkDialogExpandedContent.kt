package dev.flsrg.water.feature.water.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AddDrinkExpandedContent(
    state: AddDrinkDialogState,
    onIntent: (WaterIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Text(
            text = "Add drink",
            style = MaterialTheme.typography.headlineSmall,
        )

        Spacer(modifier = Modifier.height(20.dp))

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

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(
                onClick = {
                    onIntent(WaterIntent.AddDrinkDialogDismissed)
                },
            ) {
                Text(text = "Cancel")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    onIntent(WaterIntent.AddDrinkConfirmed)
                },
            ) {
                Text(text = "Add")
            }
        }
    }
}

@Preview
@Composable
fun AddDrinkExpandedContentPreviewLight() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        Surface(
            modifier = Modifier.size(
                width = EXPANDED_MAX_WIDTH,
                height = EXPANDED_PREFERRED_HEIGHT,
            ),
        ) {
            AddDrinkExpandedContent(
                state = AddDrinkDialogState(),
                onIntent = {},
            )
        }
    }
}

@Preview
@Composable
fun AddDrinkExpandedContentPreviewDark() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(
            modifier = Modifier.size(
                width = EXPANDED_MAX_WIDTH,
                height = EXPANDED_PREFERRED_HEIGHT,
            ),
        ) {
            AddDrinkExpandedContent(
                state = AddDrinkDialogState(),
                onIntent = {},
            )
        }
    }
}
