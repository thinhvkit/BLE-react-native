plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinNativeCocoaPods) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.sqldelightApp) apply false
    alias(libs.plugins.skie) apply false
    alias(libs.plugins.buildkonfig) apply false

    id("com.google.gms.google-services") version "4.4.0" apply false

}