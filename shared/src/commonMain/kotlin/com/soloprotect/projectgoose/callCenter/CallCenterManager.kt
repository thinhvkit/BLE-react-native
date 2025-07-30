package com.myprotect.projectx.callCenter

object CallCenterManager {
    fun getCallCenter(): CallCenter {
        return CallCenterManagerImpl.getCallCenter()
    }

    fun addListener(listener: Listener) {
        CallCenterManagerImpl.addListener(listener)
    }

    interface Listener {
        fun onCallStarted()
        fun onIncomingCallAcceptedNotifyObservers()
        fun onIncomingCallEndedNotifyObservers()
        fun onOutgoingCallAcceptedNotifyObservers()
        fun onOutgoingCallEndedNotifyObservers()
        fun onIncomingCallRinging()
    }
}

