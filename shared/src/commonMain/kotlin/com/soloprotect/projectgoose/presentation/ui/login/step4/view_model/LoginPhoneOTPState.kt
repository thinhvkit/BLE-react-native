package com.myprotect.projectx.presentation.ui.login.step4.view_model

import com.myprotect.projectx.domain.core.ProgressBarState
import com.myprotect.projectx.domain.core.UIMessage

data class LoginPhoneOTPState(
    val isNavigateNext: Boolean = false,
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val message: UIMessage? = null,
    val countDown: String? = null
)
