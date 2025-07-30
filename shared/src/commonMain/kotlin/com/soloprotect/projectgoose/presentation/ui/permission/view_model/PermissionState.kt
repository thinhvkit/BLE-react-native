package com.myprotect.projectx.presentation.ui.permission.view_model

import com.myprotect.projectx.domain.core.ProgressBarState
import com.myprotect.projectx.domain.core.UIComponentState

data class PermissionState(
    val permissionDialog: UIComponentState = UIComponentState.Hide,
    val cancelDialog: UIComponentState = UIComponentState.Hide,
    val isTokenValid: Boolean = false,
    val navigateToMain: Boolean = false,

    val progressBarState: ProgressBarState = ProgressBarState.Idle,
)
