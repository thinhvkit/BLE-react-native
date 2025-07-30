package com.myprotect.projectx.incapacitation

import com.myprotect.projectx.common.Logger
import com.myprotect.projectx.di.KMPKoinComponent
import com.myprotect.projectx.di.LibDependencyInitializer

import org.koin.core.component.get

internal object IncapacitationManagerImpl : KMPKoinComponent(), OnIncapacitation {

    private val listeners = mutableListOf<OnIncapacitationBase>()

    fun getTimer(): Timer {
        requireInitialization()
        return get()
    }

    fun addListener(listener: OnIncapacitationBase) {
        listeners.add(listener)
    }

    fun removeListener(listener: OnIncapacitationBase) {
        listeners.remove(listener)
    }

    private fun requireInitialization() {
        if (LibDependencyInitializer.isInitialized().not()) throw IllegalStateException(
            "IncapacitationFactory is not initialized. " +
                    "Please, initialize IncapacitationFactory by calling #initialize method"
        )
    }

    override fun onStart(timestamp: Long) {
        listeners.forEach {
            if (it is OnTimerRequest)
                it.onStart(timestamp)
        }
    }

    override fun onStop() {
        listeners.forEach {
            if (it is OnTimerRequest)
                it.onStop()
        }
    }

    override fun onUpdated(timestamp: String) {
        listeners.forEach {
            if (it is OnTimerRequest)
                it.onUpdated(timestamp)
        }
    }

    override fun onIncrease(timestamp: Long) {
        listeners.forEach {
            if (it is OnTimerRequest)
                it.onIncrease(timestamp)
        }
    }

    override fun onDecrease(timestamp: Long) {
        listeners.forEach {
            if (it is OnTimerRequest)
                it.onDecrease(timestamp)
        }
    }

    override fun onFall() {
        listeners.forEach {
            if (it is OnTimerResponse)
                it.onFall()
        }
    }

    override fun onFiveMinLeft() {
        listeners.forEach {
            if (it is OnTimerResponse)
                it.onFiveMinLeft()
        }
    }

    override fun onOneMinLeft() {
        listeners.forEach {
            if (it is OnTimerResponse)
                it.onOneMinLeft()
        }
    }

    override fun onTimerCompleted() {
        listeners.forEach {
            if (it is OnTimerResponse)
                it.onTimerCompleted()
        }
    }

    override fun onPreAlertCompleted() {
        listeners.forEach {
            if (it is OnTimerResponse)
                it.onPreAlertCompleted()
        }
    }
}
