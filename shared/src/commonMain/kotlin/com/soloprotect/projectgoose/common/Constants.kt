package com.myprotect.projectx.common

internal object Constants {
    const val BASE_URL = "https://sp-tst-uks-mobile-bff-app.azurewebsites.net"
    const val AUTHORIZATION_BEARER_TOKEN = "Bearer "
    const val KEY_ANDROID_FIREBASE_NOTIFICATION = "google.sent_time"
    const val KEY_IOS_FIREBASE_NOTIFICATION = "gcm.message_id"
    const val ACTION_NOTIFICATION_CLICK = "com.myprotect.notification.ACTION_NOTIFICATION_CLICK"

    const val CALL_CENTER = "call.center.action"
    const val RED_ALERT_ACTION = "red.alert.action"
    const val READY_TALK_ACTION = "ready.talk.action"
    const val EXTEND_ACTION = "extend.action"
    const val CANCEL_ACTION = "cancel.action"

    const val MAC_ID = "mac.id"

    const val NOTIFICATION_ID = 11

    const val CALL_SERVICE_START = "CALL_SERVICE_START"
    const val CALL_SERVICE_START_BLUETOOTH_SERVICE = "CALL_SERVICE_START_BLUETOOTH_SERVICE"
    const val CALL_SERVICE_STOP_BLUETOOTH_SERVICE = "CALL_SERVICE_STOP_BLUETOOTH_SERVICE"

    const val SQLITE_DB = "myprotect.db"
    const val LOGGER_TAG = "myprotect"

}

object DataStoreKeys {
    const val TOKEN = "com.myprotect.projectx.TOKEN"
    const val EMAIL = "com.myprotect.projectx.EMAIL"
    const val USER_LANGUAGE = "com.myprotect.projectx.USER_LANGUAGE"
    const val PRIVACY_MODE = "com.myprotect.projectx.PRIVACY_MODE"
    const val USER_PROFILE = "com.myprotect.projectx.USER_PROFILE"
    const val DEVICE_SETUP = "com.myprotect.projectx.DEVICE_SETUP"
    const val DEVICE_SETTINGS = "com.myprotect.projectx.DEVICE_SETTINGS"
    const val SUPPORT_LANGUAGE = "com.myprotect.projectx.SUPPORT_LANGUAGE"
    const val SUPPORT_TIMEZONE = "com.myprotect.projectx.SUPPORT_TIMEZONE"
    const val SUPPORT_COUNTRY = "com.myprotect.projectx.SUPPORT_COUNTRY"
}

internal object ApiEventName {
    const val RED_ALERT_ACTIVATED = "redAlertActivated"
    const val CHECK_IN_ACTIVATED = "amberAlertActivated"
    const val INCAP_ALERT_ACTIVATED = "IncapAlertActivated"
    const val READY2TALK_ACTIVATED = "companionAlertActivated"
    const val WORKING_STATUS = "USER_STATUS_SET"
}
