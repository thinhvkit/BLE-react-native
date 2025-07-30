package com.myprotect.projectx.presentation.ui.main.view_model

import com.myprotect.projectx.domain.core.UIComponent
import com.myprotect.projectx.domain.core.UIComponentState
import com.myprotect.projectx.common.LocationData
import kotlinx.datetime.LocalDateTime

sealed class MainEvent {
    data class OnUpdateDialog(val value: UIComponentState) : MainEvent()

    data class OnActionSelected(val value: String) : MainEvent()

    data class OnCreateEvent(
        val eventName: String, val date: LocalDateTime?,
        val locationData: LocationData?,
        val battery: Int?,
    ) : MainEvent()

    data class Error(
        val uiComponent: UIComponent
    ) : MainEvent()
}
