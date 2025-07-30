package com.myprotect.projectx.common

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLLocationAccuracyNearestTenMeters
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume

class IOSLocationProvider : LocationService {

    private val locationManager = CLLocationManager()

    @ExperimentalForeignApi
    override suspend fun getCurrentLocation(): LocationData? {
        return suspendCancellableCoroutine { continuation ->
            locationManager.requestWhenInUseAuthorization()
            locationManager.delegate = CurrentLocationDelegate(
                onData = { locationData ->
                    if (!continuation.isCompleted) {
                        continuation.resume(locationData)
                    }
                    locationManager.stopUpdatingLocation()
                },
                onError = { error ->
                    if(!continuation.isCompleted) {
                        continuation.resume(null)
                    }
                    locationManager.stopUpdatingLocation()
                }
            )
            locationManager.desiredAccuracy = kCLLocationAccuracyNearestTenMeters
            locationManager.startUpdatingLocation()
        }
    }
}

@ExperimentalForeignApi
class CurrentLocationDelegate(private  val onData: (LocationData?) -> Unit, private val onError: (Throwable) -> Unit) : NSObject(), CLLocationManagerDelegateProtocol {
    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        try {
            val lastLocation = manager.location
            if (lastLocation != null) {
                val location = LocationData(
                    latitude = lastLocation.coordinate.useContents { latitude },
                    longitude = lastLocation.coordinate.useContents { longitude }
                )
                onData(location)
            } else {
                onData(null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onError(e)
        }
    }
    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
        onError(Throwable(didFailWithError.localizedDescription))
    }
}

@Composable
actual fun getLocationService(): LocationService {
    return IOSLocationProvider()
}
