package com.myprotect.projectx.presentation.ui.permission.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myprotect.projectx.domain.core.DataState
import com.myprotect.projectx.domain.core.UIComponentState
import com.myprotect.projectx.domain.interactors.authentication.CheckTokenInteractor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PermissionViewModel(
    private val checkTokenInteractor: CheckTokenInteractor
) : ViewModel() {
    val state: MutableState<PermissionState> = mutableStateOf(PermissionState())

    init {
        checkToken()
    }

    fun onTriggerEvent(event: PermissionEvent) {
        when (event) {
            is PermissionEvent.Error -> {}
            is PermissionEvent.OnUpdatePermissionDialog -> {
                onUpdatePermissionDialog(event.value)
            }

            is PermissionEvent.OnUpdateCancelDialog -> {
                onUpdateCancelDialog(event.value)
            }
        }
    }

    private fun onUpdatePermissionDialog(value: UIComponentState) {
        state.value = state.value.copy(permissionDialog = value)
    }

    private fun onUpdateCancelDialog(value: UIComponentState) {
        state.value = state.value.copy(cancelDialog = value)
    }

    private fun checkToken() {
        checkTokenInteractor.execute().onEach { dataState ->
            when (dataState) {
                is DataState.NetworkStatus -> {}
                is DataState.Response -> {
                    onTriggerEvent(PermissionEvent.Error(dataState.uiComponent))
                }

                is DataState.Data -> {
                    state.value = state.value.copy(isTokenValid = dataState.data ?: false)
                    state.value = state.value.copy(navigateToMain = dataState.data ?: false)
                }

                is DataState.Loading -> {
                    state.value =
                        state.value.copy(progressBarState = dataState.progressBarState)
                }
            }
        }.launchIn(viewModelScope)
    }

}
