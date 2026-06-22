package dev.flsrg.water

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.flsrg.water.database.DatabaseDriverFactory
import dev.flsrg.water.database.WaterDatabase
import dev.flsrg.water.feature.water.data.SqlDelightWaterRepository
import dev.flsrg.water.feature.water.presentation.WaterRoute

@Composable
fun App(databaseDriverFactory: DatabaseDriverFactory) {
    val repository =
        remember {
            SqlDelightWaterRepository(
                database =
                    WaterDatabase(
                        driver = databaseDriverFactory.createDriver(),
                    ),
            )
        }

    MaterialTheme {
        WaterRoute(repository = repository)
    }
}
