package com.myprotect.projectx.domain.interactors.authentication

import com.myprotect.projectx.domain.core.AppDataStore
import com.myprotect.projectx.domain.core.DataState
import com.myprotect.projectx.domain.core.ProgressBarState
import com.myprotect.projectx.data.util.handleUseCaseException
import com.myprotect.projectx.common.DataStoreKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LogoutInteractor(
    private val appDataStoreManager: AppDataStore,
) {

    fun execute(): Flow<DataState<Boolean>> = flow {

        try {

            emit(DataState.Loading(progressBarState = ProgressBarState.ButtonLoading))

            appDataStoreManager.setValue(
                DataStoreKeys.TOKEN,
                ""
            )

            emit(DataState.Data(true))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(handleUseCaseException(e))

        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }
}
