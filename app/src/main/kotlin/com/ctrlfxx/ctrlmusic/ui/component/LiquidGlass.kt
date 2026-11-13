package com.ctrlfxx.ctrlmusic.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.glass.GlassStyle
import dev.chrisbanes.haze.glass.hazeGlass

/**
 * Ctrl Music "Liquid Glass" (iOS 26 style).
 *
 * Non-null while Liquid Glass is turned on: it holds the screen content that glass bars
 * refract and blur. Provided once in MainActivity around the screens.
 */
val LocalLiquidGlass = staticCompositionLocalOf<HazeState?> { null }

/** Glass tuned for Ctrl Music's charcoal theme, so labels on top stay readable. */
@Composable
fun rememberCtrlGlassStyle(
    shape: RoundedCornerShape,
    tintAlpha: Float = 0.42f,
): GlassStyle {
    val surface = MaterialTheme.colorScheme.surface
    return remember(shape, surface, tintAlpha) {
        GlassStyle.regular.then {
            shape(shape)
            tint(surface.copy(alpha = tintAlpha))
            specularIntensity(0.55f)
        }
    }
}

/**
 * Real Liquid Glass (refraction, blur, edge light) over the screen content.
 * Does nothing when Liquid Glass is off, so callers keep their normal background.
 */
@Composable
fun Modifier.liquidGlass(
    shape: RoundedCornerShape,
    tintAlpha: Float = 0.42f,
): Modifier {
    val state = LocalLiquidGlass.current ?: return this
    val style = rememberCtrlGlassStyle(shape, tintAlpha)
    return this.hazeGlass(input = HazeInput.Backdrop(state), style = style)
}

/**
 * Light glass look for small, repeated items such as filter chips: a translucent fill
 * with a bright top edge. Cheaper than real glass, so many can be on screen at once.
 */
@Composable
fun Modifier.glassChipSurface(shape: Shape, selected: Boolean): Modifier {
    val accent = MaterialTheme.colorScheme.primary
    val fill = if (selected) {
        Brush.verticalGradient(listOf(accent.copy(alpha = 0.34f), accent.copy(alpha = 0.18f)))
    } else {
        Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.12f), Color.White.copy(alpha = 0.04f)))
    }
    val edge = Brush.verticalGradient(
        listOf(Color.White.copy(alpha = if (selected) 0.45f else 0.30f), Color.White.copy(alpha = 0.04f)),
    )
    return this
        .background(fill, shape)
        .border(width = 0.8.dp, brush = edge, shape = shape)
}
