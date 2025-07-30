package com.myprotect.projectx.presentation.ui.login.step3.view_model

sealed class LoginPhoneEvent{
    data class VerifyPhoneNumber(val phoneNumber: String) : LoginPhoneEvent()
}
