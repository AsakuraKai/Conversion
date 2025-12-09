package com.example.conversion.ui.animation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin

/**
 * Animation utilities and common transitions for the app.
 *
 * Provides Material 3 motion system compliant animations and transitions.
 */
object AnimationUtils {
    
    /**
     * Standard duration for most UI animations (300ms).
     */
    const val STANDARD_DURATION = 300
    
    /**
     * Short duration for quick feedback animations (150ms).
     */
    const val SHORT_DURATION = 150
    
    /**
     * Long duration for emphasis animations (500ms).
     */
    const val LONG_DURATION = 500
    
    /**
     * Standard easing for enter animations.
     */
    val StandardEasing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
    
    /**
     * Emphasized easing for important transitions.
     */
    val EmphasizedEasing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
    
    /**
     * Creates a spring animation spec with standard parameters.
     */
    fun <T> standardSpring(
        dampingRatio: Float = Spring.DampingRatioMediumBouncy,
        stiffness: Float = Spring.StiffnessMedium
    ): SpringSpec<T> = spring(
        dampingRatio = dampingRatio,
        stiffness = stiffness
    )
    
    /**
     * Creates a tween animation spec with standard duration and easing.
     */
    fun <T> standardTween(
        durationMillis: Int = STANDARD_DURATION,
        easing: Easing = StandardEasing
    ): TweenSpec<T> = tween(
        durationMillis = durationMillis,
        easing = easing
    )
}

/**
 * Standard enter transition for screens - fade in + slide up.
 */
@Composable
fun standardEnterTransition(): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            durationMillis = AnimationUtils.STANDARD_DURATION,
            easing = AnimationUtils.StandardEasing
        )
    ) + slideInVertically(
        animationSpec = tween(
            durationMillis = AnimationUtils.STANDARD_DURATION,
            easing = AnimationUtils.StandardEasing
        ),
        initialOffsetY = { it / 4 }
    )
}

/**
 * Standard exit transition for screens - fade out + slide down.
 */
@Composable
fun standardExitTransition(): ExitTransition {
    return fadeOut(
        animationSpec = tween(
            durationMillis = AnimationUtils.STANDARD_DURATION,
            easing = AnimationUtils.StandardEasing
        )
    ) + slideOutVertically(
        animationSpec = tween(
            durationMillis = AnimationUtils.STANDARD_DURATION,
            easing = AnimationUtils.StandardEasing
        ),
        targetOffsetY = { it / 4 }
    )
}

/**
 * Slide-in from end (right in LTR) transition.
 */
@Composable
fun slideInFromEndTransition(): EnterTransition {
    return slideInHorizontally(
        animationSpec = tween(
            durationMillis = AnimationUtils.STANDARD_DURATION,
            easing = AnimationUtils.EmphasizedEasing
        ),
        initialOffsetX = { it }
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = AnimationUtils.STANDARD_DURATION
        )
    )
}

/**
 * Slide-out to start (left in LTR) transition.
 */
@Composable
fun slideOutToStartTransition(): ExitTransition {
    return slideOutHorizontally(
        animationSpec = tween(
            durationMillis = AnimationUtils.STANDARD_DURATION,
            easing = AnimationUtils.StandardEasing
        ),
        targetOffsetX = { -it }
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = AnimationUtils.STANDARD_DURATION
        )
    )
}

/**
 * Scale and fade transition for dialogs and bottom sheets.
 */
@Composable
fun scaleInTransition(): EnterTransition {
    return scaleIn(
        animationSpec = tween(
            durationMillis = AnimationUtils.STANDARD_DURATION,
            easing = AnimationUtils.EmphasizedEasing
        ),
        initialScale = 0.8f,
        transformOrigin = TransformOrigin.Center
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = AnimationUtils.SHORT_DURATION
        )
    )
}

/**
 * Scale and fade out transition for dialogs and bottom sheets.
 */
@Composable
fun scaleOutTransition(): ExitTransition {
    return scaleOut(
        animationSpec = tween(
            durationMillis = AnimationUtils.SHORT_DURATION,
            easing = AnimationUtils.StandardEasing
        ),
        targetScale = 0.8f,
        transformOrigin = TransformOrigin.Center
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = AnimationUtils.SHORT_DURATION
        )
    )
}

/**
 * Expand vertically transition for expanding content.
 */
@Composable
fun expandVerticallyTransition(): EnterTransition {
    return expandVertically(
        animationSpec = tween(
            durationMillis = AnimationUtils.STANDARD_DURATION,
            easing = AnimationUtils.EmphasizedEasing
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = AnimationUtils.STANDARD_DURATION
        )
    )
}

/**
 * Shrink vertically transition for collapsing content.
 */
@Composable
fun shrinkVerticallyTransition(): ExitTransition {
    return shrinkVertically(
        animationSpec = tween(
            durationMillis = AnimationUtils.STANDARD_DURATION,
            easing = AnimationUtils.StandardEasing
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = AnimationUtils.STANDARD_DURATION
        )
    )
}

/**
 * Modifier extension for animated visibility changes.
 */
fun Modifier.animatedVisibility(
    visible: Boolean,
    enter: EnterTransition,
    exit: ExitTransition
): Modifier = this.then(
    Modifier.animateEnterExit(enter = enter, exit = exit)
)

/**
 * Success celebration animation - scale bounce effect.
 */
@Composable
fun successAnimation(): AnimationSpec<Float> {
    return keyframes {
        durationMillis = 600
        1.0f at 0
        1.3f at 150 with FastOutSlowInEasing
        0.95f at 300 with FastOutSlowInEasing
        1.05f at 450 with FastOutSlowInEasing
        1.0f at 600
    }
}

/**
 * Error shake animation spec.
 */
@Composable
fun shakeAnimation(): AnimationSpec<Float> {
    return keyframes {
        durationMillis = 400
        0f at 0
        -10f at 50
        10f at 100
        -10f at 150
        10f at 200
        -5f at 250
        5f at 300
        0f at 400
    }
}

/**
 * Pulse animation for attention-grabbing elements.
 */
@Composable
fun pulseAnimation(): InfiniteRepeatableSpec<Float> {
    return infiniteRepeatable(
        animation = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        repeatMode = RepeatMode.Reverse
    )
}
