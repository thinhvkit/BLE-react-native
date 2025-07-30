package com.myprotect.projectx.presentation.ui.permission.view_model

import com.myprotect.projectx.domain.core.UIComponent
import com.myprotect.projectx.domain.core.UIComponentState

sealed class PermissionEvent{
    data class OnUpdatePermissionDialog(val value: UIComponentState) : PermissionEvent()
    data class OnUpdateCancelDialog(val value: UIComponentState) : PermissionEvent()

    data class Error(
        val uiComponent: UIComponent
    ) : PermissionEvent()
}
