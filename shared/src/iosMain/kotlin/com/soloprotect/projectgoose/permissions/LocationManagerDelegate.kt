package com.myprotect.projectx.permissions

import com.myprotect.projectx.common.LocationData
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.Foundation.NSError
import platform.darwin.NSObject

internal class LocationManagerDelegate(
    private val onData: ((LocationData?) -> Unit)? = null,
    private val onError: ((Throwable) -> Unit)? = null
) : NSObject(), CLLocationManagerDelegateProtocol {
    private var callback: ((CLAuthorizationStatus) -> Unit)? = null

    private val locationManager = CLLocationManager()

    init {
        locationManager.delegate = this
    }

    fun requestLocationAccess(callback: (CLAuthorizationStatus) -> Unit) {
        this.callback = callback

        locationManager.requestWhenInUseAuthorization()
    }

    override fun locationManager(
        manager: CLLocationManager,
        didChangeAuthorizationStatus: CLAuthorizationStatus
    ) {
        callback?.invoke(didChangeAuthorizationStatus)
        callback = null
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        val lastLocation = manager.location
        if (lastLocation != null) {
            val location = LocationData(
                latitude = lastLocation.coordinate.useContents { latitude },
                longitude = lastLocation.coordinate.useContents { longitude }
            )
            onData?.invoke(location)
        } else {
            onData?.invoke(null)
        }
    }

    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
        onError?.invoke(Throwable(didFailWithError.localizedDescription))
    }
}
