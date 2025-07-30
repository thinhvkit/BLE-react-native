package com.myprotect.projectx.player

import com.myprotect.projectx.common.Logger
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.addPeriodicTimeObserverForInterval
import platform.AVFoundation.currentItem
import platform.AVFoundation.isPlaybackLikelyToKeepUp
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.removeTimeObserver
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.AVFoundation.timeControlStatus
import platform.CoreMedia.CMTime
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.darwin.NSEC_PER_SEC

@OptIn(ExperimentalForeignApi::class)
class IosMediaPlayerController: MediaPlayerController() {

    private lateinit var timeObserver: Any

    private val player: AVPlayer = AVPlayer()

    private var listener: MediaPlayerListener? = null

    init {
        setUpAudioSession()
    }

    override fun prepare(pathSource: String, listener: MediaPlayerListener) {
        this.listener = listener
        val url = NSURL(string = pathSource)
        stop1()
        startTimeObserver()
        player.replaceCurrentItemWithPlayerItem(AVPlayerItem(url))
        player.play()
    }

    override fun prepare(pathSource: String){
        val url = NSURL(fileURLWithPath = pathSource)
        stop1()
        startTimeObserver()
        player.replaceCurrentItemWithPlayerItem(AVPlayerItem(url))
        player.play()
    }

    fun setUpAudioSession() {
        try {
            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setCategory(AVAudioSessionCategoryPlayback, null)
            audioSession.setActive(true, null)
        } catch (e: Exception) {
            Logger.e("Error setting up audio session: ${e.message}")
        }
    }

    private val observer: (CValue<CMTime>) -> Unit = { time: CValue<CMTime> ->
        if (player.currentItem?.isPlaybackLikelyToKeepUp() == true) {
            listener?.onReady()
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun startTimeObserver() {
        val interval = CMTimeMakeWithSeconds(1.0, NSEC_PER_SEC.toInt())
        timeObserver = player.addPeriodicTimeObserverForInterval(interval, null, observer)
        NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = player.currentItem,
            queue = NSOperationQueue.mainQueue,
            usingBlock = {
                listener?.onAudioCompleted()
            }
        )
    }

    override fun start() {
        player.play()
    }

    override fun pause() {
        player.pause()
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun stop() {
        player.run {
            pause()
            seekToTime(time = cValue {
                value = 0
            })
        }
    }

    private fun stop1() {
        if (::timeObserver.isInitialized) player.removeTimeObserver(timeObserver)
        player.pause()
        player.currentItem?.seekToTime(CMTimeMakeWithSeconds(0.0, NSEC_PER_SEC.toInt()))
    }


    override fun isPlaying(): Boolean {
        return this.player.timeControlStatus == AVPlayerTimeControlStatusPlaying
    }

    override fun release() {
        observer.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
    }
}
