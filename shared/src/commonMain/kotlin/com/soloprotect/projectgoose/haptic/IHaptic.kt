package com.myprotect.projectx.haptic

interface IHaptic {

    fun playHaptic(times: Int, type: HapticType = HapticType.SHORT)

    fun playSound(pathSource: String)
}

enum class HapticType {
    SHORT,
    LONG
}
