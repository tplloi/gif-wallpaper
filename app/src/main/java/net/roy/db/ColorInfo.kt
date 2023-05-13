package net.roy.db

import androidx.annotation.ColorInt
import androidx.palette.graphics.Palette

sealed class ColorInfo
object NotSet : ColorInfo()
class ColorScheme(
    @ColorInt val defaultColor: Int,
    val palette: Palette
) : ColorInfo()
