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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

private val DeleteRevealWidth = 96.dp
private val DeleteCardWidth = 88.dp

@Suppress("TopLevelPropertyNaming")
private const val OPEN_THRESHOLD_FRACTION = 0.4f

@Suppress("TopLevelPropertyNaming")
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
    val deleteRevealWidthPx = with(LocalDensity.current) { DeleteRevealWidth.toPx() }

    var rowWidthPx by remember { mutableIntStateOf(0) }
    var offsetX by remember(drink.id) { mutableFloatStateOf(0f) }
    var activeSwipeSide by remember(drink.id) { mutableStateOf<SwipeSide?>(null) }
    var isDeleting by remember(drink.id) { mutableStateOf(false) }

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
                    if (isDeleting || rowWidthPx == 0) return@launch

                    val deleteSide = swipeSideForDelete(activeSwipeSide, offsetX)

                    isDeleting = true
                    activeSwipeSide = deleteSide

                    offsetX =
                        animateDeleteOffset(
                            initialOffset = offsetX,
                            deleteSide = deleteSide,
                            rowWidthPx = rowWidthPx,
                        )

                    onDelete(drink)
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
                    }.swipeToRevealDelete(
                        drink.id,
                        deleteRevealWidthPx,
                        isDeleting,
                        offsetX = { offsetX },
                        onOffsetChange = { offsetX = it },
                        onSwipeSideChange = { activeSwipeSide = it },
                    ),
        )
    }
}

private fun Modifier.swipeToRevealDelete(
    drinkId: Long,
    deleteRevealWidthPx: Float,
    isDeleting: Boolean,
    offsetX: () -> Float,
    onOffsetChange: (Float) -> Unit,
    onSwipeSideChange: (SwipeSide?) -> Unit,
): Modifier =
    pointerInput(
        drinkId,
        deleteRevealWidthPx,
        isDeleting,
    ) {
        coroutineScope {
            detectHorizontalDragGestures(
                onHorizontalDrag = { change, dragAmount ->
                    if (isDeleting) return@detectHorizontalDragGestures

                    change.consume()

                    val newOffset =
                        (offsetX() + dragAmount)
                            .coerceIn(
                                minimumValue = -deleteRevealWidthPx,
                                maximumValue = deleteRevealWidthPx,
                            )

                    onOffsetChange(newOffset)
                    onSwipeSideChange(swipeSideForOffset(newOffset))
                },
                onDragEnd = {
                    if (isDeleting) return@detectHorizontalDragGestures

                    val settledOffset =
                        settledSwipeOffset(
                            offsetX = offsetX(),
                            deleteRevealWidthPx = deleteRevealWidthPx,
                        )

                    onSwipeSideChange(swipeSideForOffset(settledOffset))
                    launch {
                        onOffsetChange(animateSwipeOffset(offsetX(), settledOffset))
                    }
                },
                onDragCancel = {
                    if (isDeleting) return@detectHorizontalDragGestures

                    launch {
                        onOffsetChange(animateSwipeOffset(offsetX(), 0f))
                        onSwipeSideChange(null)
                    }
                },
            )
        }
    }

private fun swipeSideForOffset(offsetX: Float): SwipeSide? =
    when {
        offsetX > 0f -> SwipeSide.Start
        offsetX < 0f -> SwipeSide.End
        else -> null
    }

private fun swipeSideForDelete(
    activeSwipeSide: SwipeSide?,
    offsetX: Float,
): SwipeSide =
    activeSwipeSide
        ?: swipeSideForOffset(offsetX)
        ?: SwipeSide.End

private fun settledSwipeOffset(
    offsetX: Float,
    deleteRevealWidthPx: Float,
): Float {
    val openThreshold = deleteRevealWidthPx * OPEN_THRESHOLD_FRACTION

    return when {
        offsetX > openThreshold -> deleteRevealWidthPx
        offsetX < -openThreshold -> -deleteRevealWidthPx
        else -> 0f
    }
}

private suspend fun animateSwipeOffset(
    initialOffset: Float,
    targetOffset: Float,
): Float {
    var offsetX = initialOffset

    animate(
        initialValue = initialOffset,
        targetValue = targetOffset,
        animationSpec =
            spring(
                stiffness = Spring.StiffnessMediumLow,
            ),
    ) { value, _ ->
        offsetX = value
    }

    return offsetX
}

private suspend fun animateDeleteOffset(
    initialOffset: Float,
    deleteSide: SwipeSide,
    rowWidthPx: Int,
): Float {
    var offsetX = initialOffset
    val targetOffset =
        when (deleteSide) {
            SwipeSide.Start -> rowWidthPx.toFloat()
            SwipeSide.End -> -rowWidthPx.toFloat()
        }

    animate(
        initialValue = initialOffset,
        targetValue = targetOffset,
        animationSpec =
            tween(
                durationMillis = DELETE_EXIT_ANIMATION_MILLIS,
            ),
    ) { value, _ ->
        offsetX = value
    }

    return offsetX
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
