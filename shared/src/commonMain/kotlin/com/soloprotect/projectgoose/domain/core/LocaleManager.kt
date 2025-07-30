package com.myprotect.projectx.domain.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.myprotect.projectx.domain.interactors.language.GelAllTranslationInteractor
import com.myprotect.projectx.common.Context
import com.myprotect.projectx.common.readJsonFile
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.compose.getKoin

class LocaleManager(
    private val appDataStore: AppDataStore,
    private val getAllLanguageInteractor: GelAllTranslationInteractor,
    private val context: Context,
) {

    companion object {
        const val DEFAULT_FILE_NAME = "default_language_pack"

        @Composable
        fun getStringLocalization(key: String): String = key.tr()
    }

    private var mDefaultLanguage: JsonObject? = null
    private var mUserLanguage: JsonObject? = null

    val languageCode: MutableState<String> = mutableStateOf(appDataStore.userLanguage)

    fun changeLanguage(code: String) {
        updateUserLanguage()
        appDataStore.userLanguage = code
        languageCode.value = code
    }

    private fun initDefaultLanguage() {
        try {
            val json = readJsonFile(DEFAULT_FILE_NAME, "json", context)
            mDefaultLanguage = Json.parseToJsonElement(json).jsonObject
        } catch (e: Exception) {
            e.printStackTrace()
            mDefaultLanguage = JsonObject(mapOf())
        }
    }

    fun updateUserLanguage() {
        try {
            val languagePack = JsonObject(content =
            getAllLanguageInteractor.execute().associate {
                it.translationKey!! to JsonPrimitive(it.translationValue)
            })
            mUserLanguage = languagePack
        } catch (e: Exception) {
            e.printStackTrace()
//            mUserLanguage = JsonObject(mapOf())
        }
    }

    fun getLanguageByKey(key: String): String {
        if (mDefaultLanguage == null) {
            initDefaultLanguage()
        }
        mUserLanguage?.let {
            if (it.containsKey(key)) {
                return it[key]?.jsonPrimitive?.content ?: key
            }
        }
        mDefaultLanguage?.let {
            if (it.containsKey(key)) {
                return it[key]?.jsonPrimitive?.content ?: key
            }
        }
        return key
    }
}

@Composable
fun String.tr(): String {
    val localeManager: LocaleManager = getKoin().get()
    return localeManager.getLanguageByKey(this)
}
