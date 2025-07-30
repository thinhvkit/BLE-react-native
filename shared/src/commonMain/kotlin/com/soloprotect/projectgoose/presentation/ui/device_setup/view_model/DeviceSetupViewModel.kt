package com.myprotect.projectx.presentation.ui.device_setup.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myprotect.projectx.ble.BLEManager
import com.myprotect.projectx.ble.DeviceEvent
import com.myprotect.projectx.domain.core.AppDataStore
import com.myprotect.projectx.domain.core.ProgressBarState
import com.myprotect.projectx.domain.core.UIComponentState
import com.myprotect.projectx.common.DataStoreKeys
import com.myprotect.projectx.presentation.ui.device_setup.components.SetupStatus
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeviceSetupViewModel(
    private val blueMinew: BLEManager,
    private val appDataStoreManager: AppDataStore,
) : ViewModel() {

    private val state: MutableStateFlow<DeviceSetupState> = MutableStateFlow(DeviceSetupState())

    val deviceState: StateFlow<DeviceSetupState> get() = state

    init {
        blueMinew.setListener { event ->

            viewModelScope.launch {

                when (event) {
                    is DeviceEvent.OnDeviceConnected -> {
                        state.update { state ->
                            val updateDevices = state.devices.toMutableMap()
                            updateDevices[event.macId]?.let {
                                updateDevices[event.macId] = it.copy(connected = true)
                            }
                            state.copy(
                                devices = HashMap(updateDevices),
                                bluDeviceConnected = event.macId,
                                setupDialogStatus = SetupStatus.SUCCESS,
                                progressBarState = ProgressBarState.Idle
                            )
                        }
                        appDataStoreManager.setValue(
                            DataStoreKeys.DEVICE_SETUP,
                            event.macId
                        )
                    }

                    is DeviceEvent.OnDeviceDisconnected -> {
                        state.update { state ->
                            state.copy(
                                progressBarState = ProgressBarState.Idle
                            )
                        }
                    }

                    DeviceEvent.OnDeviceConnectFailed -> {
                        state.update { state ->
                            state.copy(
                                setupDialogStatus = SetupStatus.FAILED
                            )
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            val mac = async {
                appDataStoreManager.readValue(
                    DataStoreKeys.DEVICE_SETUP
                )
            }.await()

            state.update {
                it.copy(
                    bluDeviceConnected = mac
                )
            }

            blueMinew.mMacId = mac

            blueMinew.mMacId?.let {
                scan()
            }

            blueMinew.peripherals.watch { peripherals ->
                peripherals.filter { v -> v.name.uppercase() != "UNNAMED" }.forEach { peripheral ->
                    state.update {
                        val updateDevices = it.devices.toMutableMap()
                        updateDevices[peripheral.mac] =
                            EnhancedBluetoothPeripheral(false, peripheral)
                        it.copy(
                            devices = HashMap(updateDevices),
                        )
                    }
                }
            }
        }
    }

    fun onTriggerEvent(event: DeviceSetupEvent) {
        when (event) {
            is DeviceSetupEvent.Error -> {}

            DeviceSetupEvent.Scan -> {
                scan()
            }

            is DeviceSetupEvent.Connect -> {
                connect(event.mac)
            }

            is DeviceSetupEvent.Disconnect -> {
                remove(event.mac)
            }

            is DeviceSetupEvent.OnSelectBluDevice -> onSelectBluDevice(event.value)

            is DeviceSetupEvent.OnUpdateSetupDialog -> {
                onUpdateSetupDialog(event.value)
            }
        }
    }

    private fun onUpdateSetupDialog(value: UIComponentState) {
        state.update {
            it.copy(
                setupDialog = value,
                setupDialogStatus = SetupStatus.PROCESSING
            )
        }
    }

    private fun onSelectBluDevice(value: String) {
        state.update {
            it.copy(
                bluDeviceSelected = value,
                progressBarState = ProgressBarState.Idle
            )
        }
    }

    private fun scan() {
        viewModelScope.launch {
            val mac = async {
                appDataStoreManager.readValue(
                    DataStoreKeys.DEVICE_SETUP
                )
            }.await()

            state.update {
                it.copy(
                    progressBarState = if (mac.isNullOrEmpty()) ProgressBarState.ScreenLoading else ProgressBarState.Idle
                )
            }

            blueMinew.startScan(mac)
        }
    }

    private fun connect(macId: String) {
        state.update {
            it.copy(
                progressBarState = ProgressBarState.ScreenLoading
            )
        }
        state.value.devices[macId]?.let {
            blueMinew.connect(macId)
        }
    }

    private fun disconnect(macId: String) {
        state.value.devices[macId]?.let {
            blueMinew.disconnect(macId)
        }
    }

    private fun remove(macId: String) {
        viewModelScope.launch {
            appDataStoreManager.setValue(
                DataStoreKeys.DEVICE_SETUP,
                ""
            )

            state.update { state ->

                val updateDevices = state.devices.toMutableMap()
                updateDevices[macId]?.let {
                    updateDevices[macId] = it.copy(connected = false)
                }

                state.copy(
                    devices = HashMap(updateDevices),
                    bluDeviceConnected = null,
                )
            }
        }
    }
}
