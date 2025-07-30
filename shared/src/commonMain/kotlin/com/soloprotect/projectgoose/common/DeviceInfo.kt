package com.myprotect.projectx.common

import androidx.compose.runtime.Composable

expect fun getDeviceIdentifier(): String

@Composable
expect fun getBatteryStatus(): BatteryStatus

interface BatteryStatus {
    fun getBatteryPercentage(): Int
}
