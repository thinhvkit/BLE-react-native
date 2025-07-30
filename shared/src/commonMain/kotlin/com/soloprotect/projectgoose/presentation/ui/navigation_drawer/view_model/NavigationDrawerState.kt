package com.myprotect.projectx.presentation.ui.navigation_drawer.view_model

import com.myprotect.projectx.domain.core.NetworkState
import com.myprotect.projectx.domain.core.ProgressBarState
import com.myprotect.projectx.domain.core.Queue
import com.myprotect.projectx.domain.core.UIComponent

data class NavigationDrawerState(
    val logout: Boolean = false,
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val networkState: NetworkState = NetworkState.Good,
    val errorQueue: Queue<UIComponent> = Queue(mutableListOf()),
    val profileName: String = "",
)
