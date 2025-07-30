package com.myprotect.projectx.presentation.ui.navigation_drawer.view_model

import com.myprotect.projectx.domain.core.UIComponent

sealed class NavigationDrawerEvent{
    data object Logout : NavigationDrawerEvent()

    data class Error(
        val uiComponent: UIComponent
    ) : NavigationDrawerEvent()
}
