package com.myprotect.projectx.presentation.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.myprotect.projectx.common.closeApp
import com.myprotect.projectx.presentation.ui.device_setup.view_model.DeviceSetupViewModel
import com.myprotect.projectx.presentation.ui.login.LoginNav
import com.myprotect.projectx.presentation.ui.navigation_drawer.NavDrawer
import com.myprotect.projectx.presentation.ui.navigation_drawer.view_model.NavigationDrawerViewModel
import com.myprotect.projectx.presentation.ui.onboarding.OnboardingNav
import com.myprotect.projectx.presentation.ui.permission.PermissionScreen
import com.myprotect.projectx.presentation.ui.permission.view_model.PermissionViewModel
import com.myprotect.projectx.presentation.ui.splash.SplashScreen
import org.koin.compose.koinInject

@Composable
fun AppNavHost(
    modifier: Modifier,
    navController: NavHostController,
    startDestination: String = AppNavigation.Splash.route
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = AppNavigation.Splash.route) {

            SplashScreen(
                navigateToPermission = {
                    navController.popBackStack()
                    navController.navigate(AppNavigation.Permission.route)
                })
        }
        composable(route = AppNavigation.Login.route) {
            LoginNav(navigateToOnboarding = {
                navController.navigate(AppNavigation.Onboarding.route)
            })
        }
        composable(route = AppNavigation.Permission.route) {
            val viewModel: PermissionViewModel = koinInject()
            PermissionScreen(
                state = viewModel.state.value,
                events = viewModel::onTriggerEvent,
                navigateToLogin = {
                    navController.popBackStack()
                    navController.navigate(AppNavigation.Login.route)
                },
                navigateToMain = {
                    navController.popBackStack()
                    navController.navigate(AppNavigation.MainNavigationHost.route)
                },
                exit = {
                    closeApp()
                }
            )
        }
        composable(route = AppNavigation.MainNavigationHost.route) {
            val viewModel: NavigationDrawerViewModel = koinInject()
            val deviceSetupViewModel: DeviceSetupViewModel = koinInject()
            NavDrawer(
                state = viewModel.state.value,
                events = viewModel::onTriggerEvent,
                deviceState = deviceSetupViewModel.deviceState,
                navigateToLogin = {
                    navController.popBackStack()
                    navController.navigate(AppNavigation.Login.route)
                }
            )
        }
        composable(route = AppNavigation.Onboarding.route, enterTransition = {
            scaleIn(initialScale = 0.5f) + fadeIn()
        }, exitTransition = { scaleOut(targetScale = 0.5f) + fadeOut() }) {
            OnboardingNav(
                navigateToMain = {
                    navController.navigate(
                        AppNavigation.MainNavigationHost.route,
                        navOptions = NavOptions.Builder().setLaunchSingleTop(true)
                            .setPopUpTo(AppNavigation.Login.route, true).build()
                    )
                },
                popToLogin = {
                    navController.popBackStack()
                }
            )
        }
    }
}
