package dev.flsrg.water.feature.water.data

enum class DrinkType(
    val label: String,
    val hydrationMultiplier: Float,
) {
    Water("Water", 1.0f),
    Tea("Tea", 0.95f),
    Coffee("Coffee", 0.85f),
    Juice("Juice", 0.90f),
    Cola("Cola", 0.85f),
}
