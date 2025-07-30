package com.myprotect.projectx.domain.core

import com.myprotect.projectx.domain.models.country.Country
import com.myprotect.projectx.domain.models.device_setting.DeviceSetting
import com.myprotect.projectx.domain.models.language.Language
import com.myprotect.projectx.domain.models.profile.Profile
import com.myprotect.projectx.domain.models.timezone.TimeZone
import com.myprotect.projectx.common.DataStoreKeys
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


interface AppDataStore {

    suspend fun setValue(
        key: String,
        value: String
    )

    suspend fun readValue(
        key: String,
    ): String?

    var userLanguage: String
        get() = runBlocking { readValue(DataStoreKeys.USER_LANGUAGE) } ?: "en-GB"
        set(value) = runBlocking { setValue(DataStoreKeys.USER_LANGUAGE, value) }

    var privacyMode: Boolean
        get() = runBlocking {
            val rawResult = readValue(DataStoreKeys.PRIVACY_MODE)
            rawResult?.toBoolean() ?: false
        }
        set(value) = runBlocking {
            setValue(
                DataStoreKeys.PRIVACY_MODE,
                if (value) "true" else "false"
            )
        }

    var userProfile: Profile?
        get() = runBlocking {
            val rawResult = readValue(DataStoreKeys.USER_PROFILE)
            if(rawResult.isNullOrEmpty()) {
                null
            } else {
                Json.decodeFromString<Profile>(rawResult)
            }
        }
        set(value) = runBlocking {
            setValue(
                DataStoreKeys.USER_PROFILE,
                value?.let {
                    Json.encodeToString(value = value, serializer = Profile.serializer())
                } ?: ""
            )
        }

    var supportLanguages: List<Language>
        get() = runBlocking {
            val rawResult = readValue(DataStoreKeys.SUPPORT_LANGUAGE)
            if(rawResult.isNullOrEmpty()) {
                emptyList()
            } else {
                (Json.decodeFromString(rawResult) as List<String>).map { Language(it) }
            }
        }
        set(value) = runBlocking {
            setValue(
                DataStoreKeys.SUPPORT_LANGUAGE,
                value.let {
                    Json.encodeToString(value.map { it.code })
                }
            )
        }

    var supportTimezones: List<TimeZone>
        get() = runBlocking {
            val rawResult = readValue(DataStoreKeys.SUPPORT_TIMEZONE)
            if(rawResult.isNullOrEmpty()) {
                emptyList()
            } else {
                (Json.decodeFromString(rawResult) as List<String>).map { TimeZone(it) }
            }
        }
        set(value) = runBlocking {
            setValue(
                DataStoreKeys.SUPPORT_TIMEZONE,
                value.let {
                    Json.encodeToString(value.map { it.code })
                } ?: ""
            )
        }

    var supportCountries: List<Country>
        get() = runBlocking {
            val rawResult = readValue(DataStoreKeys.SUPPORT_COUNTRY)
            if(rawResult.isNullOrEmpty()) {
                emptyList()
            } else {
                (Json.decodeFromString(rawResult) as List<String>).map { Country(it) }
            }
        }
        set(value) = runBlocking {
            setValue(
                DataStoreKeys.SUPPORT_COUNTRY,
                value.let {
                    Json.encodeToString(value.map { it.code })
                }
            )
        }

    var deviceSetting: DeviceSetting?
        get() = runBlocking {
            val rawResult = readValue(DataStoreKeys.DEVICE_SETTINGS)
            if(rawResult.isNullOrEmpty()) {
                null
            } else {
                Json.decodeFromString<DeviceSetting>(rawResult)
            }
        }
        set(value) = runBlocking {
            setValue(
                DataStoreKeys.DEVICE_SETTINGS,
                value?.let {
                    Json.encodeToString(value = value, serializer = DeviceSetting.serializer())
                } ?: ""
            )
        }
}
