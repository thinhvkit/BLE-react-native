package com.myprotect.projectx.permissions

import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatus
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusDenied
import platform.UserNotifications.UNAuthorizationStatusEphemeral
import platform.UserNotifications.UNAuthorizationStatusNotDetermined
import platform.UserNotifications.UNAuthorizationStatusProvisional
import platform.UserNotifications.UNNotificationSettings
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.suspendCoroutine

internal class RemoteNotificationPermissionDelegate : PermissionDelegate {
    override suspend fun providePermission() {
        val currentCenter: UNUserNotificationCenter = UNUserNotificationCenter
            .currentNotificationCenter()

        val status: UNAuthorizationStatus = suspendCoroutine { continuation ->
            currentCenter.getNotificationSettingsWithCompletionHandler { settings: UNNotificationSettings? ->
                continuation.resumeWith(
                    Result.success(
                        settings?.authorizationStatus ?: UNAuthorizationStatusNotDetermined
                    )
                )
            }
        }
        when (status) {
            UNAuthorizationStatusAuthorized -> return
            UNAuthorizationStatusNotDetermined -> {
                val isSuccess = suspendCoroutine { continuation ->
                    UNUserNotificationCenter.currentNotificationCenter()
                        .requestAuthorizationWithOptions(
                            UNAuthorizationOptionSound
                                .or(UNAuthorizationOptionAlert)
                                .or(UNAuthorizationOptionBadge)
                        ) { isOk, error ->
                            if (isOk && error == null) {
                                continuation.resumeWith(Result.success(true))
                            } else {
                                continuation.resumeWith(Result.success(false))
                            }
                        }
                }
                if (isSuccess) {
                    providePermission()
                } else {
                    error("notifications permission failed")
                }
            }

            UNAuthorizationStatusDenied ->  error("notifications permission is denied")
            else -> error("notifications permission status $status")
        }
    }

    override suspend fun getPermissionState(): PermissionStatus {
        val currentCenter = UNUserNotificationCenter.currentNotificationCenter()

        val status = suspendCoroutine { continuation ->
            currentCenter.getNotificationSettingsWithCompletionHandler { settings: UNNotificationSettings? ->
                continuation.resumeWith(
                    Result.success(
                        settings?.authorizationStatus ?: UNAuthorizationStatusNotDetermined
                    )
                )
            }
        }
        return when (status) {
            UNAuthorizationStatusAuthorized,
            UNAuthorizationStatusProvisional,
            UNAuthorizationStatusEphemeral -> PermissionStatus.GRANTED
            UNAuthorizationStatusNotDetermined -> PermissionStatus.NOT_DETERMINED
            UNAuthorizationStatusDenied -> PermissionStatus.DENIED
            else -> error("unknown push authorization status $status")
        }
    }
}
