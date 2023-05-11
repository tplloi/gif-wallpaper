package net.roy.util

import android.app.WallpaperColors
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Compat version of the wallpaper colors, so that the app can run
 * pre Android 27
 */
@RequiresApi(Build.VERSION_CODES.O_MR1)
data class WallpaperColorsCompat(
    val primaryColor: Color,
    val secondaryColor: Color?,
    val tertiaryColor: Color?,
    val colorHints: Int? = null,
)

@RequiresApi(Build.VERSION_CODES.O_MR1)
fun WallpaperColors.toCompat(): WallpaperColorsCompat =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        toCompat31()
    } else {
        toCompat27()
    }

@RequiresApi(Build.VERSION_CODES.O_MR1)
private fun WallpaperColors.toCompat27(): WallpaperColorsCompat =
    WallpaperColorsCompat(primaryColor, secondaryColor, tertiaryColor)

@RequiresApi(Build.VERSION_CODES.S)
private fun WallpaperColors.toCompat31(): WallpaperColorsCompat =
    WallpaperColorsCompat(primaryColor, secondaryColor, tertiaryColor, colorHints)

@RequiresApi(Build.VERSION_CODES.O_MR1)
fun WallpaperColorsCompat.toReal(): WallpaperColors =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        toReal31()
    } else {
        toReal27()
    }

@RequiresApi(Build.VERSION_CODES.O_MR1)
private fun WallpaperColorsCompat.toReal27(): WallpaperColors =
    WallpaperColors(primaryColor, secondaryColor, tertiaryColor)

@RequiresApi(Build.VERSION_CODES.S)
private fun WallpaperColorsCompat.toReal31(): WallpaperColors =
    WallpaperColors(primaryColor, secondaryColor, tertiaryColor, colorHints ?: 0)
