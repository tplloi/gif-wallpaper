package net.roy.util

import androidx.core.graphics.ColorUtils

/**
 * Check if the color's luminance is dark enough to be considered as a dark color.
 **/
fun Int.isDark(): Boolean {
    return ColorUtils.calculateLuminance(this) < 0.5
}
