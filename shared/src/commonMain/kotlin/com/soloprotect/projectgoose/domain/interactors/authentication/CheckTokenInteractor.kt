package com.myprotect.projectx.domain.interactors.authentication


import com.myprotect.projectx.domain.core.AppDataStore
import com.myprotect.projectx.domain.core.DataState
import com.myprotect.projectx.domain.core.ProgressBarState
import com.myprotect.projectx.domain.core.UIComponent
import com.myprotect.projectx.data.datasource.network.common.JAlertResponse
import com.myprotect.projectx.common.DataStoreKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CheckTokenInteractor(
    private val appDataStoreManager: AppDataStore,
) {

    fun execute(): Flow<DataState<Boolean>> = flow {

        try {

            emit(DataState.Loading(progressBarState = ProgressBarState.ButtonLoading))


            val token = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""


            val isTokenValid = token.isNotEmpty()


            emit(DataState.Data(isTokenValid))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(
                DataState.Response(
                uiComponent = UIComponent.Toast(
                    JAlertResponse(
                        title = "Session Expired",
                        message = "Please login again.",
                    )
                )
            ))

        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }


    }


}
