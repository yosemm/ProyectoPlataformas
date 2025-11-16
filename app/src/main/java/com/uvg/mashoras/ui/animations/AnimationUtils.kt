package com.uvg.mashoras.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Animación de escala al hacer click (bounce effect)
 */
fun Modifier.bounceClick(
    scaleDown: Float = 0.95f,
    onClick: () -> Unit
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bounce_scale"
    )

    this
        .scale(scale)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                },
                onTap = { onClick() }
            )
        }
}

/**
 * Animación de shake (sacudir) para errores
 */
fun Modifier.shake(enabled: Boolean): Modifier = composed {
    val offsetX by animateFloatAsState(
        targetValue = if (enabled) 0f else 0f,
        animationSpec = if (enabled) {
            repeatable(
                iterations = 3,
                animation = tween(50),
                repeatMode = RepeatMode.Reverse
            )
        } else {
            spring()
        },
        label = "shake"
    )

    if (enabled) {
        var currentOffset by remember { mutableStateOf(0f) }
        LaunchedEffect(Unit) {
            val offsets = listOf(10f, -10f, 8f, -8f, 5f, -5f, 0f)
            offsets.forEach { offset ->
                currentOffset = offset
                kotlinx.coroutines.delay(50)
            }
        }
        this.scale(1f)
    } else {
        this
    }
}

/**
 * Fade in animation
 */
fun fadeInAnimation(
    durationMillis: Int = 300,
    delayMillis: Int = 0
): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            durationMillis = durationMillis,
            delayMillis = delayMillis
        )
    )
}

/**
 * Fade out animation
 */
fun fadeOutAnimation(
    durationMillis: Int = 300
): ExitTransition {
    return fadeOut(
        animationSpec = tween(durationMillis = durationMillis)
    )
}

/**
 * Slide in from bottom
 */
fun slideInFromBottomAnimation(
    durationMillis: Int = 300
): EnterTransition {
    return slideInVertically(
        initialOffsetY = { it },
        animationSpec = tween(durationMillis)
    ) + fadeIn(animationSpec = tween(durationMillis))
}

/**
 * Slide out to bottom
 */
fun slideOutToBottomAnimation(
    durationMillis: Int = 300
): ExitTransition {
    return slideOutVertically(
        targetOffsetY = { it },
        animationSpec = tween(durationMillis)
    ) + fadeOut(animationSpec = tween(durationMillis))
}

/**
 * Slide in from right
 */
fun slideInFromRightAnimation(
    durationMillis: Int = 300
): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = tween(durationMillis)
    ) + fadeIn(animationSpec = tween(durationMillis))
}

/**
 * Slide out to left
 */
fun slideOutToLeftAnimation(
    durationMillis: Int = 300
): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { -it },
        animationSpec = tween(durationMillis)
    ) + fadeOut(animationSpec = tween(durationMillis))
}

/**
 * Scale in animation
 */
fun scaleInAnimation(
    durationMillis: Int = 300,
    initialScale: Float = 0.8f
): EnterTransition {
    return scaleIn(
        initialScale = initialScale,
        animationSpec = tween(durationMillis)
    ) + fadeIn(animationSpec = tween(durationMillis))
}

/**
 * Scale out animation
 */
fun scaleOutAnimation(
    durationMillis: Int = 300,
    targetScale: Float = 0.8f
): ExitTransition {
    return scaleOut(
        targetScale = targetScale,
        animationSpec = tween(durationMillis)
    ) + fadeOut(animationSpec = tween(durationMillis))
}

/**
 * Expandir verticalmente
 */
fun expandVerticallyAnimation(
    durationMillis: Int = 300
): EnterTransition {
    return expandVertically(
        animationSpec = tween(durationMillis),
        expandFrom = Alignment.Top
    ) + fadeIn(animationSpec = tween(durationMillis))
}

/**
 * Colapsar verticalmente
 */
fun shrinkVerticallyAnimation(
    durationMillis: Int = 300
): ExitTransition {
    return shrinkVertically(
        animationSpec = tween(durationMillis),
        shrinkTowards = Alignment.Top
    ) + fadeOut(animationSpec = tween(durationMillis))
}

/**
 * Pulso infinito (para notificaciones)
 */
@Composable
fun rememberPulseAnimation(
    minScale: Float = 0.9f,
    maxScale: Float = 1.1f,
    durationMillis: Int = 1000
): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    return scale
}

/**
 * Rotación infinita (para loading)
 */
@Composable
fun rememberRotationAnimation(
    durationMillis: Int = 1000
): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation_angle"
    )
    return rotation
}

/**
 * Animación de offset suave
 */
@Composable
fun animateDpAsState(
    targetValue: Dp,
    animationSpec: AnimationSpec<Dp> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
): State<Dp> {
    return androidx.compose.animation.core.animateDpAsState(
        targetValue = targetValue,
        animationSpec = animationSpec,
        label = "dp_animation"
    )
}