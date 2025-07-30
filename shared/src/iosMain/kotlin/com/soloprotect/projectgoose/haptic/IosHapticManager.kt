package com.myprotect.projectx.haptic

import com.myprotect.projectx.common.UIDispatcher
import com.myprotect.projectx.player.IosMediaPlayerController
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import platform.AVFAudio.AVAudioPlayer
import platform.AudioToolbox.AudioServicesPlaySystemSound
import platform.AudioToolbox.kSystemSoundID_Vibrate
import platform.Foundation.NSBundle
import platform.Foundation.NSTimer
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

class IosHapticManager(private val mediaPlayerController: IosMediaPlayerController) : IHaptic {

    override fun playHaptic(times: Int, type: HapticType) {
        if (type == HapticType.SHORT) {
            val feedbackGenerator =
                UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy)
            val scope = CoroutineScope(UIDispatcher())
            scope.launch {
                delay(1000)
                for (i in 1..times) {
                    feedbackGenerator.impactOccurredWithIntensity(1.0)
                    delay(1000)
                }
            }
        } else {
            var count = times
            NSTimer.scheduledTimerWithTimeInterval(1.0, true) {
                if (count > 0) {
                    count--
                    AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)

                } else {
                    it?.invalidate()
                }
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun playSound(pathSource: String) {

        val urlPath = NSBundle.mainBundle().URLForResource("se_notification", "mp3")
        mediaPlayerController.setUpAudioSession()
        urlPath?.let {
            val audioPlayer = AVAudioPlayer(contentsOfURL = urlPath, error = null)
            audioPlayer.prepareToPlay()
            audioPlayer.play()
        }
    }
}
