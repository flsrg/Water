package dev.flsrg.water.feature.water.presentation.list

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dev.flsrg.water.feature.water.data.DrinkLogItem
import kotlinx.coroutines.launch

private val DeleteRevealWidth = 96.dp
private val DeleteCardWidth = 88.dp

private const val OPEN_THRESHOLD_FRACTION = 0.4f
private const val DELETE_EXIT_ANIMATION_MILLIS = 220

private enum class SwipeSide {
    Start,
    End,
}

@Composable
fun SwipeToDeleteDrinkRow(
    drink: DrinkLogItem,
    onDelete: (DrinkLogItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    val deleteRevealWidthPx =
        with(density) {
            DeleteRevealWidth.toPx()
        }

    var rowWidthPx by remember {
        mutableIntStateOf(0)
    }

    var offsetX by remember(drink.id) {
        mutableFloatStateOf(0f)
    }

    var activeSwipeSide by remember(drink.id) {
        mutableStateOf<SwipeSide?>(null)
    }

    var isDeleting by remember(drink.id) {
        mutableStateOf(false)
    }

    suspend fun animateOffsetTo(targetValue: Float) {
        animate(
            initialValue = offsetX,
            targetValue = targetValue,
            animationSpec =
                spring(
                    stiffness = Spring.StiffnessMediumLow,
                ),
        ) { value, _ ->
            offsetX = value
        }
    }

    suspend fun closeSwipe() {
        animateOffsetTo(0f)
        activeSwipeSide = null
    }

    suspend fun animateDeleteAndNotify() {
        if (isDeleting || rowWidthPx == 0) return

        val deleteSide =
            activeSwipeSide
                ?: when {
                    offsetX > 0f -> SwipeSide.Start
                    offsetX < 0f -> SwipeSide.End
                    else -> SwipeSide.End
                }

        isDeleting = true
        activeSwipeSide = deleteSide

        val targetOffset =
            when (deleteSide) {
                SwipeSide.Start -> rowWidthPx.toFloat()
                SwipeSide.End -> -rowWidthPx.toFloat()
            }

        animate(
            initialValue = offsetX,
            targetValue = targetOffset,
            animationSpec =
                tween(
                    durationMillis = DELETE_EXIT_ANIMATION_MILLIS,
                ),
        ) { value, _ ->
            offsetX = value
        }

        onDelete(drink)
    }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .onSizeChanged { size ->
                    rowWidthPx = size.width
                },
    ) {
        DeleteBackground(
            activeSwipeSide = activeSwipeSide,
            onDeleteClick = {
                coroutineScope.launch {
                    animateDeleteAndNotify()
                }
            },
            modifier = Modifier.matchParentSize(),
        )

        DrinksLogRow(
            drink = drink,
            modifier =
                Modifier
                    .graphicsLayer {
                        translationX = offsetX
                    }.pointerInput(
                        drink.id,
                        deleteRevealWidthPx,
                        isDeleting,
                    ) {
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { change, dragAmount ->
                                if (isDeleting) return@detectHorizontalDragGestures

                                change.consume()

                                val newOffset =
                                    (offsetX + dragAmount)
                                        .coerceIn(
                                            minimumValue = -deleteRevealWidthPx,
                                            maximumValue = deleteRevealWidthPx,
                                        )

                                offsetX = newOffset

                                activeSwipeSide =
                                    when {
                                        newOffset > 0f -> SwipeSide.Start
                                        newOffset < 0f -> SwipeSide.End
                                        else -> null
                                    }
                            },
                            onDragEnd = {
                                if (isDeleting) return@detectHorizontalDragGestures

                                val openThreshold =
                                    deleteRevealWidthPx * OPEN_THRESHOLD_FRACTION

                                val targetOffset =
                                    when {
                                        offsetX > openThreshold -> {
                                            activeSwipeSide = SwipeSide.Start
                                            deleteRevealWidthPx
                                        }

                                        offsetX < -openThreshold -> {
                                            activeSwipeSide = SwipeSide.End
                                            -deleteRevealWidthPx
                                        }

                                        else -> {
                                            0f
                                        }
                                    }

                                coroutineScope.launch {
                                    if (targetOffset == 0f) {
                                        closeSwipe()
                                    } else {
                                        animateOffsetTo(targetOffset)
                                    }
                                }
                            },
                            onDragCancel = {
                                if (isDeleting) return@detectHorizontalDragGestures

                                coroutineScope.launch {
                                    closeSwipe()
                                }
                            },
                        )
                    },
        )
    }
}

@Composable
private fun DeleteBackground(
    activeSwipeSide: SwipeSide?,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        when (activeSwipeSide) {
            SwipeSide.Start -> {
                DeleteActionCard(
                    onClick = onDeleteClick,
                    modifier =
                        Modifier
                            .align(Alignment.CenterStart)
                            .width(DeleteCardWidth)
                            .fillMaxHeight(),
                )
            }

            SwipeSide.End -> {
                DeleteActionCard(
                    onClick = onDeleteClick,
                    modifier =
                        Modifier
                            .align(Alignment.CenterEnd)
                            .width(DeleteCardWidth)
                            .fillMaxHeight(),
                )
            }

            null -> {}
        }
    }
}

@Composable
private fun DeleteActionCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier =
            modifier.clickable(
                role = Role.Button,
                onClick = onClick,
            ),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Delete",
                modifier = Modifier.padding(horizontal = 8.dp),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}
