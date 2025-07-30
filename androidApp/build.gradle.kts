import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    id("com.google.gms.google-services")
}
kotlin {
    androidTarget()
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant {
            sourceSetTree.set(KotlinSourceSetTree.test)

            dependencies {
                //   implementation("androidx.compose.ui:ui-test-junit4-android:1.6.4")
                //    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.4")
            }
        }
    }
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation("androidx.compose.material:material-ripple:1.7.0-alpha05")
            }
        }
    }
}
android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.myprotect.projectx"
    defaultConfig {
        applicationId = "com.myprotect.projectx"
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }

    flavorDimensions.add("variant")

    productFlavors {
        create("Dev") {
            dimension = "variant"
            applicationIdSuffix = ".development"
        }

        create("Test") {
            dimension = "variant"
            applicationIdSuffix = ".development"
        }

        create("Prod") {
            dimension = "variant"
        }
    }
}

