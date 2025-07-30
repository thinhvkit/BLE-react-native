package com.myprotect.projectx.domain.interactors.risk_message

import com.myprotect.api.apis.myprotectMobileBffApiApi
import com.myprotect.projectx.domain.core.DataState
import com.myprotect.projectx.domain.core.ProgressBarState
import com.myprotect.projectx.domain.models.risk_message.RiskMessage
import com.myprotect.projectx.domain.models.risk_message.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetRiskMessagesInteractor(
private val apiClient: myprotectMobileBffApiApi
) {
    fun execute(): Flow<DataState<List<RiskMessage>>> = flow {

        try {

            emit(DataState.Loading(progressBarState = ProgressBarState.ScreenLoading))

            val response = apiClient.apiUserRiskmessagesGet()

            val list = response.body().riskMessages?.map {
                it.toDomain()
            }?.filter { it.status != RiskMessage.STATUS_DELETED }

            emit(DataState.Data(list))

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }
}
