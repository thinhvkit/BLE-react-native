package com.myprotect.projectx.callCenter

import android.annotation.SuppressLint
import android.app.Service.TELECOM_SERVICE
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import com.myprotect.projectx.domain.core.AppDataStore
import com.myprotect.projectx.common.Constants
import com.myprotect.projectx.common.Logger

class AndroidCallCenterManager(private val context: Context, appDataStore: AppDataStore) : CallCenter(appDataStore) {

    @SuppressLint("MissingPermission")
    override fun makeCall(number: String) {
        val intent = Intent(context, CallCenterService::class.java).apply {
            action = Constants.CALL_SERVICE_START
        }

        ContextCompat.startForegroundService(context, intent)

        val uri = Uri.parse("tel:$number")
        if(simSlotNumber() > 1) {
            makeCallWithActiveSlot(uri)
        } else {
            val i = Intent(Intent.ACTION_CALL, uri)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            try {
                context.startActivity(i)
            } catch (s: SecurityException) {
                Logger.e("$s")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun makeCallWithActiveSlot(uri: Uri) {
        val subscriptionManager =
            context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val activeSubscriptionInfoList = subscriptionManager.activeSubscriptionInfoList
        val telecomManager = context.getSystemService(TELECOM_SERVICE) as TelecomManager
        val simSlotName = arrayOf(
            "extra_asus_dial_use_dualsim",
            "com.android.phone.extra.slot",
            "slot",
            "simslot",
            "sim_slot",
            "subscription",
            "Subscription",
            "phone",
            "com.android.phone.DialingMode",
            "simSlot",
            "slot_id",
            "simId",
            "simnum",
            "phone_type",
            "slotId",
            "slotIdx"
        )

        val phoneAccountHandleList: List<PhoneAccountHandle> = telecomManager.callCapablePhoneAccounts
        var slotIndex = phoneAccountHandleList.indexOfFirst {
            it.id == activeSubscriptionInfoList.first().subscriptionId.toString()
        }
        if(slotIndex == -1) {
            slotIndex = 0
        }
        val bundle = Bundle()
        for (s in simSlotName) {
            bundle.putInt(s, slotIndex)
        }
        bundle.putParcelable(
            "android.telecom.extra.PHONE_ACCOUNT_HANDLE",
            phoneAccountHandleList[slotIndex]
        )
        telecomManager.placeCall(uri, bundle)
    }

    @SuppressLint("MissingPermission")
    private fun simSlotNumber(): Int {
        val subscriptionManager =
            context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val activeSubscriptionInfoList = subscriptionManager.activeSubscriptionInfoList
        return activeSubscriptionInfoList.size
    }

    companion object {
        private var lastState = TelephonyManager.CALL_STATE_IDLE
        private var isIncoming: Boolean = false

        fun onReceive(intent: Intent?) {
            val state: Int = when (intent?.extras?.getString(TelephonyManager.EXTRA_STATE)) {
                TelephonyManager.EXTRA_STATE_IDLE -> TelephonyManager.CALL_STATE_IDLE
                TelephonyManager.EXTRA_STATE_OFFHOOK -> TelephonyManager.CALL_STATE_OFFHOOK
                TelephonyManager.EXTRA_STATE_RINGING -> TelephonyManager.CALL_STATE_RINGING
                else -> 0
            }
            onCallStateChanged(state)
        }

        private fun onCallStateChanged(state: Int?) {
            if (lastState == state) {
                return
            }

            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    isIncoming = true
                    CallCenterManagerImpl.onIncomingCallRinging()
                }

                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    if (!isIncoming) {
                        CallCenterManagerImpl.onOutgoingCallAccepted()
                    } else {
                        CallCenterManagerImpl.onIncomingCallAccepted()
                    }
                }

                TelephonyManager.CALL_STATE_IDLE -> {
                    if (isIncoming) {
                        CallCenterManagerImpl.onIncomingCallEnded()
                    } else {
                        CallCenterManagerImpl.onOutgoingCallEnded()
                    }
                    isIncoming = false
                }
            }

            lastState = state as Int
        }
    }
}
