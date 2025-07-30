package com.myprotect.projectx.presentation.token_manager

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.myprotect.projectx.domain.core.DataState
import com.myprotect.projectx.domain.interactors.authentication.CheckTokenInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TokenManager(
    private val checkTokenInteractor: CheckTokenInteractor,
//    private val logoutInteractor: LogoutInteractor,
) {

    private val sessionScope = CoroutineScope(Dispatchers.Main)


    val state: MutableState<TokenState> = mutableStateOf(TokenState())


    fun onTriggerEvent(event: TokenEvent) {
        when (event) {
            is TokenEvent.CheckToken -> {
                checkToken()
            }

            is TokenEvent.Logout -> {
//                logout()
            }

            else -> {}
        }
    }

    private fun checkToken() {
        checkTokenInteractor.execute().onEach { dataState ->
            when (dataState) {
                is DataState.NetworkStatus -> {}
                is DataState.Response -> {}
                is DataState.Data -> {
                    state.value = state.value.copy(isTokenAvailable = dataState.data ?: false)
                }

                is DataState.Loading -> {}
            }
        }.launchIn(sessionScope)
    }

//    private fun logout() {
//        logoutInteractor.execute().onEach { dataState ->
//            when (dataState) {
//                is DataState.NetworkStatus -> {}
//                is DataState.Response -> {}
//                is DataState.Data -> {
//                    checkToken()
//                }
//
//                is DataState.Loading -> {}
//            }
//        }.launchIn(sessionScope)
//    }

}
