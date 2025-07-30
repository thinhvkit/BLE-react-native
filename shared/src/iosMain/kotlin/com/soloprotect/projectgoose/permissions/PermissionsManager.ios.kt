package com.myprotect.projectx.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocationManager
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import kotlin.experimental.ExperimentalNativeApi


@Composable
actual fun createPermissionsManager(callback: PermissionCallback): PermissionsManager {
    return IOSPermissionsManager(callback)
}

class IOSPermissionsManager(private val callback: PermissionCallback) :
    PermissionsManager {

    private val locationManagerDelegate = LocationManagerDelegate()
    private val remoteNotificationPermissionDelegate = RemoteNotificationPermissionDelegate()
    private val bluetoothPermissionDelegate = BluetoothPermissionDelegate()
    private val locationPermissionDelegate = LocationPermissionDelegate(locationManagerDelegate)

    @Composable
    override fun askPermission(permission: PermissionType) {
        val lifecycleOwner = LocalLifecycleOwner.current
        when (permission) {
            PermissionType.LOCATION -> {
                val status: CLAuthorizationStatus =
                    remember { CLLocationManager.authorizationStatus() }
                LaunchedEffect(status) {
                    lifecycleOwner.lifecycleScope.launch {
                        try {
                            locationPermissionDelegate.providePermission()
                            callback.onPermissionStatus(permission, PermissionStatus.GRANTED)
                        } catch (e: Exception) {
                            callback.onPermissionStatus(permission, PermissionStatus.DENIED)
                        }
                    }
                }
            }

            PermissionType.NOTIFICATION -> {
                lifecycleOwner.lifecycleScope.launch {
                    try {
                        remoteNotificationPermissionDelegate.providePermission()
                        callback.onPermissionStatus(permission, PermissionStatus.GRANTED)
                    } catch (e: Exception) {
                        callback.onPermissionStatus(permission, PermissionStatus.DENIED)
                    }

                }
            }

            //Bluetooth does not work within iOS simulator
            PermissionType.BLUETOOTH -> {
                lifecycleOwner.lifecycleScope.launch {
                    bluetoothPermissionDelegate.providePermission()
                }
            }

            else -> {

            }
        }
    }

    @Composable
    override fun isPermissionGranted(permission: PermissionType): Boolean {
        return when (permission) {
            PermissionType.LOCATION -> {
                var status: PermissionStatus?
                runBlocking {
                    status = locationPermissionDelegate.getPermissionState()
                }
                status == PermissionStatus.GRANTED
            }

            PermissionType.NOTIFICATION -> {
                var status: PermissionStatus?
                runBlocking {
                    status = remoteNotificationPermissionDelegate.getPermissionState()
                }

                status == PermissionStatus.GRANTED
            }

            PermissionType.BLUETOOTH -> {
                var status: PermissionStatus?
                runBlocking {
                    status = bluetoothPermissionDelegate.getPermissionState()
                }

                isSimulator() || status == PermissionStatus.GRANTED
            }

            else -> {
                true
            }
        }
    }

    @Composable
    override fun LaunchSettings() {
        NSURL.URLWithString(UIApplicationOpenSettingsURLString)?.let {
            UIApplication.sharedApplication.openURL(it)
        }
    }

    @Composable
    override fun LaunchLocationSettingsIfNeed() {
    }
}

@Composable
actual fun permissionRequests(): List<PermissionType> {
    return listOf(
        PermissionType.LOCATION,
        PermissionType.NOTIFICATION,
        PermissionType.BLUETOOTH
    )
}

@OptIn(ExperimentalNativeApi::class)
fun isSimulator(): Boolean {
    return when (Platform.cpuArchitecture) {
        CpuArchitecture.ARM32, CpuArchitecture.X86 -> true
        else -> false
    }
}
