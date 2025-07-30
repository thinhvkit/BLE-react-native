package com.myprotect.projectx.incapacitation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class CountDownTimerHelper @OptIn(DelicateCoroutinesApi::class) constructor(
    private val millisInFuture: Long,
    private val countDownInterval: Long,
    private val scope: CoroutineScope = GlobalScope,
) {
    private var job: Job? = null
    private var remainingTime: Long = 0
    private var isTimerPaused: Boolean = true

    init {
        remainingTime = millisInFuture
    }

    fun increase(value: Long): Long {
        remainingTime += value
        return remainingTime
    }

    fun decrease(value: Long): Long {
        remainingTime -= value
        return remainingTime
    }

    fun start() {
        if (isTimerPaused) {
            job = scope.launch {
                while (remainingTime > 0) {
                    delay(countDownInterval)
                    remainingTime -= countDownInterval
                    onTimerTick(remainingTime)
                }
                onTimerFinish()
                restart()
            }
            isTimerPaused = false
        }
    }

    fun pause() {
        job?.cancel()
        isTimerPaused = true
    }

    fun restart() {
        job?.cancel()
        remainingTime = millisInFuture
        isTimerPaused = true
    }

    abstract fun onTimerTick(millisUntilFinished: Long)
    abstract fun onTimerFinish()
}
