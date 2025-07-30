package com.myprotect.projectx.presentation.navigation

import androidx.navigation.NamedNavArgument

sealed class AppNavigation(
    val route: String, val arguments: List<NamedNavArgument>
) {

    data object Splash : AppNavigation(route = "Splash", arguments = emptyList())

    data object Login : AppNavigation(route = "Login", arguments = emptyList())

    data object Permission : AppNavigation(route = "Permission", arguments = emptyList())

    data object MainNavigationHost : AppNavigation(route = "MainNavigationHost", arguments = emptyList())

    data object Onboarding : AppNavigation(route = "Onboarding", arguments = emptyList())


}
