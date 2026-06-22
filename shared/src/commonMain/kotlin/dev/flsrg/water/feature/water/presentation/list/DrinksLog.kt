package dev.flsrg.water.feature.water.presentation.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.flsrg.water.feature.water.data.DrinkLogItem
import dev.flsrg.water.feature.water.data.DrinkType

@Composable
fun DrinksLog(
    drinks: List<DrinkLogItem>,
    onDeleteDrink: (DrinkLogItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    var previousDrinkIds by remember {
        mutableStateOf<Set<Long>?>(null)
    }

    val currentDrinkIds =
        remember(drinks) {
            drinks.map { it.id }.toSet()
        }

    val newlyAddedDrinkIds =
        remember(currentDrinkIds, previousDrinkIds) {
            val previousIds = previousDrinkIds
            if (previousIds == null) {
                emptySet()
            } else {
                currentDrinkIds - previousIds
            }
        }

    LaunchedEffect(currentDrinkIds) {
        previousDrinkIds = currentDrinkIds
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(
            key = "drinks_log_title",
            contentType = "title",
        ) {
            Text(
                text = "Drinks log",
                style = MaterialTheme.typography.titleLarge,
            )
        }

        if (drinks.isEmpty()) {
            item(
                key = "empty_drinks_log",
                contentType = "empty_state",
            ) {
                EmptyDrinksLog()
            }
        } else {
            items(
                items = drinks,
                key = { it.id },
                contentType = { "drink_log_item" },
            ) { drink ->
                AnimatedDrinksLogRow(
                    drink = drink,
                    onDelete = onDeleteDrink,
                    animateAppearance = drink.id in newlyAddedDrinkIds,
                    modifier =
                        Modifier.animateItem(
                            fadeInSpec = tween(durationMillis = 180),
                            fadeOutSpec = tween(durationMillis = 120),
                            placementSpec =
                                spring(
                                    stiffness = Spring.StiffnessLow,
                                ),
                        ),
                )
            }
        }
    }
}

@Composable
private fun AnimatedDrinksLogRow(
    drink: DrinkLogItem,
    animateAppearance: Boolean,
    onDelete: (DrinkLogItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val visibleState =
        remember(drink.id) {
            MutableTransitionState(!animateAppearance).apply {
                targetState = true
            }
        }

    AnimatedVisibility(
        visibleState = visibleState,
        modifier = modifier,
        enter =
            slideInVertically(
                animationSpec = tween(durationMillis = 260),
                initialOffsetY = { fullHeight ->
                    -fullHeight
                },
            ) +
                expandVertically(
                    animationSpec = tween(durationMillis = 260),
                    expandFrom = Alignment.Top,
                ) +
                fadeIn(
                    animationSpec = tween(durationMillis = 160),
                ),
        exit =
            slideOutVertically(
                animationSpec = tween(durationMillis = 180),
                targetOffsetY = { fullHeight ->
                    -fullHeight
                },
            ) +
                shrinkVertically(
                    animationSpec = tween(durationMillis = 180),
                    shrinkTowards = Alignment.Top,
                ) +
                fadeOut(
                    animationSpec = tween(durationMillis = 120),
                ),
    ) {
        SwipeToDeleteDrinkRow(
            drink = drink,
            onDelete = onDelete,
        )
    }
}

private val drinksPreviewList =
    listOf(
        DrinkLogItem(
            id = 0,
            drinkType = DrinkType.Water,
            volumeMl = 300,
            hydrationMl = 300,
            createdAtEpochMillis = 0,
        ),
        DrinkLogItem(
            id = 1,
            drinkType = DrinkType.Coffee,
            volumeMl = 300,
            hydrationMl = 150,
            createdAtEpochMillis = 1,
        ),
        DrinkLogItem(
            id = 2,
            drinkType = DrinkType.Cola,
            volumeMl = 500,
            hydrationMl = 150,
            createdAtEpochMillis = 0,
        ),
    )

@Preview
@Composable
private fun DrinksLogPreviewLight() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            DrinksLog(
                drinks = drinksPreviewList,
                onDeleteDrink = {},
            )
        }
    }
}

@Preview
@Composable
private fun DrinksLogPreviewDark() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            DrinksLog(
                drinks = drinksPreviewList,
                onDeleteDrink = {},
            )
        }
    }
}
