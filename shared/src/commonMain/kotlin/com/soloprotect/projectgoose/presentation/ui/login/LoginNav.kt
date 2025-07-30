package com.myprotect.projectx.presentation.ui.login

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.myprotect.projectx.common.ChangeStatusBarColors
import com.myprotect.projectx.common.PlatformSupported
import com.myprotect.projectx.common.getPlatform
import com.myprotect.projectx.presentation.navigation.LoginNavigation
import com.myprotect.projectx.presentation.ui.login.step1.LoginEmailScreen
import com.myprotect.projectx.presentation.ui.login.step1.view_model.LoginEmailViewModel
import com.myprotect.projectx.presentation.ui.login.step2.LoginEmailOTPScreen
import com.myprotect.projectx.presentation.ui.login.step2.view_model.LoginEmailOTPViewModel
import com.myprotect.projectx.presentation.ui.login.step3.LoginPhoneScreen
import com.myprotect.projectx.presentation.ui.login.step3.view_model.LoginPhoneViewModel
import com.myprotect.projectx.presentation.ui.login.step4.LoginPhoneOTPScreen
import com.myprotect.projectx.presentation.ui.login.step4.view_model.LoginPhoneOTPViewModel
import org.koin.compose.koinInject

@Composable
internal fun LoginNav(navigateToOnboarding: () -> Unit) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val localFocusManager = LocalFocusManager.current
    if (getPlatform().name == PlatformSupported.ANDROID) {
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    localFocusManager.clearFocus()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }

    val navigator = rememberNavController()
    ChangeStatusBarColors(MaterialTheme.colorScheme.surface)
    NavHost(
        startDestination = LoginNavigation.Step1.route,
        navController = navigator,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(route = LoginNavigation.Step1.route) {
            val viewModel: LoginEmailViewModel = koinInject()
            LoginEmailScreen(
                state = viewModel.state.value,
                events = viewModel::onTriggerEvent,
                navigateToNext = {
                    navigator.navigate(LoginNavigation.Step2.route)
                },
            )
        }
        composable(route = LoginNavigation.Step2.route, enterTransition = {
            scaleIn(initialScale = 0.5f) + fadeIn()
        }, exitTransition = { scaleOut(targetScale = 0.5f) + fadeOut() }) {
            val viewModel: LoginEmailOTPViewModel = koinInject()
            LoginEmailOTPScreen(
                state = viewModel.state.value,
                events = viewModel::onTriggerEvent,
                navigateToNext = {
                    navigator.navigate(LoginNavigation.Step3.route)
                },
                back = {
                    navigator.popBackStack()
                }
            )
        }
        composable(route = LoginNavigation.Step3.route, enterTransition = {
            scaleIn(initialScale = 0.5f) + fadeIn()
        }, exitTransition = { scaleOut(targetScale = 0.5f) + fadeOut() }) {
            val viewModel: LoginPhoneViewModel = koinInject()
            LoginPhoneScreen(
                state = viewModel.state.value,
                events = viewModel::onTriggerEvent,
                navigateToNext = {
                    navigator.navigate(LoginNavigation.Step4.route)
                },
                back = {
                    navigator.popBackStack()
                }
            )
        }
        composable(route = LoginNavigation.Step4.route, enterTransition = {
            scaleIn(initialScale = 0.5f) + fadeIn()
        }, exitTransition = { scaleOut(targetScale = 0.5f) + fadeOut() }) {
            val viewModel: LoginPhoneOTPViewModel = koinInject()
            LoginPhoneOTPScreen(
                state = viewModel.state.value,
                events = viewModel::onTriggerEvent,
                navigateToNext = {
                    navigator.popBackStack(LoginNavigation.Step1.route, false)
                    navigateToOnboarding()
                },
                back = {
                    navigator.popBackStack()
                }
            )
        }
    }

}
