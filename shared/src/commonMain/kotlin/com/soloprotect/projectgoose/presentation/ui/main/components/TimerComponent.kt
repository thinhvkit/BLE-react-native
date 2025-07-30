package com.myprotect.projectx.presentation.ui.main.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.myprotect.projectx.domain.core.tr
import com.myprotect.projectx.common.LocaleKeys
import com.myprotect.projectx.incapacitation.TimerState
import com.myprotect.projectx.presentation.component.Spacer_8dp
import com.myprotect.projectx.presentation.theme.blue_01
import com.myprotect.projectx.presentation.theme.red_01
import com.myprotect.projectx.presentation.theme.timerBackground
import com.myprotect.projectx.presentation.theme.timerButton
import org.jetbrains.compose.resources.painterResource
import myprotect_mobile.shared.generated.resources.Res
import myprotect_mobile.shared.generated.resources.cross
import myprotect_mobile.shared.generated.resources.minus
import myprotect_mobile.shared.generated.resources.plus
import myprotect_mobile.shared.generated.resources.tick
import myprotect_mobile.shared.generated.resources.timer

@Composable
fun TimerComponent(
    modifier: Modifier = Modifier,
    timerState: TimerState,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
) {
    val isDoneTransition = updateTransition(timerState.isDone)

    Surface(modifier = modifier)
    {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxSize().background(timerBackground),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Timer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    timeText = timerState.timeText.value,
                    progress = timerState.progress,
                )

                TimerButtons(
                    modifier = Modifier
                        .padding(start = 7.dp),
                    state = timerState,
                    isDoneTransition = isDoneTransition,
                    onDecrease = onDecrease,
                    onIncrease = onIncrease,
                    onStart = onStart,
                    onStop = onStop,
                )
            }
        }
    }
}

@Composable
private fun Timer(
    modifier: Modifier = Modifier,
    timeText: String,
    color: Color = Color.White,
    progress: Float,
) {
    Box(modifier = modifier) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = timeText,
            style = MaterialTheme.typography.headlineSmall,
            color = color
        )
    }
}

@Composable
fun ClockButton(
    modifier: Modifier,
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.primary,
    enabled: Boolean = true,
    icon: Painter
) {

    Box(
        modifier = modifier.fillMaxHeight()
            .defaultMinSize(minWidth = 60.dp)
            .padding(horizontal = 1.dp)
            .background(timerButton)
            .clip(RectangleShape)
            .clickable(
                enabled = enabled,
            ) {
                onClick()
            },
    ) {

        Icon(
            icon,
            modifier = Modifier.size(48.dp)
                .align(Alignment.Center)
                .padding(horizontal = 6.dp),
            contentDescription = null,
            tint = if (enabled) color else color.copy(alpha = 0.5f)
        )
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun TimerButtons(
    modifier: Modifier,
    state: TimerState,
    isDoneTransition: Transition<Boolean>,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit
) {

    Box(modifier = modifier) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {

            ClockButton(
                modifier = Modifier.fillMaxHeight(),
                onClick = { onDecrease() },
                color = Color.White,
                enabled = state.timeInMillis != 0L,
                icon = painterResource(Res.drawable.minus)
            )

            ClockButton(
                modifier = Modifier.fillMaxHeight(),
                onClick = { onIncrease() },
                color = Color.White,
                icon = painterResource(Res.drawable.plus)
            )

            Box {

                isDoneTransition.AnimatedVisibility(
                    visible = { isTimerDone -> isTimerDone },
                    enter = fadeIn(
                        animationSpec = tween(),
                    ),
                    exit = fadeOut(
                        animationSpec = tween(),
                    ),
                ) {
                    ClockButton(
                        modifier = Modifier.fillMaxHeight().width(80.dp),
                        onClick = { onStart() },
                        enabled = state.timeInMillis != 0L,
                        color = Color.Green,
                        icon = painterResource(Res.drawable.tick)
                    )
                }

                isDoneTransition.AnimatedVisibility(
                    visible = { isTimerDone -> !isTimerDone },
                    enter = fadeIn(
                        animationSpec = tween(),
                    ),
                    exit = fadeOut(
                        animationSpec = tween(),
                    ),
                ) {
                    ClockButton(
                        modifier = Modifier.fillMaxHeight().width(80.dp),
                        onClick = { onStop() },
                        color = Color.Red,
                        icon = painterResource(Res.drawable.cross)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerAlertDialog(
    timerState: TimerState,
    onDismissRequest: () -> Unit,
) {

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 12.dp),
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false,
            dismissOnBackPress = true
        )
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = blue_01),
            shape = CardDefaults.shape,
        ) {

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.timer),
                    null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.White,
                )
                Spacer_8dp()
                Text(
                    LocaleKeys.INCAPACITATION_ALERT.tr(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().background(timerBackground),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Timer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    timeText = timerState.timeText.value,
                    color = red_01,
                    progress = timerState.progress,
                )

                ClockButton(
                    modifier = Modifier.fillMaxHeight().width(80.dp),
                    onClick = {
                        onDismissRequest()
                    },
                    color = Color.Red,
                    icon = painterResource(Res.drawable.cross)
                )
            }
        }
    }
}
