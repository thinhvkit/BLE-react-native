# Protect Kotlin Multiplatform app

This is a Protect Kotlin Multiplatform app for Android and iOS. It includes shared business logic and data handling, and a shared UI implementation using Compose Multiplatform.

### Technologies

The app uses the following multiplatform dependencies in its implementation:

- [Compose Multiplatform](https://jb.gg/compose) for UI
- [Compose navigation](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-navigation-routing.html) uses a navigation graph to manage your app's navigation
- [Material3](https://jb.gg/compose) build compose UI with Material design
- [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines) Asynchronous or non-blocking programming
- [Datastore](https://developer.android.com/kotlin/multiplatform/datastore) stores data asynchronously consistently, and transactionally
- [Ktor](https://ktor.io/) for networking
- [Coil](https://github.com/coil-kt/coil) Image loading for Android and Compose Multiplatform
- [Koin](https://github.com/InsertKoinIO/koin) for dependency injection
- [Kotest](https://github.com/kotest/kotest) for multi-platform test framework

### Architecture
The Jetpack Compose Multiplatform myproject Application is built using the Clean Architecture and the MVVM (Model-View-ViewModel) pattern.


<img width="500" src="https://bitbucket.org/repo/G8Rnqoo/images/2236727536-Screenshot%202024-07-18%20at%2012.49.02.png"></img>

### Package Structure

```
├── androidMain
│   ├── AndroidManifest.xml
│   └── kotlin
│       └── com
│           └── myproject
│               └── projectx
│                   ├── ComposeFileProvider.kt
│                   ├── common
│                   │   ├── ChangeStatusBarColors.kt
│                   │   ├── Context.kt
│                   │   ├── DataStore.kt
│                   │   ├── PermissionsManager.android.kt
│                   │   └── Platform.android.kt
│                   ├── di
│                   │   └── PlatformModule.android.kt
│                   ├── extensions
│                   │   ├── ContextExt.kt
│                   │   └── NotifierManagerExt.kt
│                   ├── firebase
│                   │   ├── FirebasePushNotifierImpl.kt
│                   │   └── MyFirebaseMessagingService.kt
│                   ├── main.android.kt
│                   └── notifications
│                       ├── AndroidMockPermissionUtil.kt
│                       ├── AndroidNotifier.kt
│                       ├── AndroidPermissionUtil.kt
│                       ├── NotificationChannelFactory.kt
│                       └── SystemBroadcastReceiver.kt
├── commonMain
│   ├── sqldelight
│   ├── composeResources
│   │   ├── drawable
│   │   ├── font
│   │   └── values
│   │       └── strings.xml
│   └── kotlin
│       └── com
│           └── myproject
│               └── projectx
│                   ├── business
│                   │   ├── constants
│                   │   ├── core
│                   │   │   ├── AppDataStore.kt
│                   │   │   ├── AppDataStoreManager.kt
│                   │   │   ├── DataState.kt
│                   │   │   ├── KtorHttpClient.kt
│                   │   │   ├── NetworkState.kt
│                   │   │   ├── ProgressBarState.kt
│                   │   │   ├── Queue.kt
│                   │   │   ├── UIComponent.kt
│                   │   │   ├── UIComponentState.kt
│                   │   │   └── UIMessage.kt
│                   │   ├── datasource
│                   │   │   └── network
│                   │   ├── interactors
│                   │   │   └── authentication
│                   │   │       ├── CheckTokenInteractor.kt
│                   │   │       ├── LoginInteractor.kt
│                   │   │       └── LogoutInteractor.kt
│                   │   │   └── event
│                   │   │       ├── CreateEvenInteractor.kt
│                   │   │   └── language
│                   │   │       ├── FetchLanguageInteractor.kt
│                   │   │       ├── GelAllLanguageInteractor.kt
│                   │   │   └── profile
│                   │   │       ├── GetUserProfileInteractor.kt
│                   │   │       ├── UpdateUserProfileInteractor.kt
│                   │   │   └── risk_message
│                   │   │       ├── GetRiskMessagesInteractor.kt
│                   │   │       ├── UpdateRiskMessageInteractor.kt
│                   │   └── util
│                   │       ├── Logger.kt
│                   │       └── handleUseCaseException.kt
│                   ├── common
│                   │   ├── CommonUtil.kt
│                   │   ├── Constants.kt
│                   │   ├── Context.kt
│                   │   ├── DataStore.kt
│                   │   ├── PermissionHandler.kt
│                   │   ├── PermissionStatus.kt
│                   │   ├── PermissionType.kt
│                   │   ├── PermissionsManager.kt
│                   │   └── Platform.kt
│                   ├── di
│                   │   ├── Koin.kt
│                   │   └── PlatformModule.kt
│                   ├── notifications
│                   │   ├── Notifier.kt
│                   │   ├── NotifierManager.kt
│                   │   ├── NotifierManagerImpl.kt
│                   │   └── PushNotifier.kt
│                   └── presentation
│                       ├── App.kt
│                       ├── component
│                       │   ├── AppTopBar.kt
│                       │   ├── Buttons.kt
│                       ├── navigation
│                       │   ├── AppNavHost.kt
│                       │   ├── AppNavigation.kt
│                       │   └── LoginNavigation.kt
│                       ├── theme
│                       │   ├── Color.kt
│                       │   ├── Shape.kt
│                       │   ├── Theme.kt
│                       │   └── Type.kt
│                       ├── token_manager
│                       │   ├── TokenEvent.kt
│                       │   ├── TokenManager.kt
│                       │   └── TokenState.kt
│                       └── ui
│                           ├── login
│                           │   ├── LoginNav.kt
│                           │   ├── step1
│                           │   │   ├── LoginEmailScreen.kt
│                           │   │   └── view_model
│                           │   │       ├── LoginEmailEvent.kt
│                           │   │       ├── LoginEmailState.kt
│                           │   │       └── LoginEmailViewModel.kt
│                           │   ├── step2
│                           │   │   ├── LoginPhoneScreen.kt
│                           │   │   └── view_model
│                           │   │       ├── LoginPhoneEvent.kt
│                           │   │       ├── LoginPhoneState.kt
│                           │   │       └── LoginPhoneViewModel.kt
│                           │   ├── step3
│                           │   │   ├── LoginPhoneOTPScreen.kt
│                           │   │   └── view_model
│                           │   │       ├── LoginPhoneOTPEvent.kt
│                           │   │       ├── LoginPhoneOTPState.kt
│                           │   │       └── LoginPhoneOTPViewModel.kt
│                           ├── onboarding
│                           │   ├── OnboardingNav.kt
│                           │   ├── language_and_timezone
│                           │   │   ├── LanguageAndTimezoneScreen.kt
│                           │   │   └── view_model
│                           │   │       ├── LanguageAndTimezoneEvent.kt
│                           │   │       ├── LanguageAndTimezoneState.kt
│                           │   │       └── LanguageAndTimezoneViewModel.kt
│                           ├── main
│                           │   ├── MainNav.kt
│                           │   └── view_model
│                           │       ├── MainEvent.kt
│                           │       ├── MainState.kt
│                           │       └── MainViewModel.kt
│                           ├── navigation_drawer
│                           │   ├── NavDrawer.kt
│                           │   └── view_model
│                           │       ├── NavigationDrawerEvent.kt
│                           │       ├── NavigationDrawerState.kt
│                           │       └── NavigationDrawerViewModel.kt
│                           ├── permission
│                           │   ├── PermissionScreen.kt
│                           │   └── view_model
│                           │       ├── PermissionEvent.kt
│                           │       ├── PermissionState.kt
│                           │       └── PermissonViewModel.kt
│                           └── splash
│                               └── SplashScreen.kt
├── iosMain
│   └── kotlin
│       └── com
│           └── myproject
│               └── projectx
│                   ├── common
│                   │   ├── BluetoothPermissionDelegate.kt
│                   │   ├── Context.kt
│                   │   ├── DataStore.kt
│                   │   ├── HapticManager.kt
│                   │   ├── LocationManagerDelegate.kt
│                   │   ├── LocationPermissionDelegate.kt
│                   │   ├── PermissionDelegate.kt
│                   │   ├── PermissionsManager.ios.kt
│                   ├── di
│                   │   └── PlatformModule.ios.kt
│                   ├── extensions
│                   │   └── NotifierManagerExt.kt
│                   ├── firebase
│                   │   └── FirebasePushNotifierImpl.kt
│                   ├── main.ios.kt
│                   └── notifications
│                       ├── IosNotifier.kt
│                       └── IosPermissionUtil.kt
```


### Protect Mobile setup guide.

### Check your environment
 ```brew install kdoctor```
  After the installation is completed, call KDoctor in the console: ```kdoctor```

  [Kotlin Multiplatform Setup](https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-setup.html)

### Java
**required** 
Kotlin 2.0.20. it is fully compatible with Gradle 6.8.3 through 8.6. Gradle 8.7 and 8.8 are also supported, with only one exception: If you use the Kotlin Multiplatform Gradle plugin, you may see deprecation warnings in your multiplatform projects calling the withJava() function in the JVM target.

Java 8, JDK 15+ 

Check the Java's been added correctly. 
```echo $JAVA_HOME``` example: */Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home*


#### ANDROID: 
  
```./gradle clean```  
```./gradlew assemble{Debug/Release}```  

#### IOS:
  
**Cocoapods:** used to manage the dependencies for our iOS application  
**Xcode:** Xcode Command Line Tools install ```xcode-select --install```  
**iOS profilesioning profiles:** will allow developers to run their apps on an iOS device  
There are 4 different types of provisioning proles. They are Development, App Store, Ad-Hoc, and Enterprise.  
They are used for different purposes, and should be only used for those purposes  

make sure ```podfile = project.file("../iosApp/Podfile")``` is commented  

```./gradle clean```  
```./gradlew iosSimulatorArm64Binaries```  
'pod install' should be executed after running ':generateDummyFramework' Gradle task:  
```./gradlew :shared:generateDummyFramework```  
```cd iosApp``` and then ```pod install```  
```xcodebuild -workspace iosApp.xcworkspace -scheme iosApp -configuration Debug -sdk iphoneos```  

**NOTE:** iOS only runs on Real Device because it includes MTBeaconPlus FrameWork. If you try to run on Simulator Xcode will through an error "Building for 'iOS-simulator', but linking in dylib (~/iosApp/MTBeaconPlus.framework/MTBeaconPlus) built for 'iOS'"


### Features

- **Login:** Launching the app with an email address.
- **Emergency Alert:** 24/7 emergency panic alarm, designed to quickly and discreetly get help to your lone workers at the press of a button.
- **Check-In:** Allows a user to record a short message, providing information about their location and activity to inform any subsequent alerts..
- **TalkLive:** Support workers in situations where there’s a clear safety risk.
- **Warning Alert:** Automatically raise an warning alert if you suffer a slip, trip or fall.
	- Android: [Foreground Service](https://developer.android.com/develop/background-work/services/foreground-services)
	- iOS: [Live Activity](https://developer.apple.com/documentation/activitykit/displaying-live-data-with-live-activities)
	
- **Working Status:** Allow you specific whether you're available.
- **Setting Menu:** Change your device settings.
- **Change App Language:** Change your app's display language.
- **Notifications:** Receive an alert to their mobile phones containing the message and the option to confirm acknowledgement
- **Device Setup:** Initiate a Red Alert by quick pressing the device button 3 times.
	- Android: Foreground Service will start BLE scan every 5 minutes when Device Setup is opened. (sleep mode)
	- iOS: WIP

