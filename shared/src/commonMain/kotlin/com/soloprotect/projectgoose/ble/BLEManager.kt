package com.myprotect.projectx.ble

import com.myprotect.projectx.common.CommonFlow
import com.myprotect.projectx.common.Context

expect fun provideBLEManager(context: Context): BLEManager

interface BLEManager : BLEDelegate {

    var mMacId: String?

    val peripherals: CommonFlow<Set<BLEPeripheralInterface>>
    fun setListener(event: (DeviceEvent) -> Unit)
}

data class BLEPeripheral(
    override val mac: String,
    override val name: String,
    override val battery: Int,
    override val active: Boolean
) : BLEPeripheralInterface

interface BLEPeripheralInterface {
    val mac: String
    val name: String
    val battery: Int
    val active: Boolean
}

sealed interface DeviceEvent {
    data class OnDeviceConnected(val macId: String) : DeviceEvent
    data class OnDeviceDisconnected(val macId: String) : DeviceEvent
    data object OnDeviceConnectFailed : DeviceEvent
}

interface BLEDelegate {
    fun startScan(macId: String?)

    fun stopScan()

    fun connect(macId: String)

    fun disconnect(macId: String)
}
