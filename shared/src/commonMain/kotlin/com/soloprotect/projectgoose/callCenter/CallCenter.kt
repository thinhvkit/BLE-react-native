package com.myprotect.projectx.callCenter

import MultipleEventsCutter
import com.myprotect.projectx.domain.core.AppDataStore
import com.myprotect.projectx.common.Logger
import get
import kotlinx.datetime.Clock

abstract class CallCenter(private val appDataStore: AppDataStore) {

    companion object {
        private var lastCallCenterType: CallCenterType = CallCenterType.UNKNOWN
        private var lastRedAlertTimeCall: Long = 0
        private var redAlertRetry: Long = 0
    }

    private val multipleEventsCutter = MultipleEventsCutter.get()
    abstract fun makeCall(number: String)

    private val now: Long
        get() = Clock.System.now().toEpochMilliseconds()

    private fun makeCall(type: CallCenterType) {
        val number = when (type) {
            CallCenterType.RED_ALERT -> appDataStore.deviceSetting?.redAlertOutGoingNumber
            CallCenterType.READY_2_TALK -> appDataStore.deviceSetting?.companionAlertOutGoingNumber
            CallCenterType.CHECK_IN -> appDataStore.deviceSetting?.amberAlertOutGoingNumber
            CallCenterType.INCAPACITATION -> appDataStore.deviceSetting?.incapAlertOutGoingNumber
            else -> null
        }

        number?.let {
            makeCall(it)
        }
    }

    fun onCallEnded() {
        Logger.d("onCallEnded: $redAlertRetry")
        if(lastCallCenterType == CallCenterType.RED_ALERT && now - lastRedAlertTimeCall < 10000 && redAlertRetry < 10) {
            makeRedAlertCall()
        } else {
            lastCallCenterType = CallCenterType.UNKNOWN
            redAlertRetry = 0
        }
    }

    fun makeRedAlertCall() {
        redAlertRetry++
        lastRedAlertTimeCall = now
        lastCallCenterType = CallCenterType.RED_ALERT
        multipleEventsCutter.processEvent(
            {
                Logger.d("RedAlertCalling")
                this.makeCall(CallCenterType.RED_ALERT)
            },
            500L
        )
    }

    fun makeReady2TalkCall() {
        lastCallCenterType = CallCenterType.READY_2_TALK
        Logger.d("Ready2TalkCalling")
        this.makeCall(CallCenterType.READY_2_TALK)
    }

    fun makeCheckInCall() {
        lastCallCenterType = CallCenterType.CHECK_IN
        Logger.d("CheckInCalling")
        this.makeCall(CallCenterType.CHECK_IN)
    }

    fun makeIncapacitationCall() {
        lastCallCenterType = CallCenterType.INCAPACITATION
        Logger.d("IncapacitationCalling")
        this.makeCall(CallCenterType.INCAPACITATION)
    }
}

enum class CallCenterType {
    UNKNOWN,
    RED_ALERT,
    READY_2_TALK,
    CHECK_IN,
    INCAPACITATION
}
