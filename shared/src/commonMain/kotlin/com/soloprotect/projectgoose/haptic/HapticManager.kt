package com.myprotect.projectx.haptic

object HapticManager {
    fun getHaptic(): IHaptic {
        return HapticManagerImpl.getHapticManager()
    }
}
