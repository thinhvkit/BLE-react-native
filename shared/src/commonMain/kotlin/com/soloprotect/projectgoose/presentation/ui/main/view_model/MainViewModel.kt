package com.myprotect.projectx.presentation.ui.main.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myprotect.projectx.domain.core.DataState
import com.myprotect.projectx.domain.core.UIComponentState
import com.myprotect.projectx.domain.interactors.events.CreateEventInteractor
import com.myprotect.projectx.common.LocationData
import com.myprotect.projectx.common.Logger
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

class MainViewModel(private val createEventInteractor: CreateEventInteractor) : ViewModel() {
    val state: MutableState<MainState> = mutableStateOf(MainState())

    fun onTriggerEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnUpdateDialog -> {
                onUpdateDialog(event.value)
            }

            is MainEvent.Error -> {}
            is MainEvent.OnActionSelected -> {
                onActionSelected(event.value)
            }

            is MainEvent.OnCreateEvent -> {
                submitEvent(
                    event.eventName,
                    event.date,
                    event.locationData,
                    event.battery
                )
            }
        }
    }

    private fun onUpdateDialog(value: UIComponentState) {
        state.value = state.value.copy(incapacitationDialog = value)
    }

    private fun onActionSelected(value: String) {
        state.value = state.value.copy(actionSelected = value)
    }

    private fun submitEvent(
        eventName: String,
        date: LocalDateTime?,
        locationData: LocationData?,
        battery: Int?
    ) {
        viewModelScope.launch {
            createEventInteractor.execute(
                eventName, date = date, locationData = locationData, battery = battery
            ).onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                    }
                    is DataState.Data -> {
                        Logger.d("submitEvent: ${dataState.data}", "MainViewModel",)
                    }
                    is DataState.Loading -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

}
