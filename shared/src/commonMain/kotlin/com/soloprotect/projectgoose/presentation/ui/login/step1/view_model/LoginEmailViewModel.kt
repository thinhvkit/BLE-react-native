package com.myprotect.projectx.presentation.ui.login.step1.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myprotect.projectx.domain.core.ProgressBarState
import com.myprotect.projectx.common.CommonUtil
import com.myprotect.projectx.common.LocaleKeys
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginEmailViewModel(
) : ViewModel() {
    val state: MutableState<LoginEmailState> = mutableStateOf(LoginEmailState())


    fun onTriggerEvent(event: LoginEmailEvent) {
        when (event) {

            is LoginEmailEvent.CheckEmail -> {
                checkEmail(event.email);
            }

        }
    }

    private fun checkEmail(email: String) {
        state.value = LoginEmailState(progressBarState = ProgressBarState.ButtonLoading)
        CommonUtil.isValidEmail(email).let {
            if (it) {
                // Fake api call
                viewModelScope.launch {
                    delay(2000)
                    state.value = LoginEmailState(isNavigateNext = true)
                }
            } else {
                state.value = LoginEmailState(error = LocaleKeys.EMAIL_INVALID_VALIDATION)
                viewModelScope.launch {
                    delay(4000)
                    state.value = state.value.copy(error = null)
                }
            }
        }
    }
}
