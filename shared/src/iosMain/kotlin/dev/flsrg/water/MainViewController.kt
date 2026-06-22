package dev.flsrg.water

import androidx.compose.ui.window.ComposeUIViewController
import dev.flsrg.water.database.DatabaseDriverFactory

@Suppress("FunctionNaming", "ktlint:standard:function-naming")
fun MainViewController() =
    ComposeUIViewController {
        App(
            databaseDriverFactory = DatabaseDriverFactory(),
        )
    }
