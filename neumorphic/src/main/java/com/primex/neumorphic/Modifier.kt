package com.primex.neumorphic

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private class NeumorphicModifierImpl(
    val outline: Neumorphic,
    val offset: Offset,
    val filter: BlurMaskFilter,
    val lightShadowColor: Color,
    val darkShadowColor: Color,
    val strokeWidth: Float = 0f
) : DrawModifier {
    override fun ContentDrawScope.draw() {

        val lightColorInt = lightShadowColor.toArgb()
        val darkShadowInt = darkShadowColor.toArgb()

        val radiusPx =
            when (outline) {
                is PercentRoundedNeumorphicShape -> kotlin.run { size.minDimension * (outline.pct / 100f) }
                is DpRoundedNeumorphicShape -> outline.radius.toPx()
            }

        val isBg = strokeWidth == 0f

        when (isBg) {
            true -> background(
                offset = offset,
                radius = radiusPx,
                lightShadowColor = lightColorInt,
                darkShadowColor = darkShadowInt,
                filter = filter
            )
            else -> foreground(
                offset = offset,
                radius = radiusPx,
                lightShadowColor = lightColorInt,
                darkShadowColor = darkShadowInt,
                filter = filter,
                strokeWidth = strokeWidth
            )
        }

    }
}

private const val POINT_60 = 0.6f
private const val POINT_95 = 0.95f


/**
 * The basic neumorphic modifier.
 */
fun Modifier.neumorphic(
    outline: Neumorphic,
    lightShadowColor: Color,
    darkShadowColor: Color,
    elevation: Dp,
    intensity: Float = 1.0f,
    spotLight: SpotLight,
    border: BorderStroke? = null
): Modifier = composed {
    val elevationPx = kotlin.math.abs(with(LocalDensity.current) { elevation.toPx() })
    val elevated = elevation > 0.dp

    val multiplier = elevationPx * if (elevated) POINT_60 else POINT_95

    val blurRadius =
        (elevationPx * if (elevated) POINT_95 else POINT_60)
            //TODO: blurRadius Crash if equal to 0
            .coerceAtLeast(0.001f)

    val filter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)

    val offset =
        spotLight.toOffset(multiplier, elevated)

    neumorphic(
        outline = outline,
        offset = offset,
        filter = filter,
        lightShadowColor = if (elevated) lightShadowColor else darkShadowColor,
        darkShadowColor = if (elevated) darkShadowColor else lightShadowColor,
        border = border,
        strokeWidth = if (elevated) 0f else multiplier,
        intensity = intensity
    )
}


fun Modifier.neumorphic(
    outline: Neumorphic,
    offset: Offset,
    filter: BlurMaskFilter,
    strokeWidth: Float,
    lightShadowColor: Color,
    darkShadowColor: Color,
    intensity: Float = Float.NaN,
    border: BorderStroke? = null
): Modifier {
    val neumorphic = when (offset) {
        Offset.Zero -> Modifier
        else -> NeumorphicModifierImpl(
            outline = outline,
            lightShadowColor = lightShadowColor.copy(intensity),
            darkShadowColor = darkShadowColor.copy(intensity),
            offset = offset,
            filter = filter,
            strokeWidth = strokeWidth
        )
    }

    val shapeAndroid = outline.asAndroidShape

    return this
        .then(neumorphic)
        .then(if (border != null) Modifier.border(border, shapeAndroid) else Modifier)
        .clip(shapeAndroid)
}