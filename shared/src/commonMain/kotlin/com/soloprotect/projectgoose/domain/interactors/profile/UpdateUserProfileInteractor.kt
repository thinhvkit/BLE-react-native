package com.myprotect.projectx.domain.interactors.profile

import com.myprotect.api.apis.myprotectMobileBffApiApi
import com.myprotect.api.models.PostUserRequest
import com.myprotect.projectx.domain.core.AppDataStore
import com.myprotect.projectx.domain.core.DataState
import com.myprotect.projectx.domain.core.ProgressBarState
import com.myprotect.projectx.domain.models.profile.Profile
import com.myprotect.projectx.extensions.formatPattern
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdateUserProfileInteractor(
    private val apiClient: myprotectMobileBffApiApi,
    private val appDataStore: AppDataStore
) {
    fun execute(
        profile: Profile
    ): Flow<DataState<Boolean>> = flow {

        try {

            emit(DataState.Loading(progressBarState = ProgressBarState.ButtonLoading))
            apiClient.apiUserPost(PostUserRequest(
                country = profile.country,
                dateOfBirth = profile.dateOfBirth?.let {
                    try {
                        profile.dateOfBirth.formatPattern("yyyy-MM-dd'T'HH:mm:ss")
                    } catch (e: Exception) {
                        null
                    }
                } ?: "1900-01-01T00:00:00",
                firstName = profile.firstName,
                lastName = profile.lastName,
                locale = profile.locale,
                medicalIssues = profile.medicalIssues,
                timeZone = profile.timeZone
            ))

            appDataStore.userProfile = profile

            emit(DataState.Data(true))

        } catch (e: Exception) {
            e.printStackTrace()
//            emit(
//                DataState.Response(
//                uiComponent = UIComponent.Toast(
//                    JAlertResponse(
//                        title = "Session Expired",
//                        message = "Please login again.",
//                    )
//                )
//            ))

        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }
}
