package com.myprotect.projectx.presentation.ui.device_setup.view_model

import com.myprotect.projectx.ble.BLEPeripheralInterface
import com.myprotect.projectx.domain.core.ProgressBarState
import com.myprotect.projectx.domain.core.UIComponentState
import com.myprotect.projectx.presentation.ui.device_setup.components.SetupStatus

data class DeviceSetupState(

    val devices: HashMap<String, EnhancedBluetoothPeripheral> = HashMap(),

    val bluDeviceSelected: String? = null,

    val bluDeviceConnected: String? = null,

    val setupDialog: UIComponentState = UIComponentState.Hide,

    val setupDialogStatus: SetupStatus = SetupStatus.PROCESSING,

    val progressBarState: ProgressBarState = ProgressBarState.Idle,
)

data class EnhancedBluetoothPeripheral(
    val connected: Boolean,
    val peripheral: BLEPeripheralInterface
)


