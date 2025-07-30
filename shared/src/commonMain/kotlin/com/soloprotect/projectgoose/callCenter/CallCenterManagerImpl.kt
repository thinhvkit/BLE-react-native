package com.myprotect.projectx.callCenter

import com.myprotect.projectx.di.KMPKoinComponent
import com.myprotect.projectx.di.LibDependencyInitializer
import org.koin.core.component.get

internal object CallCenterManagerImpl : KMPKoinComponent() {

    private val listeners = mutableListOf<CallCenterManager.Listener>()
    fun getCallCenter(): CallCenter {
        requireInitialization()
        return get()
    }

    fun addListener(listener: CallCenterManager.Listener) {
        if(listeners.contains(listener).not()) {
            listeners.add(listener)
        }
    }

    fun onCallStarted() {
        listeners.forEach { it.onCallStarted() }
    }

    fun onIncomingCallRinging() {
        listeners.forEach { it.onIncomingCallRinging() }
    }

    fun onIncomingCallAccepted() {
        listeners.forEach { it.onIncomingCallAcceptedNotifyObservers() }
    }

    fun onIncomingCallEnded() {
        listeners.forEach { it.onIncomingCallEndedNotifyObservers() }
    }

    fun onOutgoingCallAccepted() {
        listeners.forEach { it.onOutgoingCallAcceptedNotifyObservers() }
    }

    fun onOutgoingCallEnded() {
        listeners.forEach { it.onOutgoingCallEndedNotifyObservers() }
    }

    private fun requireInitialization() {
        if (LibDependencyInitializer.isInitialized().not()) throw IllegalStateException(
            "CallCenterFactory is not initialized. " +
                    "Please, initialize CallCenterFactory by calling #initialize method"
        )
    }
}
