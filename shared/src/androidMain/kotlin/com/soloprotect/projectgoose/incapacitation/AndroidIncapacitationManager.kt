package com.myprotect.projectx.incapacitation

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.myprotect.projectx.common.Sensors
import com.myprotect.projectx.notifications.PermissionUtil

internal class AndroidIncapacitationManager(
    private val context: Context,
    private val permissionUtil: PermissionUtil,
    private val timerManager: TimerManager,
    private val sensors: Sensors
) : Timer(timerManager) {

    override fun initialize() {
        IncapacitationService.initialize(timerManager, sensors)
        timerManager.setCountDownTimer()
    }

    override fun start(isSensor: Boolean) {
        if (isSensor) {
            val intent = Intent(context, IncapacitationService::class.java).apply {
                action = "START"
            }

            permissionUtil.hasLocationPermission {
                ContextCompat.startForegroundService(context, intent)
            }
        } else {
            timerManager.start()
        }
    }

    override fun stop() {
        val intent = Intent(context, IncapacitationService::class.java).apply {
            action = "STOP"
        }
        context.stopService(intent)
    }

    override fun increase() {
        timerManager.increase()
    }

    override fun decrease() {
        timerManager.decrease()
    }

    override fun onTimerCompleted() {
        val intent = Intent(context, IncapacitationService::class.java).apply {
            action = "TIMER_COMPLETED"
        }
        ContextCompat.startForegroundService(context, intent)
    }

    override fun onPreAlertCompleted() {
        val intent = Intent(context, IncapacitationService::class.java).apply {
            action = "PRE_ALERT_COMPLETED"
        }
        ContextCompat.startForegroundService(context, intent)
    }

    override fun onFiveMinLeft() {
        val intent = Intent(context, IncapacitationService::class.java).apply {
            action = "FIVE_MIN_LEFT"
        }
        ContextCompat.startForegroundService(context, intent)
    }

    override fun onOneMinLeft() {
        val intent = Intent(context, IncapacitationService::class.java).apply {
            action = "ONE_MIN_LEFT"
        }

        ContextCompat.startForegroundService(context, intent)
    }
}
