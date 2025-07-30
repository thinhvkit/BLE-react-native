package com.myprotect.projectx.common

import androidx.compose.runtime.Composable

interface LocationService {
    suspend fun getCurrentLocation(): LocationData?
}

@Composable
expect fun getLocationService(): LocationService

data class LocationData(val latitude: Double, val longitude: Double)
