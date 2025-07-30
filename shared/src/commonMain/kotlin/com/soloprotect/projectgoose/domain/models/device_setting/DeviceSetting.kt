package com.myprotect.projectx.domain.models.device_setting

import com.myprotect.api.models.DeviceSettingDto
import kotlinx.serialization.Serializable

@Serializable
data class DeviceSetting(
    val redAlertOutGoingNumber: String? = null,
    val companionAlertOutGoingNumber: String? = null,
    val amberAlertOutGoingNumber: String? = null,
    val incapAlertOutGoingNumber: String? = null,
)

fun DeviceSettingDto.toDomain(): DeviceSetting {
    return DeviceSetting(
        redAlertOutGoingNumber = this.redAlertOutGoingNumber,
        companionAlertOutGoingNumber = this.companionAlertOutGoingNumber,
        amberAlertOutGoingNumber = this.amberAlertOutGoingNumber,
        incapAlertOutGoingNumber = this.incapAlertOutGoingNumber
    )
}
