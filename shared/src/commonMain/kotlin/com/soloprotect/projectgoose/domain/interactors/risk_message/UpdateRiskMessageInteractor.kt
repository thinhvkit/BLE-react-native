package com.myprotect.projectx.domain.interactors.risk_message

import com.myprotect.api.apis.myprotectMobileBffApiApi
import com.myprotect.api.models.AcknowledgedStatus
import com.myprotect.api.models.UpdateRiskMessageRequest
import com.myprotect.projectx.domain.core.AppDataStore
import com.myprotect.projectx.domain.core.DataState
import com.myprotect.projectx.domain.core.ProgressBarState
import com.myprotect.projectx.common.LocationData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock

class UpdateRiskMessageInteractor(
    private val apiClient: myprotectMobileBffApiApi,
    private val appDataStore: AppDataStore
) {
    fun execute(
        messageId: String,
        status: AcknowledgedStatus,
        locationData: LocationData? = null,
    ): Flow<DataState<Boolean>> = flow {

        try {

            emit(DataState.Loading(progressBarState = ProgressBarState.ScreenLoading))

            val request = UpdateRiskMessageRequest(
                acknowledgedState = status.value,
                acknowledgedUtc = Clock.System.now(),
                shareLocation = appDataStore.privacyMode,
                latitude = locationData?.latitude,
                longitude = locationData?.longitude,
                accuracy = 0.0,
            )

            apiClient.apiUserRiskmessagesMessageIdPut(messageId, request)

            emit(DataState.Data(true))

        } catch (e: Exception) {
            e.printStackTrace()

        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }

    }
}
