package com.myprotect.projectx.ble

import com.myprotect.projectx.common.CommonFlow
import com.myprotect.projectx.common.Context
import com.myprotect.projectx.common.asCommonFlow
import kotlinx.coroutines.flow.MutableStateFlow

actual fun provideBLEManager(context: Context): BLEManager {
    return IOSBLEManager()
}

class IOSBLEManager : BLEManager {

    companion object {
        private val listeners = mutableListOf<BLEDelegate>()
        private val _peripherals = MutableStateFlow<Set<BLEPeripheralInterface>>(emptySet())

        private var onDeviceEvent: (DeviceEvent) -> Unit = {}

        fun setMTPeripherals(peripherals: List<BLEPeripheral>) {
            _peripherals.value = peripherals.toSet()
        }

        fun setDeviceEvent(event: DeviceEvent) {
            onDeviceEvent(event)
        }

        fun addListener(listener: BLEDelegate) {
            listeners.add(listener)
        }
    }

    override var mMacId: String? = ""
    override val peripherals: CommonFlow<Set<BLEPeripheralInterface>>
        get() = _peripherals.asCommonFlow()

    override fun startScan(macId: String?) {
        listeners.forEach { it.startScan(macId) }
    }

    override fun stopScan() {
        listeners.forEach { it.stopScan() }
    }

    override fun connect(macId: String) {
        listeners.forEach { it.connect(macId) }
    }

    override fun disconnect(macId: String) {
        listeners.forEach { it.disconnect(macId) }
    }

    override fun setListener(event: (DeviceEvent) -> Unit) {
        onDeviceEvent = event
    }

}
