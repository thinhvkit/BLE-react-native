package com.myprotect.projectx.presentation.ui.navigation_drawer.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myprotect.projectx.domain.core.AppDataStore
import com.myprotect.projectx.domain.core.DataState
import com.myprotect.projectx.domain.interactors.authentication.LogoutInteractor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class NavigationDrawerViewModel(
    private val logoutInteractor: LogoutInteractor,
    private val appDataStore: AppDataStore
) : ViewModel() {
    val state: MutableState<NavigationDrawerState> = mutableStateOf(NavigationDrawerState(profileName = appDataStore.userProfile?.fullName?: ""))

    fun onTriggerEvent(event: NavigationDrawerEvent) {
        when (event) {
            is NavigationDrawerEvent.Logout -> {
                logout()
            }

            is NavigationDrawerEvent.Error -> {}
        }
    }

    private fun logout() {
        logoutInteractor.execute()
            .onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        onTriggerEvent(NavigationDrawerEvent.Error(dataState.uiComponent))
                    }

                    is DataState.Data -> {
                        dataState.data?.let {
                            state.value = state.value.copy(logout = it)

                        }
                    }

                    is DataState.Loading -> {
                        state.value =
                            state.value.copy(progressBarState = dataState.progressBarState)
                    }
                }
            }.launchIn(viewModelScope)
    }

}
