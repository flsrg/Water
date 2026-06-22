package dev.flsrg.water.feature.water.presentation.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.flsrg.water.feature.water.data.DrinkLogItem
import dev.flsrg.water.feature.water.data.DrinkType

@Suppress("TopLevelPropertyNaming")
private const val PREVIEW_DRINK_VOLUME_ML = 100

@Suppress("TopLevelPropertyNaming")
private const val PREVIEW_HYDRATION_ML = 50

@Composable
fun DrinksLogRow(
    drink: DrinkLogItem,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors =
            CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        border =
            BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = drink.drinkType.label,
                    style = MaterialTheme.typography.titleSmall,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${drink.volumeMl} ml",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "+${drink.hydrationMl} ml",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview
@Composable
private fun DrinksLogRowPreviewLight() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            DrinksLogRow(
                drink =
                    DrinkLogItem(0, DrinkType.Coffee, PREVIEW_DRINK_VOLUME_ML, PREVIEW_HYDRATION_ML, 0),
            )
        }
    }
}

@Preview
@Composable
private fun DrinksLogRowPreviewDark() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            DrinksLogRow(
                drink =
                    DrinkLogItem(0, DrinkType.Coffee, PREVIEW_DRINK_VOLUME_ML, PREVIEW_HYDRATION_ML, 0),
            )
        }
    }
}
