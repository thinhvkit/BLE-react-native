package com.myprotect.projectx.presentation.ui.login.step3.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myprotect.projectx.domain.core.ProgressBarState
import com.myprotect.projectx.domain.core.UIMessage
import com.myprotect.projectx.common.CommonUtil
import com.myprotect.projectx.common.LocaleKeys
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginPhoneViewModel(
) : ViewModel() {

    val state: MutableState<LoginPhoneState> = mutableStateOf(LoginPhoneState())

    fun onTriggerEvent(event: LoginPhoneEvent) {
        when (event) {

            is LoginPhoneEvent.VerifyPhoneNumber -> {
                verifyPhone(event.phoneNumber)
            }
        }
    }

    private fun verifyPhone(otp: String) {
        state.value = LoginPhoneState(progressBarState = ProgressBarState.ButtonLoading)
        CommonUtil.checkPhoneFormat(otp).let {
            if (it) {
                // Fake api call
                viewModelScope.launch {
                    delay(2000)
                    state.value = state.value.copy(isNavigateNext = true, progressBarState = ProgressBarState.Idle)
                }
            } else {
                state.value =
                    state.value.copy(message = UIMessage.ErrorMessage(LocaleKeys.PHONE_INVALID), progressBarState = ProgressBarState.Idle)
                viewModelScope.launch {
                    delay(4000)
                    state.value = state.value.copy(message = null, progressBarState = ProgressBarState.Idle)
                }
            }
        }
    }
}
