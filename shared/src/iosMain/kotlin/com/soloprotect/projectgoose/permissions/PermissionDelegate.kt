package com.myprotect.projectx.permissions

internal interface PermissionDelegate {
    suspend fun providePermission()
    suspend fun getPermissionState(): PermissionStatus
}
