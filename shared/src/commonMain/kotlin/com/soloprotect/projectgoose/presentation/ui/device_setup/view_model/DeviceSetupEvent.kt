package com.myprotect.projectx.presentation.ui.device_setup.view_model

import com.myprotect.projectx.domain.core.UIComponent
import com.myprotect.projectx.domain.core.UIComponentState

sealed class DeviceSetupEvent {
    data class OnUpdateSetupDialog(val value: UIComponentState) : DeviceSetupEvent()
    data class OnSelectBluDevice(val value: String) : DeviceSetupEvent()
    data object Scan : DeviceSetupEvent()
    data class Connect(val mac: String) : DeviceSetupEvent()
    data class Disconnect(val mac: String) : DeviceSetupEvent()

    data class Error(
        val uiComponent: UIComponent
    ) : DeviceSetupEvent()
}
