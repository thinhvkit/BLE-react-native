import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import java.util.regex.Pattern

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinNativeCocoaPods)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqldelightApp)
    alias(libs.plugins.buildkonfig)
    kotlin("plugin.serialization") version "2.0.20-Beta1"
}

kotlin {
    task("testClasses")
    androidTarget()

    targets.all {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
            linkerOpts.add("-lsqlite3")
        }
    }

    cocoapods {
        ios.deploymentTarget = "16.2"
        version = "1.0"
        homepage = ""
        summary = "A build for myprotect iOS"
        framework {
            baseName = "shared"
            isStatic = true
            linkerOpts.add("-lsqlite3")
        }
        noPodspec()
        pod("FirebaseMessaging")
        podfile = project.file("../iosApp/Podfile")
        xcodeConfigurationToNativeBuildType["Dev"] = NativeBuildType.DEBUG
        xcodeConfigurationToNativeBuildType["Test"] = NativeBuildType.DEBUG
        xcodeConfigurationToNativeBuildType["Prod"] = NativeBuildType.DEBUG
    }

    sourceSets {

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.assertions.core)
                implementation(libs.kotest.property)
                implementation(libs.ktor.mock)
                implementation(libs.coroutines.test)
                implementation(libs.turbine.turbine)

                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.uiTest)

            }
        }
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.animation)
                implementation(compose.material3)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                api(compose.materialIconsExtended)
                implementation(libs.ktor.core)
                implementation(libs.ktor.logging)
                implementation(libs.ktor.serialization)
                implementation(libs.ktor.negotiation)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.androidx.datastore.preferences)
                implementation(libs.compose.navigation)
                implementation(libs.androidx.lifecycle.viewmodel.compose)
                implementation(libs.napier.logging)

                api(libs.koin.core)
                api(libs.koin.compose)
                api(libs.coil3)
                api(libs.coil3.network)
                api(libs.koin.compose.viewmodel)

                implementation(libs.bundles.sqldelight.common)

                implementation(project(":myprotectApi"))
                implementation(libs.pullrefresh)
            }
        }

        androidMain {
            dependencies {
                implementation(project(":minewBeacon"))
                api(libs.androidx.activity.compose)
                api(libs.androidx.appcompat)
                api(libs.androidx.core)
                implementation(libs.androidx.startup.runtime)
                implementation(libs.ktor.okhttp)
                api(libs.coil3.gif)
                api(libs.coil3.svg)
                api(libs.coil3.core)
                api(libs.coil3.video)
                implementation(libs.system.ui.controller)
                implementation(libs.accompanist.permissions)
                implementation(libs.firebase.messaging)
                implementation(libs.androidx.media3.exoplayer)
                implementation(libs.sqldelight.android.driver)
                implementation(libs.play.services.location)
            }
        }
        iosMain {
            dependencies {
                implementation(libs.ktor.darwin.ios)
                implementation(libs.sqliter)
                implementation(libs.ktor.ios)
                implementation(libs.kotlinx.serialization.core) // Core serialization
                implementation(libs.sqldelight.ios.driver)
            }
        }

    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.myprotect.projectx.db")
        }
    }

    linkSqlite.set(true)

}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.myprotect.projectx"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/composeResources")
    sourceSets["main"].res.srcDirs("src/commonMain/composeResources", "src/androidMain/res")

    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}

fun Project.getAndroidBuildVariantOrNull(): String? {
    val variants = setOf("Dev", "Test", "Prod")
    val taskRequestsStr = gradle.startParameter.taskRequests.toString()
    val pattern: Pattern = if (taskRequestsStr.contains("assemble")) {
        Pattern.compile("assemble(\\w+)(Release|Debug)")
    } else {
        Pattern.compile("bundle(\\w+)(Release|Debug)")
    }

    val matcher = pattern.matcher(taskRequestsStr)
    val variant = if (matcher.find()) matcher.group(1) else null
    return if (variant in variants) {
        variant
    } else {
        null
    }
}

private fun Project.currentBuildVariant(): String {
    val variants = setOf("Dev", "Test", "Prod")
    return getAndroidBuildVariantOrNull()
        ?: System.getenv()["VARIANT"]
            .toString()
            .takeIf { it in variants } ?: "Dev"
}

project.extra.set("buildkonfig.flavor", currentBuildVariant())

buildkonfig {
    packageName = "com.myprotect.projectx"
    objectName = "EnvConfig"
    exposeObjectWithName = "EnvConfig"

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "variant", "Dev")
        buildConfigField(FieldSpec.Type.STRING, "apiEndPoint", "https://sp-tst-uks-mobile-bff-app.azurewebsites.net")
        buildConfigField(FieldSpec.Type.STRING, "qsgLink", "")
    }

    targetConfigs {
        create("iosX64") {
            buildConfigField(FieldSpec.Type.STRING, "qsgLink", "https://myprotectprodcdn.blob.core.windows.net/webfiles/QSGs/iOS/myprotectMobileApp_iOS_%1\$s_QSG.pdf")
        }

        create("iosArm64") {
            buildConfigField(FieldSpec.Type.STRING, "qsgLink", "https://myprotectprodcdn.blob.core.windows.net/webfiles/QSGs/iOS/myprotectMobileApp_iOS_%1\$s_QSG.pdf")
        }

        create("iosSimulatorArm64") {
            buildConfigField(FieldSpec.Type.STRING, "qsgLink", "https://myprotectprodcdn.blob.core.windows.net/webfiles/QSGs/iOS/myprotectMobileApp_iOS_%1\$s_QSG.pdf")
        }

        create("android") {
            buildConfigField(FieldSpec.Type.STRING, "qsgLink", "https://myprotectprodcdn.blob.core.windows.net/webfiles/QSGs/Android/myprotectMobileApp_Android_%1\$s_QSG.pdf")
        }
    }

    defaultConfigs("Dev") {
        buildConfigField(FieldSpec.Type.STRING, "variant", "Dev")
        buildConfigField(FieldSpec.Type.STRING, "apiEndPoint", "https://sp-dev-uks-mobile-bff-app.azurewebsites.net")
    }

    defaultConfigs("Test") {
        buildConfigField(FieldSpec.Type.STRING, "variant", "Test")
        buildConfigField(FieldSpec.Type.STRING, "apiEndPoint", "https://sp-tst-uks-mobile-bff-app.azurewebsites.net")
    }

    defaultConfigs("Prod") {
        buildConfigField(FieldSpec.Type.STRING, "variant", "Prod")
        buildConfigField(FieldSpec.Type.STRING, "apiEndPoint", "https://sp-tst-uks-mobile-bff-app.azurewebsites.net")
    }
}
