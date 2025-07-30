package com.myprotect.projectx.domain.interactors.language

import com.myprotect.api.apis.myprotectMobileBffApiApi
import com.myprotect.projectx.domain.core.DataState
import com.myprotect.projectx.domain.core.ProgressBarState
import com.myprotect.projectx.extensions.toLong
import com.myprotect.projectx.db.AppDatabase
import com.myprotect.projectx.db.TranslationEntityQueries
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FetchTranslationInteractor(
    private val apiClient: myprotectMobileBffApiApi,
    private val appDatabase: AppDatabase,
) {

    private val dao: TranslationEntityQueries by lazy {
        appDatabase.translationEntityQueries
    }

    fun execute(languageCode: String): Flow<DataState<Unit>> = flow {

        try {

            emit(DataState.Loading(progressBarState = ProgressBarState.ScreenLoading))

            val response = apiClient.apiTranslationsLanguageGet(languageCode)

            val list = response.body().translations

            if(!list.isNullOrEmpty()) {
                list.forEach {
                    dao.insertUpdate(
                        translationKey = it.translationKey,
                        translationLanguage = it.translationLanguage,
                    translationId = it.translationId.toLong(),
                    active = it.active.toLong(),
                    translationValue = it.translationValue,
                    )
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
