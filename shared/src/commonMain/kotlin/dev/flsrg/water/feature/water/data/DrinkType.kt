package dev.flsrg.water.feature.water.data

private object DrinkHydrationMultiplier {
    const val FULL = 1.0f
    const val TEA = 0.95f
    const val JUICE = 0.90f
    const val CAFFEINATED = 0.85f
}

enum class DrinkType(
    val label: String,
    val hydrationMultiplier: Float,
) {
    Water("Water", DrinkHydrationMultiplier.FULL),
    Tea("Tea", DrinkHydrationMultiplier.TEA),
    Coffee("Coffee", DrinkHydrationMultiplier.CAFFEINATED),
    Juice("Juice", DrinkHydrationMultiplier.JUICE),
    Cola("Cola", DrinkHydrationMultiplier.CAFFEINATED),
}
