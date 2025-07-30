package com.myprotect.projectx.presentation.navigation

import androidx.navigation.NamedNavArgument

sealed class LoginNavigation(
    val route: String, val arguments: List<NamedNavArgument>
) {

    data object Step1 : LoginNavigation(route = "Step1", arguments = emptyList())

    data object Step2 : LoginNavigation(route = "Step2", arguments = emptyList())

    data object Step3 : LoginNavigation(route = "Step3", arguments = emptyList())

    data object Step4 : LoginNavigation(route = "Step4", arguments = emptyList())

}

