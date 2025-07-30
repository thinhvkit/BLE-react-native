package com.myprotect.projectx.ble;

import android.content.Intent
import androidx.core.content.ContextCompat
import com.minew.beaconplus.sdk.MTCentralManager
import com.minew.beaconplus.sdk.MTPeripheral
import com.minew.beaconplus.sdk.enums.BluetoothState
import com.minew.beaconplus.sdk.enums.ConnectionStatus
import com.minew.beaconplus.sdk.enums.FrameType
import com.minew.beaconplus.sdk.enums.TriggerType
import com.minew.beaconplus.sdk.exception.MTException
import com.minew.beaconplus.sdk.frames.UrlFrame
import com.minew.beaconplus.sdk.interfaces.ConnectionStatueListener
import com.minew.beaconplus.sdk.interfaces.GetPasswordListener
import com.minew.beaconplus.sdk.model.Trigger
import com.myprotect.projectx.callCenter.CallCenterManagerImpl
import com.myprotect.projectx.callCenter.CallCenterService
import com.myprotect.projectx.common.CommonFlow
import com.myprotect.projectx.common.Constants
import com.myprotect.projectx.common.Context
import com.myprotect.projectx.common.Logger
import com.myprotect.projectx.common.asCommonFlow
import com.myprotect.projectx.extensions.hasBluetoothPermission
import com.myprotect.projectx.notifications.NotifierManagerImpl
import get
import kotlinx.coroutines.flow.MutableStateFlow

actual fun provideBLEManager(context: Context): BLEManager {
    return AndroidBLEManager(context)
}

class AndroidBLEManager(private val context: Context) : BLEManager {

    private lateinit var mtCentralManager: MTCentralManager

    private lateinit var mMTPeripherals: HashMap<String, MTPeripheral>

    override var mMacId: String? = null

    private val _peripherals = MutableStateFlow<Set<BLEPeripheralInterface>>(emptySet())
    override val peripherals: CommonFlow<Set<BLEPeripheralInterface>>
        get() = _peripherals.asCommonFlow()

    private var listeners = mutableListOf<(DeviceEvent) -> Unit>()

    private var bluetoothState: BluetoothState? = null

    private var callCenter = CallCenterManagerImpl.getCallCenter()
    private var notifier = NotifierManagerImpl.getLocalNotifier()

    private val multipleEventsCutter = MultipleEventsCutter.get()

    private fun initManager() {
        mtCentralManager = MTCentralManager.getInstance(context)

        startService()

        bluetoothState = mtCentralManager.getBluetoothState(context)

        mtCentralManager.setBluetoothChangedListener {
            if (it == null) return@setBluetoothChangedListener

            when (it) {
                BluetoothState.BluetoothStateNotSupported -> {
                    Logger.e(
                        "BluetoothStateNotSupported", TAG
                    )
                    bluetoothState = BluetoothState.BluetoothStateNotSupported
                }

                BluetoothState.BluetoothStatePowerOff -> {
                    Logger.e("BluetoothStatePowerOff", TAG)
                    bluetoothState = BluetoothState.BluetoothStatePowerOff

                    _peripherals.value = _peripherals.value.map { p ->
                        BLEPeripheral(
                            p.mac,
                            p.name,
                            p.battery,
                            active = false
                        )
                    }.toSet()
                }

                BluetoothState.BluetoothStatePowerOn -> {
                    Logger.e("BluetoothStatePowerOn", TAG)
                    bluetoothState = BluetoothState.BluetoothStatePowerOn
                }
            }
        }

        mtCentralManager.setMTCentralManagerListener { peripherals ->

            Logger.d( "scanning ${peripherals[0].mMTFrameHandler.mac}", "BLEController")

            val deviceConnectedNotAvailable =
                !mMacId.isNullOrEmpty() && peripherals.find { i -> i.mMTFrameHandler.mac == mMacId } == null

            if (deviceConnectedNotAvailable) {
                _peripherals.value = setOf(
                    BLEPeripheral(
                        mMacId!!,
                        "",
                        0,
                        false
                    )
                )
            } else {
                val params = HashMap<String, MTPeripheral>()
                _peripherals.value = peripherals.map {
                    val mtFrameHandler = it.mMTFrameHandler
                    val mac = mtFrameHandler.mac //mac address of device
                    val name = mtFrameHandler.name // name of device
                    val battery = mtFrameHandler.battery //battery
//                    val rssi = mtFrameHandler.rssi //rssi
//                    val lastUpdate = mtFrameHandler.lastUpdate //last updated time
//                    Logger.i( "$mac - $name", TAG)

                    params[mac] = it


                    if (mac == mMacId) {

                        // all data frames of device（such as:iBeacon，UID，URL...）
                        val advFrames = mtFrameHandler.advFrames
                        for (minewFrame in advFrames) {
                            val frameType = minewFrame.frameType

                            when (frameType) {
                                FrameType.FrameURL -> {
                                    multipleEventsCutter.processEvent(
                                        {
                                            onTriggered(minewFrame.lastUpdate)
                                        },
                                        300L
                                    )
                                }

                                else -> {}
                            }
                        }
                    }

                    if (mMacId.isNullOrEmpty()) {
                        BLEPeripheral(
                            mac,
                            name,
                            battery,
                            bluetoothState == BluetoothState.BluetoothStatePowerOn
                        )
                    } else {
                        BLEPeripheral(
                            mac,
                            name,
                            battery,
                            bluetoothState == BluetoothState.BluetoothStatePowerOn
                        )
                    }
                }.toSet()

                mMTPeripherals = params
            }
        }
    }

    init {
        initManager()
    }

    private fun onTriggered(lastUpdate: Long) {
        val currentTime = System.currentTimeMillis()
        if ((currentTime - lastUpdate) < 5) {
            callCenter.makeRedAlertCall()

            notifier.notify(13, "Bluetooth button", "Triple Tap Trigger")
        }
    }

    private fun startService() {
        mtCentralManager.registerBleChangeBroadcast()

        mtCentralManager.startService()

        CallCenterService.setBLEManager(this)
        ContextCompat.startForegroundService(
            context,
            Intent(context, CallCenterService::class.java)
        )
    }

    private fun stopService() {
        mtCentralManager.unRegisterBleChangeBroadcast()
        mtCentralManager.stopService()
    }


    override fun startScan(macId: String?) {
        mMacId = macId

        if (!context.hasBluetoothPermission()) {
            Logger.i("do not have BluetoothPermission", TAG)
            return
        }

        if (!mMacId.isNullOrEmpty()) {
            _peripherals.value = setOf(
                BLEPeripheral(
                    mMacId!!,
                    "Unnamed",
                    0,
                    false
                )
            )
        }
        if (mtCentralManager.isScanning) {
            // Stop scanning
            mtCentralManager.stopScan()
        }
        // Clear cache
        mtCentralManager.clear()
        // Start scanning
        mtCentralManager.startScan()

        val intent = Intent(context, CallCenterService::class.java).apply {
            action = Constants.CALL_SERVICE_START_BLUETOOTH_SERVICE
            putExtra(Constants.MAC_ID, macId)
        }

        ContextCompat.startForegroundService(context, intent)

    }

    override fun stopScan() {
        if (mtCentralManager.isScanning) {
            mtCentralManager.stopScan()
        }

        val intent = Intent(context, CallCenterService::class.java).apply {
            action = Constants.CALL_SERVICE_STOP_BLUETOOTH_SERVICE
        }

        ContextCompat.startForegroundService(context, intent)
    }

    override fun connect(macId: String) {
        mMTPeripherals[macId]?.let { this.connect(it) }
    }

    override fun disconnect(macId: String) {
        this.disconnect(mtCentralManager.getConnectedMTPeripheral(macId))
    }

    override fun setListener(event: (DeviceEvent) -> Unit) {
        listeners.add(event)
    }

    private fun connect(mtPeripheral: MTPeripheral) {

        val macId = mtPeripheral.mMTFrameHandler.mac
        val connectionStatueListener: ConnectionStatueListener =
            object : ConnectionStatueListener {

                override fun onUpdateConnectionStatus(
                    connectionStatus: ConnectionStatus?, getPasswordListener: GetPasswordListener?
                ) {

                    Logger.i(connectionStatus?.name ?: "", TAG)

                    when (connectionStatus) {
                        ConnectionStatus.CONNECTED -> {
                            mMacId = macId
                        }

                        ConnectionStatus.PASSWORDVALIDATING -> {
                            getPasswordListener?.getPassword(MINEW_PASSWORD_DEFAULT)
                        }

                        ConnectionStatus.CONNECTFAILED -> {
                            listeners.forEach {
                                it(DeviceEvent.OnDeviceConnectFailed)
                            }
                        }

                        ConnectionStatus.DISCONNECTED -> {
                            mMacId?.let {
                                listeners.forEach {
                                    it(DeviceEvent.OnDeviceDisconnected(mMacId!!))
                                }
                            }
                        }

                        ConnectionStatus.COMPLETED -> {
                            listeners.forEach {
                                it(
                                    DeviceEvent.OnDeviceConnected(
                                        macId
                                    )
                                )
                            }
                            saveBroadcastParams()
                        }

                        else -> {}
                    }
                }

                override fun onError(e: MTException) {
                    Logger.e(e.message, TAG)
                    listeners.forEach {
                        it(DeviceEvent.OnDeviceConnectFailed)
                    }
                }
            }

        mtCentralManager.connect(mtPeripheral, connectionStatueListener)
    }

    private fun disconnect(mtPeripheral: MTPeripheral) {
        mtCentralManager.disconnect(mtPeripheral)
    }

    fun resetFactory() {
        val mMTPeripheral = this.mMTPeripherals[mMacId]
        mMTPeripheral?.mMTConnectionHandler?.resetFactorySetting { success, mtException ->
            if (success) {
                Logger.i("Success!", "$TAG resetFactory");
                saveBroadcastParams()
            } else {
                Logger.i("Failed! ${mtException.message}", "$TAG resetFactory");
            }
        }
    }

    private fun saveTrigger(
        mCurSlot: Int,
        isOpen: Boolean,
        triggerType: TriggerType? = TriggerType.TRIGGER_SRC_NONE
    ) {
        val mMTPeripheral = this.mMTPeripherals[mMacId]
        val mMTConnectionHandler = mMTPeripheral?.mMTConnectionHandler ?: return

        val trigger = Trigger()

        trigger.curSlot = mCurSlot
        if (isOpen) {
            trigger.triggerType = TriggerType.BTN_TTAP_EVT
            when (triggerType) {
                TriggerType.TEMPERATURE_ABOVE_ALARM, TriggerType.TEMPERATURE_BELOW_ALARM, TriggerType.HUMIDITY_ABOVE_ALRM, TriggerType.HUMIDITY_BELOW_ALRM, TriggerType.LIGHT_ABOVE_ALRM, TriggerType.LIGHT_BELOW_ALARM, TriggerType.FORCE_ABOVE_ALRM, TriggerType.FORCE_BELOW_ALRM, TriggerType.TVOC_ABOVE_ALARM, TriggerType.TVOC_BELOW_ALARM -> trigger.condition =
                    10

                else -> trigger.condition = 10 * 1000
            }
        } else {
            trigger.triggerType = TriggerType.TRIGGER_SRC_NONE
            trigger.condition = 0
        }

        trigger.advInterval = 2000
        trigger.radioTxpower = 0
        trigger.isAlwaysAdvertising = false

        mMTConnectionHandler.setTriggerCondition(
            trigger
        ) { success, _ -> //Monitor whether the write is successful
            Logger.e("trigger $mCurSlot success $success", TAG)
        }
    }

    private fun saveBroadcastParams() {
        val mConnectedMTPeripheral = mMTPeripherals[mMacId] ?: return

        val allFrames = mConnectedMTPeripheral.mMTConnectionHandler.allFrames
        val slotAtitude =
            mConnectedMTPeripheral.mMTConnectionHandler.mTConnectionFeature.slotAtitude


        for (i in 0..<slotAtitude) {

            val frameType = allFrames[i].frameType

            when (frameType) {
                FrameType.FrameURL -> {
                    saveTrigger(i, isOpen = true, TriggerType.BTN_TTAP_EVT)
                    val urlFrame = allFrames[i] as UrlFrame
                    urlFrame.urlString = MINEW_URL
                    mConnectedMTPeripheral.mMTConnectionHandler.writeSlotFrame(
                        urlFrame, i
                    ) { b, _ ->
                        Logger.e("writeSlotFrame FrameURL success $b", TAG)
                    }
                }

                FrameType.FrameiBeacon, FrameType.FrameDeviceInfo -> {
                    saveTrigger(i, isOpen = false, TriggerType.TRIGGER_SRC_NONE)
                }

                else -> {

                    saveTrigger(i, isOpen = false)

                    allFrames[i].frameType = FrameType.FrameNone

                    mConnectedMTPeripheral.mMTConnectionHandler.writeSlotFrame(
                        allFrames[i], i
                    ) { b, e ->
                        Logger.e("writeSlotFrame success $b", TAG)

                        if (i == slotAtitude - 1) {
                            mMacId?.let { disconnect(it) }
                        }
                    }
                }
            }
        }
    }

    fun powerOff() {
        val mMTPeripheral = this.mMTPeripherals[mMacId]
        val mMTConnectionHandler = mMTPeripheral?.mMTConnectionHandler
        mMTConnectionHandler?.powerOff { success, _ ->
            if (success) {
                Logger.i("Success!", "$TAG-powerOff")
            } else {
                Logger.e("Failed!", "$TAG-powerOff")
            }
        }
    }

    internal companion object {
        const val TAG = "AndroidBLEManager"
        const val MINEW_PASSWORD_DEFAULT = "minew123"
        const val MINEW_URL = "https://www.sos/"
    }
}
