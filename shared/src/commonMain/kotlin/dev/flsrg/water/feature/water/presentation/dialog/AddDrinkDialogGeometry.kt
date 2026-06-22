package dev.flsrg.water.feature.water.presentation.dialog

import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

fun Density.calculateMorphGeometry(
    anchorCenter: IntOffset,
    parentWidthPx: Int,
    parentHeightPx: Int,
    progress: Float,
): MorphGeometry {
    val marginPx = DialogScreenMargin.roundToPx()
    val collapsedWidthPx = AddDrinkButtonWidth.roundToPx()
    val collapsedHeightPx = AddDrinkButtonHeight.roundToPx()
    val expandedWidthPx =
        minOf(
            ExpandedMaxWidth.roundToPx(),
            parentWidthPx - marginPx * 2,
        ).coerceAtLeast(collapsedWidthPx)
    val expandedHeightPx =
        minOf(
            ExpandedPreferredHeight.roundToPx(),
            parentHeightPx - marginPx * 2,
        ).coerceAtLeast(collapsedHeightPx)

    val currentWidthPx =
        lerpInt(
            start = collapsedWidthPx,
            stop = expandedWidthPx,
            fraction = progress,
        )
    val currentHeightPx =
        lerpInt(
            start = collapsedHeightPx,
            stop = expandedHeightPx,
            fraction = progress,
        )

    return MorphGeometry(
        currentBounds =
            morphBoundsFor(
                anchorCenter = anchorCenter,
                parentWidthPx = parentWidthPx,
                parentHeightPx = parentHeightPx,
                marginPx = marginPx,
                widthPx = currentWidthPx,
                heightPx = currentHeightPx,
            ),
        expandedBounds =
            morphBoundsFor(
                anchorCenter = anchorCenter,
                parentWidthPx = parentWidthPx,
                parentHeightPx = parentHeightPx,
                marginPx = marginPx,
                widthPx = expandedWidthPx,
                heightPx = expandedHeightPx,
            ),
    )
}

private fun morphBoundsFor(
    anchorCenter: IntOffset,
    parentWidthPx: Int,
    parentHeightPx: Int,
    marginPx: Int,
    widthPx: Int,
    heightPx: Int,
): MorphBounds {
    val maxX =
        (parentWidthPx - widthPx - marginPx)
            .coerceAtLeast(marginPx)
    val maxY =
        (parentHeightPx - heightPx - marginPx)
            .coerceAtLeast(marginPx)

    return MorphBounds(
        x =
            (anchorCenter.x - widthPx / 2)
                .coerceIn(marginPx, maxX),
        y =
            (anchorCenter.y - heightPx / 2)
                .coerceIn(marginPx, maxY),
        width = widthPx,
        height = heightPx,
    )
}

fun Modifier.morphContainerLayout(bounds: MorphBounds): Modifier =
    offset {
        IntOffset(
            x = bounds.x,
            y = bounds.y,
        )
    }.layout { measurable, _ ->
        val placeable =
            measurable.measure(
                Constraints.fixed(
                    width = bounds.width,
                    height = bounds.height,
                ),
            )

        layout(
            width = bounds.width,
            height = bounds.height,
        ) {
            placeable.place(
                x = 0,
                y = 0,
            )
        }
    }

fun Modifier.expandedContentLayout(bounds: MorphBounds): Modifier =
    layout { measurable, constraints ->
        val placeable =
            measurable.measure(
                Constraints.fixed(
                    width = bounds.width,
                    height = bounds.height,
                ),
            )
        val width = constraints.maxWidth
        val height = constraints.maxHeight

        layout(
            width = width,
            height = height,
        ) {
            placeable.place(
                x = (width - bounds.width) / 2,
                y = (height - bounds.height) / 2,
            )
        }
    }

private fun lerpInt(
    start: Int,
    stop: Int,
    fraction: Float,
): Int =
    lerpFloat(
        start = start.toFloat(),
        stop = stop.toFloat(),
        fraction = fraction,
    ).roundToInt()
