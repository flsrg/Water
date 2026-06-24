package dev.flsrg.water.feature.water.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import dev.flsrg.water.feature.water.data.WaterRepository
import dev.flsrg.water.feature.water.presentation.dialog.AddDrinkButtonHeight
import dev.flsrg.water.feature.water.presentation.dialog.AddDrinkButtonWidth
import dev.flsrg.water.feature.water.presentation.dialog.AddDrinkMorphingSurface
import dev.flsrg.water.feature.water.presentation.dialog.AddDrinkScrim
import dev.flsrg.water.feature.water.presentation.list.DrinksLog
import kotlin.math.roundToInt

@Composable
fun WaterRoute(
    repository: WaterRepository,
    viewModel: WaterViewModel = viewModel { WaterViewModel(repository) },
) {
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