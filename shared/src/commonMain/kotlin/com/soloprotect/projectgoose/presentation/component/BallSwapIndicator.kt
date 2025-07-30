package com.myprotect.projectx.presentation.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun BallSwapIndicator(
    modifier: Modifier = Modifier
) {
    val ballRadius = 10f
    val distanceBetweenBalls = 40f // Total distance the balls will travel
    val animationDuration = 1000 // Duration for the animation cycle
    val color = MaterialTheme.colorScheme.primary

    // Infinite transition to control both animations
    val transition = rememberInfiniteTransition()

    // Right ball moves linearly from right to left
    val blueBallX by transition.animateFloat(
        initialValue = distanceBetweenBalls / 2, // Rightmost point
        targetValue = -distanceBetweenBalls / 2, // Leftmost point
        animationSpec = infiniteRepeatable(
            tween(animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Left ball moves along the upper semi-circle from left to right
    val redBallFraction by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Calculate the left ball's X and Y coordinates for the upper semi-circle movement
    val redBallX = -distanceBetweenBalls / 2 + distanceBetweenBalls * redBallFraction // From left to right
    val redBallY = -distanceBetweenBalls / 2 * sin(PI * redBallFraction).toFloat() // Semi-circle curve

    // Draw the two balls on the canvas
    Box(
        modifier = modifier.background(Color.Blue),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.width(40.dp)) {
            // Draw the red ball moving along the upper semi-circle (left to right)
            drawCircle(
                color = color,
                radius = ballRadius,
                center = center.copy(x = center.x + redBallX, y = center.y + redBallY)
            )

            // Draw the blue ball moving linearly from right to left
            drawCircle(
                color = color,
                radius = ballRadius,
                center = center.copy(x = center.x + blueBallX, y = center.y)
            )
        }
    }
}
