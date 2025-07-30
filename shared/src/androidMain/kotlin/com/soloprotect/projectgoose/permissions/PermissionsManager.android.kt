package com.myprotect.projectx.permissions

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.myprotect.projectx.common.Context
import kotlinx.coroutines.launch

fun isRunningOnEmulator(): Boolean {
    return Build.PRODUCT.startsWith("sdk")
}

@Composable
actual fun createPermissionsManager(callback: PermissionCallback): PermissionsManager {
    return remember { AndroidPermissionsManager(callback) }
}

class AndroidPermissionsManager(private val callback: PermissionCallback) :
    PermissionsManager {

    @SuppressLint("CoroutineCreationDuringComposition")
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    override fun askPermission(permission: PermissionType) {
        val lifecycleOwner = LocalLifecycleOwner.current

        val permissions = when (permission) {
            PermissionType.PHONE -> phoneCallPermissions()
            PermissionType.LOCATION -> backgroundLocationCompat()
            PermissionType.NOTIFICATION -> remoteNotificationsPermissions()
            PermissionType.BLUETOOTH -> allBluetoothPermissions()
            PermissionType.APPEAR_ON_TOP -> {
                LaunchAppearOnTop()
                emptyList()
            }

            PermissionType.BATTERY -> {
                LaunchBatteryOptimization()
                emptyList()
            }

            else -> emptyList()
        }

        if (permissions.isEmpty()) {
            return
        }


        val permissionsState = rememberMultiplePermissionsState(permissions)

        LaunchedEffect(permissionsState) {

            if (!permissionsState.allPermissionsGranted) {
                if (permissionsState.shouldShowRationale) {
                    callback.onPermissionStatus(
                        permission, PermissionStatus.SHOW_RATIONAL
                    )
                } else {
                    lifecycleOwner.lifecycleScope.launch {
                        permissionsState.launchMultiplePermissionRequest()
                    }
                }
            } else {
                callback.onPermissionStatus(
                    permission, PermissionStatus.GRANTED
                )
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    override fun isPermissionGranted(permission: PermissionType): Boolean {

        return when (permission) {
            PermissionType.PHONE -> {
                val permissions = phoneCallPermissions()
                val permissionsState = rememberMultiplePermissionsState(permissions)

                permissionsState.allPermissionsGranted
            }

            PermissionType.LOCATION -> {
                val permissions = backgroundLocationCompat()
                val permissionsState = rememberMultiplePermissionsState(permissions)
                permissionsState.allPermissionsGranted
            }

            PermissionType.NOTIFICATION -> {
                val permissions = remoteNotificationsPermissions()
                val permissionsState = rememberMultiplePermissionsState(permissions)
                permissionsState.allPermissionsGranted
            }

            PermissionType.BLUETOOTH -> {
                val permissions = allBluetoothPermissions()
                val permissionsState = rememberMultiplePermissionsState(permissions)
                isRunningOnEmulator() || permissionsState.allPermissionsGranted
            }

            PermissionType.BATTERY -> {
                val context = LocalContext.current
                val pm = context.getSystemService(Context.POWER_SERVICE)
                if (pm is PowerManager) {
                    return pm.isIgnoringBatteryOptimizations(context.packageName)
                } else false
            }

            PermissionType.APPEAR_ON_TOP -> {
                val context = LocalContext.current
                Settings.canDrawOverlays(context)
            }

            else -> true
        }
    }

    @Composable
    fun LaunchAppearOnTop() {
        val context = LocalContext.current
        Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.fromParts("package", context.packageName, null)
        ).also {
            context.startActivity(it)
        }
    }

    @SuppressLint("BatteryLife")
    @Composable
    fun LaunchBatteryOptimization() {
        val context = LocalContext.current
        Intent(
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Uri.fromParts("package", context.packageName, null)
        ).also {
            context.startActivity(it)
        }
    }

    @Composable
    override fun LaunchSettings() {
        val context = LocalContext.current
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        ).also {
            context.startActivity(it)
        }
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    override fun LaunchLocationSettingsIfNeed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val backgroundLocationPermission =
                rememberPermissionState(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            LaunchedEffect(backgroundLocationPermission) {
                if (!backgroundLocationPermission.status.isGranted) {
                    backgroundLocationPermission.launchPermissionRequest()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun phoneCallPermissions() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.CALL_PHONE,
//                Manifest.permission.CALL_PRIVILEGED,
                Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.READ_PRECISE_PHONE_STATE
            )
        } else {
            listOf(
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_PHONE_STATE
            )
        }

    private fun backgroundLocationCompat() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            )
        } else {
            listOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }

    private fun remoteNotificationsPermissions() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyList()
        }

    private fun allBluetoothPermissions() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
            )
        } else {
            listOf(
                Manifest.permission.BLUETOOTH,
            )
        }
}

@Composable
actual fun permissionRequests(): List<PermissionType> {

    val permissions = mutableListOf(
        PermissionType.PHONE,
        PermissionType.LOCATION,
        PermissionType.APPEAR_ON_TOP,
        PermissionType.BATTERY,
        PermissionType.BLUETOOTH
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions += PermissionType.NOTIFICATION
    }

    return permissions
}

