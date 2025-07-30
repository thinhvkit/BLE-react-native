package com.myprotect.projectx.incapacitation

import com.myprotect.projectx.common.Sensors
import com.myprotect.projectx.common.fallDetection
import kotlinx.datetime.Clock

class IosIncapacitationManager(
    private val timerManager: TimerManager,
    private val sensors: Sensors
) : Timer(timerManager) {

    private var currentTime: Long = 0
    private var lastTime: Long = 0

    private var accelerationReaders = ArrayList<Double>()
    private var acceleration = Triple(0.0, 0.0, 0.0)

    companion object {
        private lateinit var timerManager: TimerManager
        private lateinit var sensors: Sensors

        fun init(timerManager: TimerManager, sensors: Sensors) {
            this.timerManager = timerManager
            this.sensors = sensors

            this.timerManager.state.value._timeText.watch { text ->
                if (timerManager.state.value.isPlaying) {
                    IncapacitationManagerImpl.onUpdated(text)
                }
            }
        }

        fun addListener(onIncapacitation: OnIncapacitation) {
            IncapacitationManager.addListener(onIncapacitation)
        }

        fun increase() {
            this.timerManager.increase()?.let { remainingTime ->
                IncapacitationManagerImpl.onIncrease(remainingTime)
            }
        }

        fun decrease() {
            this.timerManager.decrease()?.let { remainingTime ->
                IncapacitationManagerImpl.onDecrease(remainingTime)
            }
        }

        fun stop() {
            this.timerManager.reset()

            if (sensors.isEnabled) {
                sensors.stop()
            }

            IncapacitationManagerImpl.onStop()
        }
    }

    override fun initialize() {
        init(timerManager, sensors)
        timerManager.setCountDownTimer()
    }

    override fun start(isSensor: Boolean) {
        timerManager.start()
        IncapacitationManagerImpl.onStart(timerManager.state.value.timeInMillis)

        if (isSensor) {
            sensors.start()
            sensors.data.watch { s ->
                s?.let {
                    currentTime = Clock.System.now().toEpochMilliseconds()
                    if (currentTime - lastTime > 100) {

                        lastTime = currentTime

                        acceleration = Triple(
                            (0.5 * it.sensor.x + 0.5 * acceleration.first),
                            (0.5 * it.sensor.y + 0.5 * acceleration.second),
                            (0.5 * it.sensor.z + 0.5 * acceleration.third)
                        )
                        if (fallDetection(accelerationReaders, acceleration)) {
                            accelerationReaders.clear()
                            acceleration = Triple(0.0, 0.0, 0.0)
                            IncapacitationManagerImpl.onFall()
                        }
                    }
                }
            }
        }
    }


    override fun stop() {
        Companion.stop()
    }

    override fun increase() {
        Companion.increase()
    }

    override fun decrease() {
        Companion.decrease()
    }

    override fun onFiveMinLeft() {
        IncapacitationManagerImpl.onFiveMinLeft()
    }

    override fun onOneMinLeft() {
        IncapacitationManagerImpl.onOneMinLeft()
    }

    override fun onTimerCompleted() {
        IncapacitationManagerImpl.onTimerCompleted()
        Companion.sensors.stop()
    }

    override fun onPreAlertCompleted() {
        IncapacitationManagerImpl.onPreAlertCompleted()
    }
}
