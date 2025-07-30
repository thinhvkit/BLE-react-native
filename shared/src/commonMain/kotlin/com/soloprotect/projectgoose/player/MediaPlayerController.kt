package com.myprotect.projectx.player

abstract class MediaPlayerController {
    abstract fun prepare(pathSource: String, listener: MediaPlayerListener)

    abstract fun prepare(pathSource: String)

    abstract fun start()

    abstract fun pause()

    abstract fun stop()

    abstract fun isPlaying(): Boolean

    abstract fun release()
}
