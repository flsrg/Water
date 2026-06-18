package dev.flsrg.water.feature.water.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

private val DIALOG_SCREEN_MARGIN = 24.dp
val EXPANDED_MAX_WIDTH = 328.dp
val EXPANDED_PREFERRED_HEIGHT = 344.dp

private val CLOSED_CORNER_RADIUS = 50.dp
private val OPEN_CORNER_RADIUS = 36.dp

private const val OPEN_DURATION_MILLIS = 380
private const val CLOSE_DURATION_MILLIS = 280
private const val EXPANDED_CONTENT_DELAY_MILLIS = 170

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

    var showExpandedContent by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            // Let the lightweight container morph first.
            delay(240.milliseconds)
            showExpandedContent = true
        } else {
            showExpandedContent = false
        }
    }

    BoxWithConstraints(
        modifier = modifier,
    ) {
        val density = LocalDensity.current

        val availableWidth =
            (maxWidth - DIALOG_SCREEN_MARGIN * 2f)
                .coerceAtLeast(AddDrinkButtonWidth)

        val availableHeight =
            (maxHeight - DIALOG_SCREEN_MARGIN * 2f)
                .coerceAtLeast(AddDrinkButtonHeight)

        val expandedWidth =
            minOf(
                EXPANDED_MAX_WIDTH,
                availableWidth,
            )

        val expandedHeight =
            minOf(
                EXPANDED_PREFERRED_HEIGHT,
                availableHeight,
            )

        val transition =
            updateTransition(
                targetState = isExpanded,
                label = "Add drink morph transition",
            )

        val width by transition.animateDp(
            transitionSpec = {
                if (false isTransitioningTo true) {
                    keyframes {
                        durationMillis = OPEN_DURATION_MILLIS

                        AddDrinkButtonWidth at 0 using EMPHASIZED_DECELERATE
                        expandedWidth at 300 using EMPHASIZED_DECELERATE
                        expandedWidth at OPEN_DURATION_MILLIS
                    }
                } else {
                    keyframes {
                        durationMillis = CLOSE_DURATION_MILLIS

                        expandedWidth at 0 using EMPHASIZED_ACCELERATE
                        AddDrinkButtonWidth at 220 using EMPHASIZED_ACCELERATE
                        AddDrinkButtonWidth at CLOSE_DURATION_MILLIS
                    }
                }
            },
            label = "Container width",
        ) { expanded ->
            if (expanded) expandedWidth else AddDrinkButtonWidth
        }

        val height by transition.animateDp(
            transitionSpec = {
                if (false isTransitioningTo true) {
                    keyframes {
                        durationMillis = OPEN_DURATION_MILLIS

                        AddDrinkButtonHeight at 0
                        AddDrinkButtonHeight at 120 using EMPHASIZED_DECELERATE
                        expandedHeight at OPEN_DURATION_MILLIS using FastOutSlowInEasing
                    }
                } else {
                    keyframes {
                        durationMillis = CLOSE_DURATION_MILLIS

                        expandedHeight at 0 using EMPHASIZED_ACCELERATE
                        AddDrinkButtonHeight at 190 using EMPHASIZED_ACCELERATE
                        AddDrinkButtonHeight at CLOSE_DURATION_MILLIS
                    }
                }
            },
            label = "Container height",
        ) { expanded ->
            if (expanded) expandedHeight else AddDrinkButtonHeight
        }

        val circularity by transition.animateFloat(
            transitionSpec = {
                if (false isTransitioningTo true) {
                    keyframes {
                        durationMillis = OPEN_DURATION_MILLIS

                        1f at 0
                        1f at 220 using EMPHASIZED_ACCELERATE
                        0f at OPEN_DURATION_MILLIS using FastOutSlowInEasing
                    }
                } else {
                    keyframes {
                        durationMillis = CLOSE_DURATION_MILLIS

                        0f at 0
                        1f at 150 using EMPHASIZED_ACCELERATE
                        1f at CLOSE_DURATION_MILLIS
                    }
                }
            },
            label = "Container circularity",
        ) { expanded ->
            if (expanded) 0f else 1f
        }

        val cornerRadius =
            lerp(
                start = OPEN_CORNER_RADIUS,
                stop = CLOSED_CORNER_RADIUS,
                fraction = circularity,
            )

        val containerColor by transition.animateColor(
            transitionSpec = {
                tween(
                    durationMillis = 220,
                    easing = FastOutSlowInEasing,
                )
            },
            label = "Container color",
        ) { expanded ->
            if (expanded) {
                MaterialTheme.colorScheme.surfaceContainerHigh
            } else {
                MaterialTheme.colorScheme.primary
            }
        }

        val contentColor by transition.animateColor(
            transitionSpec = {
                tween(
                    durationMillis = 220,
                    easing = FastOutSlowInEasing,
                )
            },
            label = "Content color",
        ) { expanded ->
            if (expanded) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onPrimary
            }
        }

        val elevation by transition.animateDp(
            transitionSpec = {
                if (false isTransitioningTo true) {
                    tween(
                        durationMillis = 220,
                        delayMillis = 80,
                        easing = LinearOutSlowInEasing,
                    )
                } else {
                    tween(
                        durationMillis = 120,
                        easing = FastOutLinearInEasing,
                    )
                }
            },
            label = "Container elevation",
        ) { expanded ->
            if (expanded) 8.dp else 0.dp
        }

        val collapsedContentAlpha by transition.animateFloat(
            transitionSpec = {
                if (false isTransitioningTo true) {
                    tween(
                        durationMillis = 80,
                        easing = FastOutLinearInEasing,
                    )
                } else {
                    tween(
                        durationMillis = 120,
                        delayMillis = 120,
                        easing = LinearOutSlowInEasing,
                    )
                }
            },
            label = "Collapsed content alpha",
        ) { expanded ->
            if (expanded) 0f else 1f
        }

        val expandedContentAlpha by animateFloatAsState(
            targetValue = if (showExpandedContent) 1f else 0f,
            animationSpec =
                tween(
                    durationMillis = 160,
                    easing = LinearOutSlowInEasing,
                ),
            label = "Expanded content alpha",
        )

        val offset =
            with(density) {
                val parentWidthPx = maxWidth.roundToPx()
                val parentHeightPx = maxHeight.roundToPx()

                val widthPx = width.roundToPx()
                val heightPx = height.roundToPx()
                val marginPx = DIALOG_SCREEN_MARGIN.roundToPx()

                val minX = marginPx
                val minY = marginPx

                val maxX =
                    (parentWidthPx - widthPx - marginPx)
                        .coerceAtLeast(minX)

                val maxY =
                    (parentHeightPx - heightPx - marginPx)
                        .coerceAtLeast(minY)

                val x =
                    (anchorCenter.x - widthPx / 2)
                        .coerceIn(minX, maxX)

                val y =
                    (anchorCenter.y - heightPx / 2)
                        .coerceIn(minY, maxY)

                IntOffset(
                    x = x,
                    y = y,
                )
            }

        Surface(
            modifier =
                Modifier
                    .offset { offset }
                    .size(
                        width = width,
                        height = height,
                    ).clickable(
                        enabled = !isExpanded,
                        onClick = {
                            onIntent(WaterIntent.AddDrinkClicked)
                        },
                    ),
            shape = RoundedCornerShape(cornerRadius),
            color = containerColor,
            contentColor = contentColor,
            tonalElevation = elevation,
            shadowElevation = if (showExpandedContent) elevation else 0.dp,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier =
                        Modifier.graphicsLayer {
                            alpha = collapsedContentAlpha
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    AddDrinkCollapsedContent()
                }

                if (showExpandedContent || expandedContentAlpha > 0f) {
                    Box(
                        modifier =
                            Modifier.graphicsLayer {
                                alpha = expandedContentAlpha
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
