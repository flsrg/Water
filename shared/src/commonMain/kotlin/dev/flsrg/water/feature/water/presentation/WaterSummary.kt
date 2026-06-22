package dev.flsrg.water.feature.water.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun WaterSummary(
    summary: WaterSummaryState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Water today",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${summary.consumedMl} ml",
            style = MaterialTheme.typography.displayMedium,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Remaining: ${summary.remainingMl} ml",
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = { summary.progress },
        )
    }
}

@Preview
@Composable
private fun WaterSummaryPreviewLight() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        Surface {
            WaterSummary(
                summary =
                    WaterSummaryState(
                        consumedMl = 100,
                    ),
            )
        }
    }
}

@Preview
@Composable
private fun WaterSummaryPreviewDark() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface {
            WaterSummary(
                summary =
                    WaterSummaryState(
                        consumedMl = 100,
                    ),
            )
        }
    }
}
