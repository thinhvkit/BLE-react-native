package com.myprotect.projectx.incapacitation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.myprotect.projectx.domain.core.UIComponentState
import com.myprotect.projectx.extensions.convertTime
import com.myprotect.projectx.extensions.formatTime
import kotlinx.coroutines.CoroutineScope
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class TimerManager {

    val state: MutableState<TimerState> = mutableStateOf(TimerState())
    val preAlertState: MutableState<TimerState> = mutableStateOf(TimerState())

    private var countDownTimerHelper: CountDownTimerHelper? = null
    private var preAlertCountDownTimerHelper: CountDownTimerHelper? = null

    private var scope: CoroutineScope? = null

    companion object {
        const val HOURS = 0
        const val MINUTES = 0
        const val SECONDS = 20
        private const val DIALOG_SECONDS = 10
        val adjDuration =
            (HOURS.hours.inWholeMilliseconds + MINUTES.minutes.inWholeMilliseconds + SECONDS.seconds.inWholeMilliseconds)
    }

    init {
        setHour(HOURS)
        setMinute(MINUTES)
        setSecond(SECONDS)
    }

    fun setScope(scope: CoroutineScope) {
        this.scope = scope
    }

    fun setHour(hour: Int) {
        state.value = state.value.copy(hour = hour)
    }

    fun setMinute(minute: Int) {
        state.value = state.value.copy(minute = minute)
    }

    fun setSecond(second: Int) {
        state.value = state.value.copy(second = second)
    }

    fun setCountDownTimer() {
        countDownTimerHelper?.restart()
        state.value =
            state.value.copy(timeInMillis = (state.value.hour.hours + state.value.minute.minutes + state.value.second.seconds).inWholeMilliseconds)

        handleTimerValues(false, state.value.timeInMillis.formatTime(), 0f, state)

        countDownTimerHelper =
            object : CountDownTimerHelper(state.value.timeInMillis, 1000) {
                override fun onTimerTick(millisUntilFinished: Long) {
                    val progressValue = millisUntilFinished.toFloat() / state.value.timeInMillis

                    handleTimerValues(true, millisUntilFinished.formatTime(), progressValue, state)

                    when (millisUntilFinished.formatTime()) {
                        "00:05:00" -> IncapacitationManager.getTimer().onFiveMinLeft()
                        "00:01:00" -> IncapacitationManager.getTimer().onOneMinLeft()
                    }
                }

                override fun onTimerFinish() {
                    IncapacitationManager.getTimer().onTimerCompleted()
                    IncapacitationManager.getTimer().stop()
                    reset()
                }
            }
    }

    private fun setPreAlertCountDownTimer() {
        preAlertCountDownTimerHelper?.restart()
        preAlertState.value =
            preAlertState.value.copy(timeInMillis = (preAlertState.value.hour.hours + preAlertState.value.minute.minutes + preAlertState.value.second.seconds).inWholeMilliseconds)

        handleTimerValues(false, preAlertState.value.timeInMillis.formatTime(), 0f, preAlertState)

        preAlertCountDownTimerHelper =
            object : CountDownTimerHelper(preAlertState.value.timeInMillis, 1000) {
                override fun onTimerTick(millisUntilFinished: Long) {
                    val progressValue =
                        millisUntilFinished.toFloat() / preAlertState.value.timeInMillis

                    handleTimerValues(
                        true,
                        millisUntilFinished.formatTime(),
                        progressValue,
                        preAlertState
                    )
                }

                override fun onTimerFinish() {
                    IncapacitationManager.getTimer().onPreAlertCompleted()
                    preAlertState.value = preAlertState.value.copy(
                        isPlaying = false,
                        progress = 0f,
                    )

                    state.value = state.value.copy(
                        preAlert = UIComponentState.Hide
                    )
                }
            }
    }

    fun decrease(): Long? {
        if (state.value.isPlaying) {
            return countDownTimerHelper?.decrease(adjDuration)
        } else {
            val timeInMillis =
                (state.value.hour.hours + state.value.minute.minutes + state.value.second.seconds).inWholeMilliseconds - adjDuration

            val (h, m, s) = timeInMillis.convertTime()
            state.value =
                state.value.copy(hour = h.toInt(), minute = m.toInt(), second = s.toInt())
            setCountDownTimer()
            return timeInMillis
        }
    }

    fun increase(): Long? {
        if (state.value.isPlaying) {
            return countDownTimerHelper?.increase(adjDuration)
        } else {
            val timeInMillis =
                (state.value.hour.hours + state.value.minute.minutes + state.value.second.seconds).inWholeMilliseconds + adjDuration

            val (h, m, s) = timeInMillis.convertTime()
            state.value =
                state.value.copy(hour = h.toInt(), minute = m.toInt(), second = s.toInt())
            setCountDownTimer()
            return timeInMillis
        }
    }

    fun reset() {
        state.value =
            state.value.copy(
                hour = HOURS,
                minute = MINUTES,
                second = SECONDS,
                isDone = true, preAlert = UIComponentState.Hide
            )
        setCountDownTimer()
    }

    private fun resetPreAlert() {
        state.value =
            state.value.copy(
                hour = 0,
                minute = 0,
                second = DIALOG_SECONDS,
                preAlert = UIComponentState.Hide
            )
        setPreAlertCountDownTimer()
    }

    fun start() {
        countDownTimerHelper?.start()
        state.value = state.value.copy(isPlaying = true)
        if (state.value.isDone) {
            state.value = state.value.copy(progress = 1f)
            state.value = state.value.copy(isDone = false)
        }
    }

    fun pause() {
        countDownTimerHelper?.pause()
        state.value = state.value.copy(isPlaying = false)
    }

    private fun handleTimerValues(
        isPlaying: Boolean,
        text: String,
        progress: Float,
        timerState: MutableState<TimerState>
    ) {
        timerState.value.timeText.value = text
        timerState.value =
            timerState.value.copy(isPlaying = isPlaying, progress = progress)
    }

    fun handlePreAlert(value: UIComponentState) {
        if (value == UIComponentState.Show) {
            state.value =
                state.value.copy(
                    preAlert = UIComponentState.Show,
                )

            preAlertState.value = preAlertState.value.copy(
                hour = 0,
                minute = 0,
                second = DIALOG_SECONDS,
                isPlaying = true
            )
            setPreAlertCountDownTimer()
            preAlertCountDownTimerHelper?.start()
        } else {
            state.value =
                state.value.copy(
                    preAlert = UIComponentState.Hide,
                )
            resetPreAlert()
        }
    }
}
