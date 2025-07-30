package com.myprotect.projectx.incapacitation

import com.myprotect.projectx.domain.core.UIComponentState
import com.myprotect.projectx.common.CommonFlow
import com.myprotect.projectx.common.asCommonFlow
import kotlinx.coroutines.flow.MutableStateFlow

data class TimerState(
    val timeInMillis: Long = 0L,
    val timeText: MutableStateFlow<String> = MutableStateFlow("00:00:00"),
    val _timeText: CommonFlow<String> = timeText.asCommonFlow(),
    val hour: Int = 0,
    val minute: Int = 0,
    val second: Int = 0,
    val progress: Float = 0f,
    val isPlaying: Boolean = false,
    val isDone: Boolean = true,
    val preAlert: UIComponentState = UIComponentState.Hide,
)
