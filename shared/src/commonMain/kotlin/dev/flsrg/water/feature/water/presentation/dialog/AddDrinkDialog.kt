package dev.flsrg.water.feature.water.presentation.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.flsrg.water.feature.water.presentation.AddDrinkDialogState
import dev.flsrg.water.feature.water.presentation.WaterIntent
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.lerp as lerpColor
import androidx.compose.ui.unit.lerp as lerpDp

private val DIALOG_SCREEN_MARGIN = 24.dp
val EXPANDED_MAX_WIDTH = 380.dp
val EXPANDED_PREFERRED_HEIGHT = 420.dp

private val CLOSED_CORNER_RADIUS = 50.dp
private val OPEN_CORNER_RADIUS = 36.dp

private const val OPEN_DURATION_MILLIS = 420
private const val CLOSE_DURATION_MILLIS = 260
private const val COLLAPSED_CONTENT_FADE_END_PROGRESS = 0.24f
private const val EXPANDED_CONTENT_FADE_START_PROGRESS = 0.56f

private val EMPHASIZED_DECELERATE =
    CubicBezierEasing(
        0.05f,
        0.70f,
        0.10f,
        1.00f,
    )

private val EMPHASIZED_ACCELERATE =
    CubicBezierEasing(
        0.30f,
        0.00f,
        0.80f,
        0.15f,
    )

@Composable
fun AddDrinkMorphingSurface(
    anchorCenter: IntOffset?,
    dialogState: AddDrinkDialogState?,
    onIntent: (WaterIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (anchorCenter == null) return

    val isExpanded = dialogState != null

    var lastDialogState by remember {
        mutableStateOf(AddDrinkDialogState())
    }

    LaunchedEffect(dialogState) {
        if (dialogState != null) {
            lastDialogState = dialogState
        }
    }

    BoxWithConstraints(
        modifier = modifier,
    ) {
        val density = LocalDensity.current
        val colorScheme = MaterialTheme.colorScheme

        val transition =
            updateTransition(
                targetState = isExpanded,
                label = "Add drink morph transition",
            )

        val progress by transition.animateFloat(
            transitionSpec = {
                if (false isTransitioningTo true) {
                    tween(
                        durationMillis = OPEN_DURATION_MILLIS,
                        easing = EMPHASIZED_DECELERATE,
                    )
                } else {
                    tween(
                        durationMillis = CLOSE_DURATION_MILLIS,
                        easing = EMPHASIZED_ACCELERATE,
                    )
                }
            },
            label = "Container morph progress",
        ) { expanded ->
            if (expanded) 1f else 0f
        }

        val geometry =
            with(density) {
                val parentWidthPx = maxWidth.roundToPx()
                val parentHeightPx = maxHeight.roundToPx()
                val marginPx = DIALOG_SCREEN_MARGIN.roundToPx()

                val collapsedWidthPx = AddDrinkButtonWidth.roundToPx()
                val collapsedHeightPx = AddDrinkButtonHeight.roundToPx()
                val expandedWidthPx =
                    minOf(
                        EXPANDED_MAX_WIDTH.roundToPx(),
                        parentWidthPx - marginPx * 2,
                    ).coerceAtLeast(collapsedWidthPx)
                val expandedHeightPx =
                    minOf(
                        EXPANDED_PREFERRED_HEIGHT.roundToPx(),
                        parentHeightPx - marginPx * 2,
                    ).coerceAtLeast(collapsedHeightPx)

                fun boundsFor(
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

                MorphGeometry(
                    currentBounds =
                        boundsFor(
                            widthPx = currentWidthPx,
                            heightPx = currentHeightPx,
                        ),
                    expandedBounds =
                        boundsFor(
                            widthPx = expandedWidthPx,
                            heightPx = expandedHeightPx,
                        ),
                )
            }

        val collapsedContentAlpha =
            1f -
                progressFraction(
                    progress = progress,
                    start = 0f,
                    end = COLLAPSED_CONTENT_FADE_END_PROGRESS,
                )
        val expandedContentAlpha =
            progressFraction(
                progress = progress,
                start = EXPANDED_CONTENT_FADE_START_PROGRESS,
                end = 1f,
            )
        val expandedContentScale = lerpFloat(0.96f, 1f, expandedContentAlpha)
        val shouldComposeExpandedContent =
            isExpanded || progress > EXPANDED_CONTENT_FADE_START_PROGRESS

        Surface(
            modifier =
                Modifier
                    .offset {
                        IntOffset(
                            x = geometry.currentBounds.x,
                            y = geometry.currentBounds.y,
                        )
                    }.layout { measurable, _ ->
                        val placeable =
                            measurable.measure(
                                Constraints.fixed(
                                    width = geometry.currentBounds.width,
                                    height = geometry.currentBounds.height,
                                ),
                            )

                        layout(
                            width = geometry.currentBounds.width,
                            height = geometry.currentBounds.height,
                        ) {
                            placeable.place(
                                x = 0,
                                y = 0,
                            )
                        }
                    }.clickable(
                        enabled = !isExpanded,
                        onClick = {
                            onIntent(WaterIntent.AddDrinkClicked)
                        },
                    ),
            shape =
                RoundedCornerShape(
                    lerpDp(
                        start = CLOSED_CORNER_RADIUS,
                        stop = OPEN_CORNER_RADIUS,
                        fraction = progress,
                    ),
                ),
            color =
                lerpColor(
                    start = colorScheme.primary,
                    stop = colorScheme.surfaceContainerHigh,
                    fraction = progress,
                ),
            contentColor =
                lerpColor(
                    start = colorScheme.onPrimary,
                    stop = colorScheme.onSurface,
                    fraction = progress,
                ),
            tonalElevation = lerpDp(0.dp, 8.dp, progress),
            shadowElevation = lerpDp(0.dp, 8.dp, progress),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                AddDrinkCollapsedContent(
                    modifier =
                        Modifier.graphicsLayer {
                            alpha = collapsedContentAlpha
                        },
                )

                if (shouldComposeExpandedContent) {
                    Box(
                        modifier =
                            Modifier
                                .graphicsLayer {
                                    alpha = expandedContentAlpha
                                    scaleX = expandedContentScale
                                    scaleY = expandedContentScale
                                }.layout { measurable, constraints ->
                                    val placeable =
                                        measurable.measure(
                                            Constraints.fixed(
                                                width = geometry.expandedBounds.width,
                                                height = geometry.expandedBounds.height,
                                            ),
                                        )
                                    val width = constraints.maxWidth
                                    val height = constraints.maxHeight

                                    layout(
                                        width = width,
                                        height = height,
                                    ) {
                                        placeable.place(
                                            x = (width - geometry.expandedBounds.width) / 2,
                                            y = (height - geometry.expandedBounds.height) / 2,
                                        )
                                    }
                                },
                    ) {
                        AddDrinkExpandedContent(
                            state = dialogState ?: lastDialogState,
                            onIntent = onIntent,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddDrinkScrim(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource =
        remember {
            MutableInteractionSource()
        }

    AnimatedVisibility(
        visible = visible,
        enter =
            fadeIn(
                animationSpec =
                    tween(
                        durationMillis = 180,
                        easing = LinearOutSlowInEasing,
                    ),
            ),
        exit =
            fadeOut(
                animationSpec =
                    tween(
                        durationMillis = 120,
                        easing = FastOutLinearInEasing,
                    ),
            ),
        modifier = modifier,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f),
                    ).clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onDismiss,
                    ),
        )
    }
}

private data class MorphGeometry(
    val currentBounds: MorphBounds,
    val expandedBounds: MorphBounds,
)

private data class MorphBounds(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
)

private fun progressFraction(
    progress: Float,
    start: Float,
    end: Float,
): Float = ((progress - start) / (end - start)).coerceIn(0f, 1f)

private fun lerpFloat(
    start: Float,
    stop: Float,
    fraction: Float,
): Float = start + (stop - start) * fraction

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
