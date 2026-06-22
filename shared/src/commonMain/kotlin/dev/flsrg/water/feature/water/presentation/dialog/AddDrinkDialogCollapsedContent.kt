package dev.flsrg.water.feature.water.presentation.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
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

val AddDrinkButtonWidth = 160.dp
val AddDrinkButtonHeight = 52.dp

@Composable
fun AddDrinkCollapsedContent(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier.size(
                width = AddDrinkButtonWidth,
                height = AddDrinkButtonHeight,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Add drink",
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Preview
@Composable
private fun AddDrinkCollapsedContentPreviewLight() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        Surface {
            AddDrinkCollapsedContent()
        }
    }
}

@Preview
@Composable
private fun AddDrinkCollapsedContentPreviewDark() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface {
            AddDrinkCollapsedContent()
        }
    }
}
