package com.myprotect.projectx.incapacitation

import androidx.compose.runtime.MutableState
import com.myprotect.projectx.domain.core.UIComponentState

object IncapacitationManager {

    fun getTimer(): Timer {
        return IncapacitationManagerImpl.getTimer()
    }

    fun addListener(listener: OnIncapacitationBase) {
        IncapacitationManagerImpl.addListener(listener)
    }

    fun removeListener(listener: OnIncapacitationBase) {
        IncapacitationManagerImpl.removeListener(listener)
    }
}

abstract class Timer(private val timerManager: TimerManager) {

    abstract fun initialize()
    abstract fun start(isSensor: Boolean = true)
    abstract fun stop()
    abstract fun onFiveMinLeft()
    abstract fun onOneMinLeft()
    abstract fun onTimerCompleted()
    abstract fun onPreAlertCompleted()
    abstract fun increase()
    abstract fun decrease()

    fun timerState(): MutableState<TimerState> {
        return timerManager.state
    }

    fun preAlertTimerState(): MutableState<TimerState> {
        return timerManager.preAlertState
    }

    fun handlePreAlert(value: UIComponentState) {
        timerManager.handlePreAlert(value)
    }
}

interface OnIncapacitationBase

interface OnIncapacitation: OnTimerRequest, OnTimerResponse

interface OnTimerRequest: OnIncapacitationBase {
    fun onStart(timestamp: Long)
    fun onStop()
    fun onUpdated(timestamp: String)
    fun onIncrease(timestamp: Long)
    fun onDecrease(timestamp: Long)
}

interface OnTimerResponse: OnIncapacitationBase {
    fun onFall()
    fun onFiveMinLeft()
    fun onOneMinLeft()
    fun onTimerCompleted()
    fun onPreAlertCompleted()
}
