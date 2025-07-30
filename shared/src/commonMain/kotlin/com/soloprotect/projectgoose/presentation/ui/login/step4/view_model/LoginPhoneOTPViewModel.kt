package com.myprotect.projectx.presentation.ui.login.step4.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myprotect.projectx.domain.core.DataState
import com.myprotect.projectx.domain.core.ProgressBarState
import com.myprotect.projectx.domain.core.UIMessage
import com.myprotect.projectx.domain.interactors.authentication.LoginInteractor
import com.myprotect.projectx.common.CommonUtil
import com.myprotect.projectx.common.LocaleKeys
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LoginPhoneOTPViewModel(
    private val loginInteractor : LoginInteractor
) : ViewModel() {

    companion object {
        const val OTP_TIME = 5 * 60
    }

    val state: MutableState<LoginPhoneOTPState> = mutableStateOf(LoginPhoneOTPState())
    private var _timer = OTP_TIME

    private var timerJob: Job? = null

    init {
        startTimer()
    }

    fun onTriggerEvent(event: LoginPhoneOTPEvent) {
        when (event) {
            is LoginPhoneOTPEvent.VerifyOTP -> {
                verifyOTP(event.otp);
            }
            LoginPhoneOTPEvent.ResendOTP -> reSendOTP()
            is LoginPhoneOTPEvent.Error -> {}
        }
    }

    private fun reSendOTP() {

        stopTimer()
        state.value = state.value.copy(countDown = null)

        // Fake api call
        viewModelScope.launch {
            delay(1000)
            state.value =
                state.value.copy(message = UIMessage.InfoMessage(LocaleKeys.SMS_OTP_RESENT_INFO))
            launch {
                delay(4000)
                state.value =
                    state.value.copy(message = null)
            }
            startTimer()
        }
    }

    private fun verifyOTP(otp: String) {
        state.value = LoginPhoneOTPState(progressBarState = ProgressBarState.ButtonLoading)
        CommonUtil.checkOTPFormat(otp).let {
            if (it) {
                // Fake api call
                viewModelScope.launch {
                    delay(2000)
                    loginInteractor.execute(
                        phoneOTP = otp,
                    ).onEach { dataState ->
                        when (dataState) {
                            is DataState.NetworkStatus -> {}
                            is DataState.Response -> {
                                onTriggerEvent(LoginPhoneOTPEvent.Error(dataState.uiComponent))
                            }

                            is DataState.Data -> {
                                state.value =
                                    state.value.copy(isNavigateNext = !dataState.data.isNullOrEmpty())
                            }

                            is DataState.Loading -> {
                                state.value =
                                    state.value.copy(progressBarState = dataState.progressBarState)
                            }
                        }
                    }.launchIn(viewModelScope)
                }
            } else {
                state.value =
                    state.value.copy(
                        message = UIMessage.ErrorMessage(LocaleKeys.SMS_EMAIL_OTP_EXPIRED),
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
