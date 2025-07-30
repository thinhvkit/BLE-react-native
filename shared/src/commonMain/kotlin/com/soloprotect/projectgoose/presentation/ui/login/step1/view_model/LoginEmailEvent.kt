package com.myprotect.projectx.presentation.ui.login.step1.view_model

sealed class LoginEmailEvent{
    data class CheckEmail(val email: String) : LoginEmailEvent()
}
