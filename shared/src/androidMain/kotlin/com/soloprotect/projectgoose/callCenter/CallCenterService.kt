package com.myprotect.projectx.callCenter

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.myprotect.projectx.R
import com.myprotect.projectx.ble.AndroidBLEManager
import com.myprotect.projectx.common.Constants
import com.myprotect.projectx.common.Logger
import com.myprotect.projectx.haptic.HapticManagerImpl
import com.myprotect.projectx.haptic.HapticType
import com.myprotect.projectx.haptic.IHaptic
import com.myprotect.projectx.notifications.AndroidNotifier
import com.myprotect.projectx.notifications.NotifierManager


class CallCenterService : Service() {

    private lateinit var tm: TelephonyManager

    private val callTimer = object : CountDownTimer(10000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            // do not anything
        }

        override fun onFinish() {

            val isCalling = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.READ_PHONE_STATE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }

                tm.callStateForSubscription != TelephonyManager.CALL_STATE_IDLE

            } else {
                tm.callState != TelephonyManager.CALL_STATE_IDLE
            }
            if (isCalling) {
                haptic?.playHaptic(2, HapticType.SHORT)
                start()
            } else {
                cancel()
            }
        }
    }

    private val callCenterReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Constants.RED_ALERT_ACTION -> {
                    callCenter?.makeRedAlertCall()
                }

                Constants.READY_TALK_ACTION -> {
                    callCenter?.makeReady2TalkCall()
                }
            }
        }
    }

//    start bluetooth button scan every 5 minutes
//    private val bleTimer = object : CountDownTimer(300000, 1000) {
//        override fun onTick(p0: Long) {
//            // do not anything
//        }
//
//        @RequiresApi(Build.VERSION_CODES.S)
//        override fun onFinish() {
//            macId.isNotBlank().let {
//                bleManager?.startScan(macId)
//            }
//        }
//    }

    @SuppressLint("MissingPermission")
    val powerButtonReceiver = PowerButtonReceiver(callBack = {
        callCenter?.makeRedAlertCall()
    })

    companion object {
        private var NOTIFICATION_ID = 10
        private var haptic: IHaptic? = null
        private var callCenter: CallCenter? = null
        private var macId: String = ""
        private var bleManager: AndroidBLEManager? = null
        private val callStateListener = object : CallCenterManager.Listener {
            override fun onCallStarted() {
                Logger.d("onCallStarted")
                haptic?.playHaptic(3)
            }

            override fun onIncomingCallAcceptedNotifyObservers() {

            }

            override fun onIncomingCallEndedNotifyObservers() {
                Logger.d("onIncomingCallEnded")
                haptic?.playHaptic(2, HapticType.LONG)
            }

            override fun onOutgoingCallAcceptedNotifyObservers() {
                Logger.d("onOutgoingCallAccepted")
            }

            override fun onOutgoingCallEndedNotifyObservers() {
                Logger.d("onOutgoingCallEnded")
                callCenter?.onCallEnded()
                haptic?.playHaptic(2, HapticType.LONG)
            }

            override fun onIncomingCallRinging() {
                Logger.d("onIncomingCallRinging")
            }
        }

        fun startCall() {
            haptic = HapticManagerImpl.getHapticManager()

            CallCenterManager.addListener(callStateListener)
            CallCenterManagerImpl.onCallStarted()
        }

        fun setMacId(value: String) {
            macId = value
        }

        fun setBLEManager(value: AndroidBLEManager) {
            bleManager = value
        }
    }

    override fun onBind(p0: Intent?): IBinder? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        tm = application.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        callCenter = CallCenterManager.getCallCenter()

        if (intent != null) {
            when (intent.action) {
                Constants.CALL_SERVICE_START -> {
                    callTimer.start()
                    startCall()
                }

                Constants.CALL_SERVICE_START_BLUETOOTH_SERVICE -> {
//                    bleTimer.start()
                    intent.extras?.getString(Constants.MAC_ID)?.let { setMacId(it) }
                }

                Constants.CALL_SERVICE_STOP_BLUETOOTH_SERVICE -> {
//                    bleTimer.cancel()
                    setMacId("")
                }
            }
        }

        startForeground()

        val notiActionFilter = IntentFilter().apply {
            addAction(Constants.RED_ALERT_ACTION)
            addAction(Constants.READY_TALK_ACTION)
        }
        registerReceiver(callCenterReceiver, notiActionFilter, RECEIVER_EXPORTED)

        val pwFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
        }
        registerReceiver(powerButtonReceiver, pwFilter)

        return START_NOT_STICKY
    }

    override fun onDestroy() {

        unregisterReceiver(powerButtonReceiver)
        unregisterReceiver(callCenterReceiver)

        setMacId("")

        callTimer.cancel()

//        bleTimer.cancel()

        Logger.d("CallCenterService onDestroy")

    }

    private fun startForeground() {
        val notification = (NotifierManager.getLocalNotifier() as AndroidNotifier)
            .createRedAlertNotification(
                title = getString(R.string.keeping_you_safe),
                body = "",
                payloadData = emptyMap()
            )
        startForeground(
            NOTIFICATION_ID,
            notification,
        )
    }
}
