package com.myprotect.projectx.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import coil3.fetch.NetworkFetcher
import com.myprotect.projectx.common.Context
import com.myprotect.projectx.common.onApplicationStartPlatformSpecific
import com.myprotect.projectx.di.appModule
import com.myprotect.projectx.domain.core.LocaleManager
import com.myprotect.projectx.presentation.navigation.AppNavHost
import com.myprotect.projectx.presentation.navigation.AppNavigation
import com.myprotect.projectx.presentation.theme.AppTheme
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.dsl.koinApplication

@OptIn(ExperimentalCoilApi::class)
@Composable
internal fun App(context: Context) {

    KoinContext(context = koinApplication { modules(appModule(context)) }.koin) {

        AppInitializer.onApplicationStart()

        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context).components {
                add(NetworkFetcher.Factory())
            }.build()
        }

        val localeManager = koinInject<LocaleManager>()

        val languageCode = rememberSaveable {
            localeManager.languageCode
        }

        languageCode.value

        AppTheme {
            val navigator = rememberNavController()
            val viewModel: SharedViewModel = koinInject()

            LaunchedEffect(key1 = viewModel.tokenManager.state.value.isTokenAvailable) {
                if (!viewModel.tokenManager.state.value.isTokenAvailable) {
                    navigator.popBackStack()
                    navigator.navigate(AppNavigation.Splash.route)
                }
            }

            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black)
                    .windowInsetsPadding(WindowInsets.safeDrawing).imePadding()
            ) {
                AppNavHost(
                    navController = navigator,
                    startDestination = AppNavigation.Splash.route,
                    modifier = Modifier.fillMaxSize().imePadding()
                )
            }
        }
    }
}


object AppInitializer {
    @Composable
    fun onApplicationStart() {
        onApplicationStartPlatformSpecific()
    }
}





