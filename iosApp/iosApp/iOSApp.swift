import SwiftUI
import shared
import FirebaseCore
import FirebaseMessaging
import CallKit

let sharedCallController: CXCallController = CXCallController(queue: .main)

class AppDelegate: NSObject, UIApplicationDelegate {
    
    var callController = sharedCallController
    
    var haptic: IHaptic? = nil
    
    var timer = Timer()
    
    var isCalling = false
    
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        FirebaseApp.configure()
        
        return true
    }
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
        
    }
    
    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable : Any]) async -> UIBackgroundFetchResult {
        NotifierManager.shared.onApplicationDidReceiveRemoteNotification(userInfo: userInfo)
        return UIBackgroundFetchResult.newData
    }
    
    func application(_ application: UIApplication, willFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        IosIncapacitationManager.Companion.shared.addListener(onIncapacitation: self)
        IOSBLEManager.Companion.shared.addListener(listener: self)
        
        callController.callObserver.setDelegate(self, queue: nil)
        
        return true
    }
}

extension AppDelegate: OnIncapacitation {
    
    func onFall() {
        
    }
    
    func onFiveMinLeft() {
        
    }
    
    func onOneMinLeft() {
        
    }
    
    func onPreAlertCompleted() {
        
    }
    
    func onStart(timestamp: Int64) {
        Incapacitation.shared.startLiveActivity(Double(timestamp/1000))
    }
    
    func onStop() {
        Incapacitation.shared.stopLiveActivity()
    }
    
    func onUpdated(timestamp: String){
        
    }
    
    func onIncrease(timestamp: Int64) {
        Incapacitation.shared.update(Double(timestamp/1000))
        
    }
    
    func onDecrease(timestamp: Int64) {
        Incapacitation.shared.update(Double(timestamp/1000))
    }
    
    func onTimerCompleted() {
        Incapacitation.shared.stopLiveActivity()
    }
}

extension AppDelegate: BLEDelegate {
    func connect(macId: String) {
        BLEController.shared.connectDevice(macId: macId)
    }
    
    func disconnect(macId: String) {
        BLEController.shared.disconnect(macId: macId)
    }
    
    func startScan(macId: String?) {
        BLEController.shared.startScan(macId: macId)
    }
    
    func stopScan() {
        BLEController.shared.stopScan()
    }
    
}

extension AppDelegate: CXCallObserverDelegate {
    
    func callObserver(_ callObserver: CXCallObserver, callChanged call: CXCall) {
        
        if(haptic == nil){
            haptic = HapticManager.shared.getHaptic()
        }
        
        if (!call.hasConnected && !call.hasEnded) {
            if(!call.isOutgoing){
                Logger.shared.d(message: "incoming call", tag: "")
            } else {
                Logger.shared.d(message: "outgoing call", tag: "")
                isCalling = true
                haptic?.playHaptic(times: 3, type: HapticType.long_)
                startTimer()
            }
        }
        else if(call.hasConnected && !call.hasEnded && !call.isOnHold){
            Logger.shared.d(message: "call started", tag: "")
            isCalling = true
        }
        else if call.hasEnded == true {
            Logger.shared.d(message: "call ended", tag: "")
            isCalling = false
            CallCenterManager.shared.getCallCenter().onCallEnded()
            haptic?.playHaptic(times: 2, type: HapticType.long_)
            endTimer()
        }
    }
    
    @objc func updateTime() {
        
        if isCalling {
            haptic?.playHaptic(times: 2, type: HapticType.long_)
        } else {
            endTimer()
        }
    }
    
    
    func startTimer() {
        timer = Timer.scheduledTimer(timeInterval: 10.0, target: self, selector: #selector(updateTime), userInfo: nil, repeats: true)
    }
    
    func endTimer() {
        timer.invalidate()
    }
}


extension Date {
    func currentTimeMillis() -> Int64 {
        return Int64(self.timeIntervalSince1970 * 1000)
    }
}

@main
struct iOSApp: App {
    
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    var body: some Scene {
        WindowGroup {
            ContentView().onOpenURL(perform: { url in
                Logger.shared.d(message: url.absoluteString, tag: "")
                handleIncomingURL(url)
            })
            
        }
    }
    
    func handleIncomingURL(_ url: URL) {
        guard url.scheme == "myprotect" else {
            return
        }
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else {
            Logger.shared.d(message: "Invalid URL", tag: "")
            return
        }
        
        
        switch components.host {
        case "red-alert":
            
            CallCenterManager.shared.getCallCenter().makeRedAlertCall()
            return
        case "ready2talk":
            
            CallCenterManager.shared.getCallCenter().makeReady2TalkCall()
            return
        default:
            return
        }
        
    }
    
}
