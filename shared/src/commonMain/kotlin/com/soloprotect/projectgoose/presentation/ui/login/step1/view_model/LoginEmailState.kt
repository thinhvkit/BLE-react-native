package com.myprotect.projectx.presentation.ui.login.step1.view_model

import com.myprotect.projectx.domain.core.ProgressBarState

data class LoginEmailState(
    val isNavigateNext: Boolean = false,
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val error: String? = null
)
