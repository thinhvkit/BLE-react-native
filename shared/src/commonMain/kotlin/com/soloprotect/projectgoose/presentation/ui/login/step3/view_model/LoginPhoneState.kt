package com.myprotect.projectx.presentation.ui.login.step3.view_model

import com.myprotect.projectx.domain.core.ProgressBarState
import com.myprotect.projectx.domain.core.UIMessage

data class LoginPhoneState(
    val isNavigateNext: Boolean = false,
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val message: UIMessage? = null,
)
