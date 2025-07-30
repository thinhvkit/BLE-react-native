package com.myprotect.projectx.presentation.navigation

import androidx.navigation.NamedNavArgument

sealed class OnboardingNavigation(
    val route: String, val arguments: List<NamedNavArgument>
) {

    data object PersonalDetails : OnboardingNavigation(route = "PersonalDetails", arguments = emptyList())

    data object MedicalDetails : OnboardingNavigation(route = "MedicalDetails", arguments = emptyList())

    data object LanguageAndTimezone : OnboardingNavigation(route = "LanguageAndTimezone", arguments = emptyList())

}
