package com.myprotect.projectx.domain.interactors.events

import com.myprotect.api.apis.myprotectMobileBffApiApi
import com.myprotect.api.models.BatteryInfo
import com.myprotect.api.models.CellSignalInfo
import com.myprotect.api.models.CreateEventRequest
import com.myprotect.api.models.DeviceInfo
import com.myprotect.api.models.DeviceStatusInfo
import com.myprotect.api.models.EventData
import com.myprotect.api.models.EventInfo
import com.myprotect.api.models.GnssSignalInfo
import com.myprotect.api.models.GpsInfo
import com.myprotect.api.models.LocationInfo
import com.myprotect.api.models.MobileEvent
import com.myprotect.api.models.UserInfo
import com.myprotect.api.models.UserStatus
import com.myprotect.projectx.domain.core.AppDataStore
import com.myprotect.projectx.domain.core.DataState
import com.myprotect.projectx.domain.core.ProgressBarState
import com.myprotect.projectx.domain.models.event.WorkingStatus
import com.myprotect.projectx.common.LocationData
import com.myprotect.projectx.common.Logger
import com.myprotect.projectx.common.getDeviceIdentifier
import com.myprotect.projectx.common.randomUUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class CreateEventInteractor(
    private val apiClient: myprotectMobileBffApiApi,
    private val appDataStore: AppDataStore
) {
    fun execute(eventName: String,
                workingStatus: WorkingStatus? = null,
                date: LocalDateTime? = null,
                locationData: LocationData? = null,
                battery: Int? = null): Flow<DataState<Boolean>> = flow {

        try {

            emit(DataState.Loading(progressBarState = ProgressBarState.ButtonLoading))
            delay(2000)

            val request = makeCreateEventRequest(eventName, workingStatus, date, locationData, battery)

            val response = apiClient.createEvent(request)

            if(response.success) {
                emit(DataState.Data(true))
            } else {
                emit(DataState.Data(false))
            }

            emit(DataState.Data(true))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.Data(false))
        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }


    }

    private fun makeCreateEventRequest(
        eventName: String,
        workingStatus: WorkingStatus? = null,
        date: LocalDateTime? = null,
        locationData: LocationData? = null,
        battery: Int? = null
    ): CreateEventRequest {
        return CreateEventRequest(
            event = MobileEvent(
                event = EventInfo(
                    eventData = EventData(),
                    eventName = eventName,
                    eventTime = Clock.System.now(),
                    eventId = randomUUID()
                ),
                shareLocation = appDataStore.privacyMode,
                deviceInfo = DeviceInfo(
                    imei = getDeviceIdentifier(),
                    deviceConfigurationCheckSum = "N/A",
                    deviceFirmwareRevision = "N/A",
                ),
                deviceStatus = DeviceStatusInfo(
                    location = LocationInfo(
                        shareLocation = appDataStore.privacyMode,
                        gps = if(appDataStore.privacyMode) GpsInfo(
                            latitude = locationData?.latitude,
                            longitude = locationData?.longitude,
                            gnssSignal = GnssSignalInfo(
                                gpsStatus = "Mobile GPS", fixType = "Mobile GPS",
                            )
                        ) else null,
                    ),
                    cellularSignal = CellSignalInfo(),
                    battery = BatteryInfo(
                        currentCharge = battery?.toDouble(),
                    )
                ),
                user = UserInfo(
                    pinCode = "N/A",
                    nfcCardId = "N/A",
                    status = workingStatus?.run {
                        listOf(
                            UserStatus(
                                userStatusId = workingStatus.id.toLong(),
                                statusCode = workingStatus.name,
                                checked = true,
                                expectedReturnDate = date?.toInstant(TimeZone.currentSystemDefault())
                            )
                        )
                    }
                )
            )
        ).apply { Logger.d("$this", "CreateEventRequest", ) }
    }
}
