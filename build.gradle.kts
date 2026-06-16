import dev.detekt.gradle.extensions.DetektExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ktlint) apply false
}

subprojects {
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        pluginManager.apply("org.jlleitschuh.gradle.ktlint")
        pluginManager.apply("dev.detekt")

        extensions.configure<KtlintExtension> {
            version.set(libs.versions.ktlint.get())
            filter {
                exclude { element ->
                    element.file.toPath().startsWith(layout.buildDirectory.get().asFile.toPath())
                }
            }
        }

        extensions.configure<DetektExtension> {
            toolVersion = libs.versions.detekt.get()
            source.setFrom(files("src"))
            buildUponDefaultConfig = true
            config.setFrom(rootProject.file("config/detekt/detekt.yml"))
            basePath.set(rootProject.layout.projectDirectory)
        }

        dependencies {
            add("ktlintRuleset", libs.compose.rules.ktlint)
            add("detektPlugins", libs.compose.rules.detekt)
        }
    }
}
