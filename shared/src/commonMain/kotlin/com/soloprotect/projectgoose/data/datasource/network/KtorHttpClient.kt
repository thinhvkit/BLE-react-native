package com.myprotect.projectx.data.datasource.network

import com.myprotect.api.apis.myprotectMobileBffApiApi
import com.myprotect.projectx.EnvConfig
import com.myprotect.projectx.common.DataStoreKeys
import com.myprotect.projectx.common.LoggerNative
import com.myprotect.projectx.domain.core.AppDataStore
import com.myprotect.projectx.presentation.token_manager.TokenEvent
import com.myprotect.projectx.presentation.token_manager.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
    fun httpClient(tokenManager: TokenManager, appDataStore: AppDataStore) = myprotectMobileBffApiApi(
        baseUrl = EnvConfig.apiEndPoint,
        httpClient = HttpClient {
            expectSuccess = false
            install(HttpTimeout) {
                val timeout = 60000L
                connectTimeoutMillis = timeout
                requestTimeoutMillis = timeout
                socketTimeoutMillis = timeout
            }

            install(ResponseObserver) {
                onResponse { response ->
                    LoggerNative.debug("","AppDebug HTTP ResponseObserver status: ${response.status.value}")
                }
            }
            HttpResponseValidator {
                validateResponse { response: HttpResponse ->
                    val statusCode = response.status.value

                    if (statusCode == 401) {
                        tokenManager.onTriggerEvent(TokenEvent.Logout)
                    }

                }
            }

            install(Logging) {
                //  logger = Logger.DEFAULT
                level = LogLevel.ALL

                logger = object : Logger {
                    override fun log(message: String) {
                        LoggerNative.debug("","AppDebug KtorHttpClient message:$message")
                    }
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    explicitNulls = false
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = true
                    encodeDefaults = true
                    classDiscriminator = "#class"
                })
            }

        }.apply {
            requestPipeline.intercept(HttpRequestPipeline.Before) {
                // Set the Authorization header with the Bearer token
//                val token = appDataStore.readValue(DataStoreKeys.TOKEN) ?: ""
                val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJNb2JpbGVCZmZUZXN0IiwiYXVkIjoiTmV3TW9iaWxlQXBwIiwiZXhwIjoxNzMzNTI5NjAwLCJ1c2VySWQiOjEzOTE2NCwicGhvbmVOdW1iZXIiOiIxODY1NDg4NzA1In0.qaTuIwSWfwPmGY1xa1Coh49d4g0tC9bHnF2_q5cEDcE"
                context.header(HttpHeaders.Authorization, token)
            }
        }
    )
