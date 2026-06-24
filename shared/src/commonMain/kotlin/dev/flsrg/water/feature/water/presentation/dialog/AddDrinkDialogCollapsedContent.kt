package dev.flsrg.water.feature.water.presentation.dialog

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val AddDrinkButtonWidth = 56.dp
val AddDrinkButtonHeight = 56.dp

private val AddDrinkIconSize = 24.dp
private val AddDrinkIconStrokeWidth = 2.dp

@Composable
fun AddDrinkCollapsedContent(modifier: Modifier = Modifier) {
    val contentColor = LocalContentColor.current

    Box(
        modifier =
            modifier.size(
                width = AddDrinkButtonWidth,
                height = AddDrinkButtonHeight,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(AddDrinkIconSize)) {
            val strokeWidth = AddDrinkIconStrokeWidth.toPx()
            val halfStrokeWidth = strokeWidth / 2f
            val centerX = size.width / 2f
            val centerY = size.height / 2f

            drawLine(
                color = contentColor,
                start = Offset(centerX, halfStrokeWidth),
                end = Offset(centerX, size.height - halfStrokeWidth),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = contentColor,
                start = Offset(halfStrokeWidth, centerY),
                end = Offset(size.width - halfStrokeWidth, centerY),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
        }
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