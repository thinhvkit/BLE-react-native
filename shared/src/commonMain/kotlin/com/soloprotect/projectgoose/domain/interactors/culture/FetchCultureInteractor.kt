package com.myprotect.projectx.domain.interactors.culture

import com.myprotect.api.apis.myprotectMobileBffApiApi
import com.myprotect.projectx.domain.core.AppDataStore
import com.myprotect.projectx.domain.core.DataState
import com.myprotect.projectx.domain.core.ProgressBarState
import com.myprotect.projectx.domain.models.country.Country
import com.myprotect.projectx.domain.models.language.Language
import com.myprotect.projectx.domain.models.timezone.TimeZone
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FetchCultureInteractor(
    private val apiClient: myprotectMobileBffApiApi,
    private val appDataStore: AppDataStore,
) {

    fun execute(): Flow<DataState<Unit>> = flow {

        try {

            emit(DataState.Loading(progressBarState = ProgressBarState.ButtonLoading))

            val response = apiClient.apiCulturesGet()

            val cultures = response.body()
            val languages = cultures.locales.map { Language(it.localeCode) }
            val countries = cultures.countries.map { Country(it.isoCode) }
            val timezones = cultures.timeZones.map { TimeZone(it.timezoneId) }

            appDataStore.supportLanguages = languages
            appDataStore.supportCountries = countries
            appDataStore.supportTimezones = timezones

            emit(DataState.Data(Unit))

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }
}
