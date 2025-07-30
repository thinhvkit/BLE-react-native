package com.myprotect.projectx.common

import androidx.compose.runtime.Composable
import platform.UIKit.UIDevice

actual fun getDeviceIdentifier(): String {
    return UIDevice.currentDevice.identifierForVendor?.UUIDString ?: "Unknown"
}

@Composable
actual fun getBatteryStatus(): BatteryStatus {
    return IosBatteryStatus()
}

class IosBatteryStatus : BatteryStatus {
    override fun getBatteryPercentage(): Int {
        UIDevice.currentDevice.batteryMonitoringEnabled = true
        return (UIDevice.currentDevice.batteryLevel * 100).toInt()
    }
}
