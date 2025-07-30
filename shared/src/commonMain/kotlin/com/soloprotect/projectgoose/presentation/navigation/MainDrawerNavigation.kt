package com.myprotect.projectx.presentation.navigation

import androidx.navigation.NamedNavArgument

sealed class MainDrawerNavigation (
    val route: String, val arguments: List<NamedNavArgument>
) {

    data object Main : MainDrawerNavigation(route = "Main", arguments = emptyList())

    data object RiskMessages : MainDrawerNavigation(route = "RiskMessages", arguments = emptyList())

    data object DeviceSetup : MainDrawerNavigation(route = "DeviceSetup", arguments = emptyList())

    data object Settings : MainDrawerNavigation(route = "Settings", arguments = emptyList())

    data object RiskMessageDetail : MainDrawerNavigation(route = "RiskMessageDetail?message={message}", arguments = emptyList())
}
