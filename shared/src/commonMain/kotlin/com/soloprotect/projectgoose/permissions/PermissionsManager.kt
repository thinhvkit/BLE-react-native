package com.myprotect.projectx.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


interface PermissionCallback {
    fun onPermissionStatus(permissionType: PermissionType, status: PermissionStatus)
}

@Composable
expect fun createPermissionsManager(callback: PermissionCallback): PermissionsManager

@Composable
expect fun permissionRequests(): List<PermissionType>

@Composable
fun performHapticFeedback(times: Int) {
    val haptic = LocalHapticFeedback.current

    val hapticScope = rememberCoroutineScope()
    hapticScope.launch {
        repeat(times) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            delay(1000)
        }
    }
}
