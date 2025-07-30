package com.myprotect.projectx.permissions

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreBluetooth.CBCentralManager
import platform.CoreBluetooth.CBCentralManagerDelegateProtocol
import platform.CoreBluetooth.CBManager
import platform.CoreBluetooth.CBManagerAuthorization
import platform.CoreBluetooth.CBManagerAuthorizationAllowedAlways
import platform.CoreBluetooth.CBManagerAuthorizationDenied
import platform.CoreBluetooth.CBManagerAuthorizationNotDetermined
import platform.CoreBluetooth.CBManagerAuthorizationRestricted
import platform.CoreBluetooth.CBManagerState
import platform.CoreBluetooth.CBManagerStatePoweredOff
import platform.CoreBluetooth.CBManagerStatePoweredOn
import platform.CoreBluetooth.CBManagerStateResetting
import platform.CoreBluetooth.CBManagerStateUnauthorized
import platform.CoreBluetooth.CBManagerStateUnknown
import platform.CoreBluetooth.CBManagerStateUnsupported
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class BluetoothPermissionDelegate : PermissionDelegate {
    @OptIn(ExperimentalForeignApi::class)
    override suspend fun providePermission() {
        // To maintain compatibility with iOS 12 (@see https://developer.apple.com/documentation/corebluetooth/cbmanagerauthorization)
        val isNotDetermined: Boolean =
//            if (CBManager.resolveClassMethod(NSSelectorFromString("authorization"))) {
                CBManager.authorization == CBManagerAuthorizationNotDetermined
//            } else {
//                CBCentralManager().state == CBManagerStateUnknown
//            }

        val state: CBManagerState = if (isNotDetermined) {
            suspendCoroutine { continuation ->
                CBCentralManager(object : NSObject(), CBCentralManagerDelegateProtocol {
                    override fun centralManagerDidUpdateState(central: CBCentralManager) {
                        continuation.resume(central.state)
                    }
                }, null)
            }
        } else {
            CBCentralManager().state
        }

        when (state) {
            CBManagerStatePoweredOn -> return
            CBManagerStateUnauthorized -> throw Exception("Unauthorized")
            CBManagerStatePoweredOff ->
                throw Exception( "Bluetooth is powered off")

            CBManagerStateResetting ->
                throw Exception( "Bluetooth is restarting")

            CBManagerStateUnsupported ->
                throw Exception("Bluetooth is not supported on this device")

            CBManagerStateUnknown -> return
//                error("Bluetooth state should be known at this point")

            else ->
                error("Unknown state (Permissions library should be updated) : $state")
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun getPermissionState(): PermissionStatus {
        // To maintain compatibility with iOS 12 (@see https://developer.apple.com/documentation/corebluetooth/cbmanagerauthorization)
        //        if (CBManager.resolveClassMethod(NSSelectorFromString("authorization"))) {
            return when (val state: CBManagerAuthorization = CBManager.authorization) {
                CBManagerAuthorizationNotDetermined -> PermissionStatus.NOT_DETERMINED
                CBManagerAuthorizationAllowedAlways, CBManagerAuthorizationRestricted -> PermissionStatus.GRANTED
                CBManagerAuthorizationDenied -> PermissionStatus.DENIED
                else -> error("unknown state $state")
            }
//        }
//        return when (val state: CBManagerState = CBCentralManager().state) {
//            CBManagerStatePoweredOn -> PermissionStatus.GRANTED
//            CBManagerStateUnauthorized, CBManagerStatePoweredOff,
//            CBManagerStateResetting, CBManagerStateUnsupported -> PermissionStatus.DENIED
//
//            CBManagerStateUnknown -> PermissionStatus.NOT_DETERMINED
//            else -> error("unknown state $state")
//        }
    }
}
