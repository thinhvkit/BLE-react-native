package com.myprotect.projectx.notifications

interface PermissionUtil {

    fun hasNotificationPermission(onPermissionResult: (Boolean) -> Unit = {})
    fun askNotificationPermission(onPermissionGranted: () -> Unit = {})

    fun hasLocationPermission(onPermissionResult: (Boolean) -> Unit = {})
    fun askLocationPermission(onPermissionGranted: () -> Unit = {})
}
