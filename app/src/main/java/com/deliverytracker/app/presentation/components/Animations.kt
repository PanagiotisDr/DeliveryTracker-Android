package com.deliverytracker.app.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.deliverytracker.app.presentation.theme.Motion

/**
 * Micro-animations για premium UX.
 * Περιλαμβάνει fade-in, scale-on-press, και staggered animations.
 */

// ============ Fade-In Animation ============

/**
 * Wrapper που κάνει fade-in το περιεχόμενο με slide από κάτω.
 * @param visible Αν είναι true, εμφανίζει το content
 * @param delayMs Καθυστέρηση πριν την εμφάνιση (για staggered effects)
 */
@Composable
fun FadeInCard(
    visible: Boolean = true,
    delayMs: Int = 0,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = Motion.DURATION_MEDIUM,
                delayMillis = delayMs
            )
        ) + slideInVertically(
            animationSpec = tween(
                durationMillis = Motion.DURATION_MEDIUM,
                delayMillis = delayMs
            ),
            initialOffsetY = { it / 4 } // Slide 25% από κάτω
        )
    ) {
        content()
    }
}

// ============ Scale-On-Press Animation ============

/**
 * Modifier που κάνει scale όταν πατιέται το element.
 * Χρησιμοποιείται για premium tap feedback.
 */
fun Modifier.scaleOnPress(
    scaleTarget: Float = 0.95f
) = composed {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleTarget else 1f,
        animationSpec = tween(durationMillis = Motion.DURATION_SHORT),
        label = "scale"
    )
    
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                }
            )
        }
}

// ============ Staggered List Animation ============

/**
 * Column που εμφανίζει τα children με staggered animation.
 * Κάθε child εμφανίζεται με μια μικρή καθυστέρηση μετά το προηγούμενο.
 * 
 * @param staggerDelayMs Καθυστέρηση μεταξύ κάθε item (ms)
 */
@Composable
fun StaggeredColumn(
    modifier: Modifier = Modifier,
    staggerDelayMs: Int = 50,
    content: @Composable ColumnScope.() -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    Column(modifier = modifier) {
        // Σημείωση: Για πλήρη staggered animation, χρησιμοποιείται 
        // LazyColumn με AnimatedItem ή itemsIndexed με FadeInCard
        content()
    }
}

/**
 * Helper για staggered lists.
 * Χρησιμοποιείται μέσα σε LazyColumn:
 * 
 * ```
 * LazyColumn {
 *     itemsIndexed(items) { index, item ->
 *         FadeInCard(delayMs = index * 50) {
 *             ItemCard(item)
 *         }
 *     }
 * }
 * ```
 */
@Composable
fun StaggeredItem(
    index: Int,
    staggerDelayMs: Int = 50,
    content: @Composable () -> Unit
) {
    FadeInCard(
        visible = true,
        delayMs = index * staggerDelayMs
    ) {
        content()
    }
}

// ============ Bounce Animation ============

/**
 * Modifier που κάνει ένα μικρό "bounce" effect στο element.
 * Χρησιμοποιείται για emphasis.
 */
fun Modifier.bounceOnAppear() = composed {
    var hasAppeared by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (hasAppeared) 1f else 0.8f,
        animationSpec = Motion.entranceTween(),
        label = "bounce"
    )
    
    LaunchedEffect(Unit) {
        hasAppeared = true
    }
    
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
