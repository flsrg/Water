package dev.flsrg.water.feature.water.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

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
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Water today",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Main water counter
        Text(
            text = "${state.consumedMl}",
            style = MaterialTheme.typography.displayMedium,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Remaining
        Text(
            text = "Remaining: ${state.remainingMl} ml",
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Progress
        LinearProgressIndicator(
            progress = { state.progress },
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onIntent(WaterIntent.AddWaterClicked) },
        ) {
            Text(text = "Add water")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { onIntent(WaterIntent.ResetClicked) }
        ) {
            Text(text = "Reset")
        }
    }
}

@Preview
@Composable
fun WaterScreenPreview() {
    WaterScreen(
        state = WaterUiState(consumedMl = 500),
        onIntent = {},
    )
}
