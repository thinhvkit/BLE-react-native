package com.myprotect.projectx.haptic

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import com.myprotect.projectx.R
import com.myprotect.projectx.player.MediaPlayerController

class AndroidHapticManager(
    private val context: Context,
    private val mediaPlayerController: MediaPlayerController
) : IHaptic {

    private val vibrator: Vibrator =
        context.getSystemService(Vibrator::class.java)

    private fun createVibrationPattern(oneShotPattern: LongArray, repeat: Int): LongArray {
        val repeatPattern = LongArray(oneShotPattern.size * repeat)
        System.arraycopy(oneShotPattern, 0, repeatPattern, 0, oneShotPattern.size)
        for (count in 0 until repeat) {
            repeatPattern[oneShotPattern.size * count] =
                500 // Delay in ms, change whatever you want for each repetition
            System.arraycopy(
                oneShotPattern,
                1,
                repeatPattern,
                oneShotPattern.size * count + 1,
                oneShotPattern.size - 1
            )
        }
        return repeatPattern
    }

    override fun playHaptic(times: Int, type: HapticType) {
        val pattern = longArrayOf(0, 400)
        val amplitude = if (type == HapticType.SHORT) 155 else 255

        val amplitudes = IntArray(pattern.size * times)
        for (count in 0 until pattern.size * times) {
            if (count % 2 == 0) {
                amplitudes[count] = 0
            } else {
                amplitudes[count] = amplitude
            }
        }

        vibrator.let {
            if (it.hasVibrator()) {

                val patterns = createVibrationPattern(pattern, times)
                val effect =
                    VibrationEffect.createWaveform(
                        patterns,
                        amplitudes,
                        -1
                    )


                it.vibrate(effect)
            }
        }
    }

    override fun playSound(pathSource: String) {
        val path =
            "android.resource://" + context.packageName + "/" + SoundList.SOUND_01.audio
        mediaPlayerController.prepare(path)
    }
}

enum class SoundList {
    SOUND_01 {
        override val audio = R.raw.se_notification
    };

    abstract val audio: Int
}
