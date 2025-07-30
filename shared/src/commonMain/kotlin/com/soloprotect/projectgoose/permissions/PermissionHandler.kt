package com.myprotect.projectx.permissions

import androidx.compose.runtime.Composable

interface PermissionsManager {
    @Composable
    fun askPermission(permission: PermissionType)

    @Composable
    fun isPermissionGranted(permission: PermissionType): Boolean

    @Composable
    fun LaunchSettings()

    @Composable
    fun LaunchLocationSettingsIfNeed()

}
