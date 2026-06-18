package dev.flsrg.water.feature.water.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.roundToInt

@Composable
fun WaterRoute(viewModel: WaterViewModel = viewModel { WaterViewModel() }) {
    val state by viewModel.state.collectAsState()

    WaterScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun WaterScreen(
    state: WaterUiState,
    onIntent: (WaterIntent) -> Unit,
) {
    val isDialogOpen = state.addDrinkDialog != null
    var addButtonCenter by remember { mutableStateOf<IntOffset?>(null) }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            WaterSummary(
                summary = state.summary,
            )

            Spacer(modifier = Modifier.height(32.dp))

            AddDrinkButtonAnchor(
                onPositioned = { center ->
                    addButtonCenter = center
                },
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    onIntent(WaterIntent.ResetClicked)
                },
            ) {
                Text(text = "Reset")
            }
        }

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
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f),
        )
    }
}

@Composable
private fun AddDrinkButtonAnchor(
    onPositioned: (IntOffset) -> Unit,
) {
    Box(
        modifier = Modifier
            .size(
                width = AddDrinkButtonWidth,
                height = AddDrinkButtonHeight,
            )
            .onGloballyPositioned { coordinates ->
                val bounds = coordinates.boundsInRoot()

                onPositioned(
                    IntOffset(
                        x = (bounds.left + bounds.width / 2f).roundToInt(),
                        y = (bounds.top + bounds.height / 2f).roundToInt(),
                    ),
                )
            },
    )
}

@Preview
@Composable
fun WaterScreenPreviewLight() {
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
fun WaterScreenPreviewDark() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface {
            WaterScreen(
                state = WaterUiState(),
                onIntent = {},
            )
        }
    }
}
