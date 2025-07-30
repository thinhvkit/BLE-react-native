package com.myprotect.projectx.callCenter

import com.myprotect.projectx.domain.core.AppDataStoreManager
import com.myprotect.projectx.common.Logger
import com.myprotect.projectx.permissions.isSimulator
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class IosCallCenterManager(appDataStore: AppDataStoreManager) : CallCenter(appDataStore) {

    override fun makeCall(number: String) {

        if (isSimulator()) {
            Logger.d("makeAPhoneCall")
            return
        }
        NSURL.URLWithString("tel://$number")?.let {
            UIApplication.sharedApplication.openURL(it)
        }
    }
}
