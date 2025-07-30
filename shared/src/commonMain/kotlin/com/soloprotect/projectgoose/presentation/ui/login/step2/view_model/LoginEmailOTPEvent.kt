package com.myprotect.projectx.presentation.ui.login.step2.view_model

sealed class LoginEmailOTPEvent{
    data class VerifyOTP(val otp: String) : LoginEmailOTPEvent()

    data object ResendOTP : LoginEmailOTPEvent()
}
