package com.myprotect.projectx.haptic

import com.myprotect.projectx.di.KMPKoinComponent
import com.myprotect.projectx.di.LibDependencyInitializer
import org.koin.core.component.get

internal object HapticManagerImpl: KMPKoinComponent() {
    fun getHapticManager(): IHaptic {
        requireInitialization()
        return get()
    }

    private fun requireInitialization() {
        if (LibDependencyInitializer.isInitialized().not()) throw IllegalStateException(
            "HapticFactory is not initialized. " +
                    "Please, initialize HapticFactory by calling #initialize method"
        )
    }
}
