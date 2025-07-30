package com.myprotect.projectx.presentation.ui.main.view_model

import com.myprotect.projectx.domain.core.UIComponentState

data class MainState(
    val incapacitationDialog: UIComponentState = UIComponentState.Hide,
    val actionSelected: String = ""
)
