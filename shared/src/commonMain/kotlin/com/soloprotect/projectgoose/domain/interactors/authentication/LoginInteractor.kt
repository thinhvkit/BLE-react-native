package com.myprotect.projectx.domain.interactors.authentication

import com.myprotect.projectx.domain.core.AppDataStore
import com.myprotect.projectx.domain.core.DataState
import com.myprotect.projectx.domain.core.ProgressBarState
import com.myprotect.projectx.domain.core.UIComponent
import com.myprotect.projectx.data.datasource.network.common.JAlertResponse
import com.myprotect.projectx.domain.interactors.profile.GetUserProfileInteractor
import com.myprotect.projectx.data.util.handleUseCaseException
import com.myprotect.projectx.common.Constants
import com.myprotect.projectx.common.DataStoreKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoginInteractor(
    private val appDataStoreManager: AppDataStore,
    private val getUserProfileInteractor: GetUserProfileInteractor
) {


    fun execute(
        email: String,
        password: String,
    ): Flow<DataState<String>> = flow {

        try {

            emit(DataState.Loading(progressBarState = ProgressBarState.ButtonLoading))

//            val apiResponse = service.login(email, password)
            val apiResponse = object {
                val alert: JAlertResponse? = null
                var result: String? = null
                val status: Boolean = true
            }
            apiResponse.result = "fake token"

            apiResponse.alert?.let { alert ->
                emit(
                    DataState.Response(
                        uiComponent = UIComponent.Dialog(
                            alert = alert
                        )
                    )
                )
            }

            val result = apiResponse.result


            if (result != null) {
                appDataStoreManager.setValue(
                    DataStoreKeys.EMAIL,
                    email
                )
            }

            emit(DataState.Data(result, apiResponse.status))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(handleUseCaseException(e))

        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }

    }

    fun execute(
        phoneOTP: String,
    ): Flow<DataState<String>> = flow {

        try {

            emit(DataState.Loading(progressBarState = ProgressBarState.ButtonLoading))

//            val apiResponse = service.verify(phoneOTP)
            val apiResponse = object {
                val alert: JAlertResponse? = null
                var result: String? = null
                val status: Boolean = true
            }
            apiResponse.result = "fake token"


            apiResponse.alert?.let { alert ->
                emit(
                    DataState.Response(
                        uiComponent = UIComponent.Dialog(
                            alert = alert
                        )
                    )
                )
            }

            val result = apiResponse.result

            if (result != null) {
                appDataStoreManager.setValue(
                    DataStoreKeys.TOKEN,
                    Constants.AUTHORIZATION_BEARER_TOKEN + result
                )
            }

            kotlin.runCatching {
                getUserProfileInteractor.execute().collect{ value ->
                }
            }.onFailure {
                it.printStackTrace()
            }

            emit(DataState.Data(result, apiResponse.status))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(handleUseCaseException(e))

        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }

}
