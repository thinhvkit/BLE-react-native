package com.myprotect.projectx.common

import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.os.BatteryManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

actual fun getDeviceIdentifier(): String {
    return getIMEI() ?: "Unknown"
}

fun getIMEI(): String? {
    return Build.ID
}

@Composable
actual fun getBatteryStatus(): BatteryStatus {
    return AndroidBatteryStatus(LocalContext.current)
}

class AndroidBatteryStatus(private val context: Context): BatteryStatus {
    override fun getBatteryPercentage(): Int {
        val batteryManager = context.getSystemService(BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }
}

