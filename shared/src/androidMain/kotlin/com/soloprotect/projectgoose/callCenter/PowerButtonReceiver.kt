package com.myprotect.projectx.callCenter

import android.app.Activity
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PowerButtonReceiver(private val callBack: (() -> Unit)) : BroadcastReceiver() {

    companion object {
        private const val WAKE_LOCK_TAG = "myprotectapp:SoloWakelockTag"
    }

    private var powerButtonCount = 0
    private var resetJob: Job? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_SCREEN_OFF || intent.action == Intent.ACTION_SCREEN_ON) {
            powerButtonCount++
            Log.d("PowerButtonReceiver", "Power button was pressed $powerButtonCount times")

            if (powerButtonCount == 3) {
                // Power button was pressed three times
                Log.d("PowerButtonReceiver", "Power button was pressed three times")
                powerButtonCount = 0

                // Launch a coroutine for the wake-up and call action
                CoroutineScope(Dispatchers.Main).launch {
                    wakeUpAndCall(context)
                }
            }

            // Cancel any existing reset job and start a new one
            resetJob?.cancel()
            resetJob = CoroutineScope(Dispatchers.Main).launch {
                delay(3000L) // Wait for 3 seconds
                powerButtonCount = 0
            }
        }
    }

    private fun wakeUpAndCall(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock =
            powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG)
        wakeLock.acquire(500L)
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (keyguardManager.isKeyguardLocked) {
            (context as? Activity)?.apply {
                setTurnScreenOn(true)
                setShowWhenLocked(true)
            }
        }
        callBack.invoke()
        wakeLock.release()
    }
}
