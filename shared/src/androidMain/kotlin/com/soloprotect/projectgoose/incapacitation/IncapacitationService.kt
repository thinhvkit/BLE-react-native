package com.myprotect.projectx.incapacitation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import com.myprotect.projectx.R
import com.myprotect.projectx.common.Constants.NOTIFICATION_ID
import com.myprotect.projectx.common.Logger
import com.myprotect.projectx.common.Sensors
import com.myprotect.projectx.common.fallDetection
import com.myprotect.projectx.notifications.NotifierManager
import get
import kotlinx.datetime.Clock

class IncapacitationService : Service() {

    private var currentTime: Long = 0
    private var lastTime: Long = 0

    private val multipleEventsCutter = MultipleEventsCutter.get()

    companion object {
        private var timerManager: TimerManager? = null
        private var sensors: Sensors? = null
        private var accelerationReaders = ArrayList<Double>()
        private var acceleration = Triple(0.0, 0.0, 0.0)
        private const val CHANNEL_ID = "DEFAULT_NOTIFICATION_CHANNEL_ID"
        private const val CHANNEL_NAME: String = "General"

        fun initialize(
            timerManager: TimerManager,
            sensors: Sensors,
        ) {
            Companion.timerManager = timerManager
            Companion.sensors = sensors

            timerManager.state.value._timeText.watch {
                if (timerManager.state.value.isPlaying) {
                    NotifierManager.getLocalNotifier().notifyIncapacitation(
                        id = NOTIFICATION_ID,
                        title = "myprotect Mobile - now",
                        body = "Incapacitation timer - $it",
                    )
                } else {
                    NotifierManager.getLocalNotifier().remove(NOTIFICATION_ID)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent != null) {
            when (intent.action) {

                "START" -> {
                    timerManager?.start()
                    sensors?.start()
                    sensors?.data?.watch { s ->
                        s?.let {
                            currentTime = Clock.System.now().toEpochMilliseconds()
                            if (currentTime - lastTime > 100) {

                                lastTime = currentTime
                                acceleration = Triple(
                                    (0.5 * it.sensor.x + 0.5 * acceleration.first),
                                    (0.5 * it.sensor.y + 0.5 * acceleration.second),
                                    (0.5 * it.sensor.z + 0.5 * acceleration.third)
                                )
                                if (fallDetection(accelerationReaders, acceleration)) {
                                    accelerationReaders.clear()
                                    acceleration = Triple(0.0, 0.0, 0.0)

                                    Logger.d("ON FALL")
                                    multipleEventsCutter.processEvent(
                                        {
                                            IncapacitationManagerImpl.onFall()
                                        },
                                        duration = 3000L
                                    )
                                }
                            }
                        }
                    }
                }

                "INCREASE" -> timerManager?.increase()
                "DECREASE" -> timerManager?.decrease()
                "STOP" -> {
                    timerManager?.reset()
                    sensors?.stop()

                    stopForeground(STOP_FOREGROUND_DETACH)
                    stopSelf()
                }

                "FIVE_MIN_LEFT" -> {
                    IncapacitationManagerImpl.onFiveMinLeft()
                }

                "ONE_MIN_LEFT" -> {
                    IncapacitationManagerImpl.onOneMinLeft()
                }

                "TIMER_COMPLETED" -> {
                    IncapacitationManagerImpl.onTimerCompleted()
                    sensors?.stop()
                    NotifierManager.getLocalNotifier().remove(NOTIFICATION_ID)
                    stopSelf()

                }

                "PRE_ALERT_COMPLETED" -> {
                    IncapacitationManagerImpl.onPreAlertCompleted()
                }
            }
        }

        startForeground()

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()

        timerManager?.reset()
        if (sensors?.isEnabled == true) {
            accelerationReaders.clear()
            acceleration = Triple(0.0, 0.0, 0.0)
            sensors?.stop()
        }
        stopSelf()
    }


    private fun startForeground() {
        val chan =
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chan)

        val notification: Notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("myprotect Mobile - now")
            .setContentText("Incapacitation service is running")
            .setSmallIcon(R.drawable.app_logo)
            .build()


        startForeground(NOTIFICATION_ID, notification)
    }
}
