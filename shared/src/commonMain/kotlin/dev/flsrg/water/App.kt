package dev.flsrg.water

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.flsrg.water.feature.water.presentation.WaterRoute

@Composable
fun App() {
    MaterialTheme {
        WaterRoute()
    }
}

@Preview
@Composable
private fun AppPreview() {
    App()
}
