package net.roy.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Orange = Color(0xffF44336)
val DarkOrange = Color(0xffF44336)
// val LightGreen = Color(0xFF8BC34A)
// val Green = Color(0xFF8BC34A)

private val darkColors = darkColors(
    primary = Orange,
    primaryVariant = DarkOrange,
    secondary = Orange,
    secondaryVariant = DarkOrange,
)
private val lightColors = lightColors(
    primary = Orange,
    primaryVariant = DarkOrange,
    secondary = Orange,
    secondaryVariant = DarkOrange,
)
val typography = Typography(
    h1 = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 44.sp,
        letterSpacing = 0.sp,
    ),
    h2 = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 40.sp,
        letterSpacing = 0.sp,
    ),
    h3 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        letterSpacing = 0.sp,
    ),
    h4 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        letterSpacing = 0.25.sp,
    ),
    h5 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        letterSpacing = 0.sp,
    ),
    h6 = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        letterSpacing = 0.15.sp,
    ),
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = if (darkTheme) darkColors else lightColors,
        typography = typography,
        content = content,
    )
}
