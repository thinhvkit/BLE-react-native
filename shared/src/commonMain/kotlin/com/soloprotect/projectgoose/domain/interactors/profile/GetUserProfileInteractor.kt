package com.myprotect.projectx.domain.interactors.profile

import com.myprotect.api.apis.myprotectMobileBffApiApi
import com.myprotect.projectx.domain.core.AppDataStore
import com.myprotect.projectx.domain.core.DataState
import com.myprotect.projectx.domain.core.LocaleManager
import com.myprotect.projectx.domain.core.ProgressBarState
import com.myprotect.projectx.domain.models.profile.Profile
import com.myprotect.projectx.domain.interactors.language.FetchTranslationInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDateTime

class GetUserProfileInteractor(
    private val apiClient: myprotectMobileBffApiApi,
    private val appDataStore: AppDataStore,
    private val localeManager: LocaleManager,
    private val fetchLanguageInteractor: FetchTranslationInteractor
) {
    fun execute(): Flow<DataState<Unit>> = flow {

        try {

            emit(DataState.Loading(progressBarState = ProgressBarState.ButtonLoading))

            val response = apiClient.apiUserGet()

            val profileDto = response.body()

            val profile = Profile(
                country = profileDto.country,
                dateOfBirth = profileDto.dateOfBirth?.let {
                    LocalDateTime.parse(it)
                },
                firstName = profileDto.firstName,
                lastName = profileDto.lastName,
                locale = profileDto.locale,
                medicalIssues = profileDto.medicalIssues,
                timeZone = profileDto.timeZone
            )

            appDataStore.userProfile = profile
            profile.locale?.let {
                if(it != appDataStore.userLanguage) {
                    fetchLanguageInteractor.execute(profile.locale)
                    localeManager.changeLanguage(profile.locale)
                }
            }

            emit(DataState.Data(Unit))

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }
}
