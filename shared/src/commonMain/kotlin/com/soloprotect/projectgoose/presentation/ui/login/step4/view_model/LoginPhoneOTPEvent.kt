package com.myprotect.projectx.presentation.ui.login.step4.view_model

import com.myprotect.projectx.domain.core.UIComponent

sealed class LoginPhoneOTPEvent{
    data class VerifyOTP(val otp: String) : LoginPhoneOTPEvent()

    data object ResendOTP : LoginPhoneOTPEvent()

    data class Error(
        val uiComponent: UIComponent
    ) : LoginPhoneOTPEvent()
}
