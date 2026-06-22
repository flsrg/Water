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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.flsrg.water.feature.water.presentation.AddDrinkDialogState
import dev.flsrg.water.feature.water.presentation.WaterIntent
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.lerp as lerpColor
import androidx.compose.ui.unit.lerp as lerpDp

val DialogScreenMargin = 24.dp
val ExpandedMaxWidth = 380.dp
val ExpandedPreferredHeight = 420.dp

private val ClosedCornerRadius = 50.dp
private val OpenCornerRadius = 36.dp

private object MorphTiming {
    const val OPEN_DURATION_MILLIS = 420
    const val CLOSE_DURATION_MILLIS = 260
    const val COLLAPSED_CONTENT_FADE_END_PROGRESS = 0.24f
    const val EXPANDED_CONTENT_FADE_START_PROGRESS = 0.56f
}

private val EmphasizedDecelerate =
    CubicBezierEasing(
        0.05f,
        0.70f,
        0.10f,
        1.00f,
    )

private val EmphasizedAccelerate =
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
        val progress = rememberMorphProgress(isExpanded = isExpanded)
        val geometry =
            with(density) {
                calculateMorphGeometry(
                    anchorCenter = anchorCenter,
                    parentWidthPx = maxWidth.roundToPx(),
                    parentHeightPx = maxHeight.roundToPx(),
                    progress = progress,
                )
            }

        val collapsedContentAlpha =
            1f -
                progressFraction(
                    progress = progress,
                    start = 0f,
                    end = MorphTiming.COLLAPSED_CONTENT_FADE_END_PROGRESS,
                )
        val expandedContentAlpha =
            progressFraction(
                progress = progress,
                start = MorphTiming.EXPANDED_CONTENT_FADE_START_PROGRESS,
                end = 1f,
            )
        val expandedContentScale = lerpFloat(0.96f, 1f, expandedContentAlpha)
        val shouldComposeExpandedContent =
            isExpanded || progress > MorphTiming.EXPANDED_CONTENT_FADE_START_PROGRESS

        AddDrinkMorphingContainer(
            geometry = geometry,
            progress = progress,
            isExpanded = isExpanded,
            collapsedContentAlpha = collapsedContentAlpha,
            expandedContentAlpha = expandedContentAlpha,
            expandedContentScale = expandedContentScale,
            shouldComposeExpandedContent = shouldComposeExpandedContent,
            dialogState = dialogState ?: lastDialogState,
            colorScheme = colorScheme,
            onIntent = onIntent,
        )
    }
}

@Composable
private fun rememberMorphProgress(isExpanded: Boolean): Float {
    val transition =
        updateTransition(
            targetState = isExpanded,
            label = "Add drink morph transition",
        )

    val progress by transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) {
                tween(
                    durationMillis = MorphTiming.OPEN_DURATION_MILLIS,
                    easing = EmphasizedDecelerate,
                )
            } else {
                tween(
                    durationMillis = MorphTiming.CLOSE_DURATION_MILLIS,
                    easing = EmphasizedAccelerate,
                )
            }
        },
        label = "Container morph progress",
    ) { expanded ->
        if (expanded) 1f else 0f
    }

    return progress
}

@Composable
private fun AddDrinkMorphingContainer(
    geometry: MorphGeometry,
    progress: Float,
    isExpanded: Boolean,
    collapsedContentAlpha: Float,
    expandedContentAlpha: Float,
    expandedContentScale: Float,
    shouldComposeExpandedContent: Boolean,
    dialogState: AddDrinkDialogState,
    colorScheme: ColorScheme,
    onIntent: (WaterIntent) -> Unit,
) {
    Surface(
        modifier =
            Modifier
                .morphContainerLayout(geometry.currentBounds)
                .clickable(
                    enabled = !isExpanded,
                    onClick = {
                        onIntent(WaterIntent.AddDrinkClicked)
                    },
                ),
        shape =
            RoundedCornerShape(
                lerpDp(
                    start = ClosedCornerRadius,
                    stop = OpenCornerRadius,
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
        AddDrinkMorphingContent(
            geometry = geometry,
            collapsedContentAlpha = collapsedContentAlpha,
            expandedContentAlpha = expandedContentAlpha,
            expandedContentScale = expandedContentScale,
            shouldComposeExpandedContent = shouldComposeExpandedContent,
            dialogState = dialogState,
            onIntent = onIntent,
        )
    }
}

@Composable
private fun AddDrinkMorphingContent(
    geometry: MorphGeometry,
    collapsedContentAlpha: Float,
    expandedContentAlpha: Float,
    expandedContentScale: Float,
    shouldComposeExpandedContent: Boolean,
    dialogState: AddDrinkDialogState,
    onIntent: (WaterIntent) -> Unit,
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
                        }.expandedContentLayout(geometry.expandedBounds),
            ) {
                AddDrinkExpandedContent(
                    state = dialogState,
                    onIntent = onIntent,
                )
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

data class MorphGeometry(
    val currentBounds: MorphBounds,
    val expandedBounds: MorphBounds,
)

data class MorphBounds(
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

fun lerpFloat(
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
