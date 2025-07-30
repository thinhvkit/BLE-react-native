import android.app.Application
import android.telephony.TelephonyManager
import androidx.compose.runtime.Composable
import com.myprotect.projectx.callCenter.AndroidCallCenterManager
import com.myprotect.projectx.common.Constants
import com.myprotect.projectx.notifications.NotifierManagerImpl
import com.myprotect.projectx.SystemBroadcastReceiver
import com.myprotect.projectx.presentation.App

@Composable
fun MainView(application: Application) {
    App(application)

    SystemBroadcastReceiver(systemAction = Constants.EXTEND_ACTION) {
        NotifierManagerImpl.onNotificationActionClicked(Constants.EXTEND_ACTION)

    }
    SystemBroadcastReceiver(systemAction = Constants.CANCEL_ACTION) {
        NotifierManagerImpl.onNotificationActionClicked(Constants.CANCEL_ACTION)
    }

    SystemBroadcastReceiver(systemAction = TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
        AndroidCallCenterManager.onReceive(it)
    }
}


