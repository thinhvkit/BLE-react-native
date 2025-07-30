package com.myprotect.projectx.presentation.ui.login.step2.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myprotect.projectx.domain.core.ProgressBarState
import com.myprotect.projectx.domain.core.UIMessage
import com.myprotect.projectx.common.CommonUtil
import com.myprotect.projectx.common.LocaleKeys
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginEmailOTPViewModel(
) : ViewModel() {

    companion object {
        const val OTP_TIME = 15 * 60
    }

    val state: MutableState<LoginEmailOTPState> = mutableStateOf(LoginEmailOTPState())
    private var _timer = OTP_TIME

    private var timerJob: Job? = null

    init {
        startTimer()
    }

    fun onTriggerEvent(event: LoginEmailOTPEvent) {
        when (event) {

            is LoginEmailOTPEvent.VerifyOTP -> {
                verifyOTP(event.otp);
            }

            LoginEmailOTPEvent.ResendOTP -> reSendOTP()
        }
    }

    private fun reSendOTP() {

        stopTimer()
        state.value = state.value.copy(countDown = null)

        // Fake api call
        viewModelScope.launch {
            delay(1000)
            state.value =
                state.value.copy(message = UIMessage.InfoMessage(LocaleKeys.EMAIL_OTP_RESENT_INFO))
            launch {
                delay(4000)
                state.value =
                    state.value.copy(message = null)
            }
            startTimer()
        }
    }

    private fun verifyOTP(otp: String) {
        state.value = LoginEmailOTPState(progressBarState = ProgressBarState.ButtonLoading)
        CommonUtil.checkOTPFormat(otp).let {
            if (it) {
                // Fake api call
                viewModelScope.launch {
                    delay(2000)
                    state.value = state.value.copy(
                        isNavigateNext = true,
                        progressBarState = ProgressBarState.Idle
                    )
                }
            } else {
                state.value =
                    state.value.copy(
                        message = UIMessage.ErrorMessage(LocaleKeys.EMAIL_OTP_INVALID),
                        progressBarState = ProgressBarState.Idle
                    )
                viewModelScope.launch {
                    delay(4000)
                    state.value =
                        state.value.copy(message = null, progressBarState = ProgressBarState.Idle)
                }
            }
        }
    }

    private fun startTimer() {
        stopTimer()
        _timer = OTP_TIME
        timerJob = viewModelScope.launch {
            while (_timer > 0) {
                delay(1000)
                _timer--
                state.value =
                    state.value.copy(countDown = "${format2Digit(_timer / 60)}:${format2Digit(_timer % 60)}")
            }
        }
    }

    private fun format2Digit(value: Int): String {
        return "0$value".takeLast(2)
    }

    fun stopTimer() {
        _timer = 0
        timerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
