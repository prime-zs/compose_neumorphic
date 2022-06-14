@file:Suppress("NOTHING_TO_INLINE", "FunctionName")

package com.primex.neumorphic

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The marker [Neumorphic] shape interface.
 * TODO: Future version will surely include much more advanced features.
 */
sealed interface Neumorphic

/**
 * Construct a  Circular [Neumorphic] shape
 */
val CircleNeumorphicShape get() = RoundedNeumorphicShape(50)

/**
 * Constructs The [Neumorphic] Round Shape.
 *
 * @param: radius of the shape in [Dp].
 */
@JvmInline
internal value class DpRoundedNeumorphicShape(val radius: Dp) : Neumorphic

/**
 * Constructs the [Neumorphic] with pct size of corner
 */
@JvmInline
internal value class PercentRoundedNeumorphicShape(val pct: Int) : Neumorphic


/**
 * Construct a [Neumorphic] shape with corners equal to [percent] radius
 * @param percent: radius in percent between 0 to 100
 */
fun RoundedNeumorphicShape(percent: Int = 0): Neumorphic =
    PercentRoundedNeumorphicShape(percent)

/**
 * Construct a [Neumorphic] shape with corners equal to [radius]
 * @param radius: radius in [Dp]
 */
fun RoundedNeumorphicShape(radius: Dp = 0.dp): Neumorphic =
    DpRoundedNeumorphicShape(radius = radius)

val Neumorphic.asAndroidShape
    get() =
        when (this) {
            is DpRoundedNeumorphicShape -> RoundedCornerShape(radius)
            is PercentRoundedNeumorphicShape -> if (pct == 50) CircleShape else RoundedCornerShape(
                pct
            )
        }