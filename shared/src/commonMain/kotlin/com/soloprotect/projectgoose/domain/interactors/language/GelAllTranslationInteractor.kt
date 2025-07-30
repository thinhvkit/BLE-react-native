package com.myprotect.projectx.domain.interactors.language

import com.myprotect.projectx.domain.models.language.Translation
import com.myprotect.projectx.db.AppDatabase
import com.myprotect.projectx.db.TranslationEntityQueries

class GelAllTranslationInteractor(
    private val appDatabase: AppDatabase,
) {

    private val dao: TranslationEntityQueries by lazy {
        appDatabase.translationEntityQueries
    }

    fun execute(): List<Translation> {
        return dao.selectAll(mapper = ::translationFactory).executeAsList()
    }

    private fun translationFactory(
        translationKey: String,
        translationLanguage: String,
        translationId: Long,
        active: Long,
        translationValue: String,
    ) = Translation(
        active = if(active == 1L) true else false,
        translationId = translationId,
        translationKey = translationKey,
        translationLanguage = translationLanguage,
        translationValue = translationValue
    )
}
