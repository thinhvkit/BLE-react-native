package com.myprotect.projectx.domain.core

sealed class NetworkState{

   data object Good: NetworkState()

   data object Failed: NetworkState()

}
