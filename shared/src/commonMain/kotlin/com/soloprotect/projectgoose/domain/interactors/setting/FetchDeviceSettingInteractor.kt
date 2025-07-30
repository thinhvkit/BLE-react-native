package com.myprotect.projectx.domain.interactors.setting

import com.myprotect.api.apis.myprotectMobileBffApiApi
import com.myprotect.projectx.domain.core.AppDataStore
import com.myprotect.projectx.domain.core.DataState
import com.myprotect.projectx.domain.core.ProgressBarState
import com.myprotect.projectx.domain.models.device_setting.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FetchDeviceSettingInteractor(
    private val apiClient: myprotectMobileBffApiApi,
    private val appDataStore: AppDataStore,
) {

    fun execute(): Flow<DataState<Unit>> = flow {

        try {

            emit(DataState.Loading(progressBarState = ProgressBarState.ButtonLoading))

            val response = apiClient.apiDeviceSettingsGet()

            val deviceSetting = response.body().settings.toDomain()

            appDataStore.deviceSetting = deviceSetting

            emit(DataState.Data(Unit))

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }
}
