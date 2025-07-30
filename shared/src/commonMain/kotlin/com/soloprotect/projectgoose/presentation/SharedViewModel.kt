package com.myprotect.projectx.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myprotect.projectx.domain.core.AppDataStore
import com.myprotect.projectx.domain.core.DataState
import com.myprotect.projectx.domain.core.LocaleManager
import com.myprotect.projectx.domain.interactors.culture.FetchCultureInteractor
import com.myprotect.projectx.domain.interactors.language.FetchTranslationInteractor
import com.myprotect.projectx.domain.interactors.setting.FetchDeviceSettingInteractor
import com.myprotect.projectx.presentation.token_manager.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class SharedViewModel(
    val tokenManager: TokenManager,
    val fetchLanguageInteractor: FetchTranslationInteractor,
    val fetchDeviceSettingInteractor: FetchDeviceSettingInteractor,
    val fetchCultureInteractor: FetchCultureInteractor,
    val appDataStore: AppDataStore,
    val localeManager: LocaleManager
) : ViewModel() {

    init {
        fetchLanguage()
        fetchDeviceSetting()
        fetchCulture()
    }

    private fun fetchLanguage() {
        fetchLanguageInteractor.execute(appDataStore.userLanguage)
            .map { dataState ->
                if(dataState is DataState.Data) {
                    localeManager.changeLanguage(appDataStore.userLanguage)
                }
                dataState
            }
            .flowOn(Dispatchers.IO)
            .onEach { dataState ->
            }.flowOn(Dispatchers.Main)
            .launchIn(viewModelScope)
    }

    private fun fetchDeviceSetting() {
        fetchDeviceSettingInteractor.execute()
            .flowOn(Dispatchers.IO)
            .onEach { dataState ->
            }.flowOn(Dispatchers.Main)
            .launchIn(viewModelScope)
    }

    private fun fetchCulture() {
        fetchCultureInteractor.execute()
            .flowOn(Dispatchers.IO)
            .onEach { dataState ->
            }.flowOn(Dispatchers.Main)
            .launchIn(viewModelScope)
    }
}
