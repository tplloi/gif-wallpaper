package net.roy.ui.setup

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.roy.db.ColorScheme
import net.roy.db.FlowBasedModel
import net.roy.db.NotSet
import net.roy.db.WallpaperStatus
import net.roy.render.DrawableProvider
import net.roy.ui.rgbToColor
import net.roy.util.isDark

interface SetupModel {
    val displayDarkIcons: Flow<Boolean>
    val colorFlow: Flow<ColorPalette>
    val backgroundColorFlow: Flow<Color>
    val hasColorFlow: Flow<Boolean>
    val drawables: Flow<Drawable>
    val isWallpaperSet: Flow<Boolean>
    val hasSettings: Boolean

    suspend fun setBackgroundColor(color: Color)
    suspend fun resetTranslate()
    suspend fun postTranslate(translateX: Float, translateY: Float)
    suspend fun loadNewGif(context: Context, uri: Uri)
    suspend fun clearGif()
    suspend fun setNextScale()
    suspend fun setNextRotation()
}

class SetupModelImpl(
    private val flowBasedModel: FlowBasedModel,
    private val drawableProvider: DrawableProvider,
) : SetupModel {
    override val displayDarkIcons: Flow<Boolean>
        get() = flowBasedModel.backgroundColorFlow.map { !it.isDark() }

    override val colorFlow: Flow<ColorPalette>
        get() = flowBasedModel.colorInfoFlow.map { colorInfo ->
            when (colorInfo) {
                is ColorScheme -> {
                    colorInfo.toColorPalette()
                }

                NotSet -> ColorPalette(Color.Black, emptyList())
            }
        }

    override val backgroundColorFlow: Flow<Color>
        get() = flowBasedModel.backgroundColorFlow.map(Int::rgbToColor)

    override val hasColorFlow: Flow<Boolean> get() = flowBasedModel.colorInfoFlow.map { it is ColorScheme }

    override val drawables: Flow<Drawable>
        get() = drawableProvider.drawables
    override val isWallpaperSet: Flow<Boolean>
        get() = flowBasedModel.wallpaperStatusFlow.map { it is WallpaperStatus.Wallpaper }
    override val hasSettings: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    override suspend fun setBackgroundColor(color: Color) {
        flowBasedModel.setBackgroundColor(color.toArgb())
    }

    override suspend fun resetTranslate() {
        flowBasedModel.resetTranslate()
    }

    override suspend fun postTranslate(translateX: Float, translateY: Float) {
        flowBasedModel.postTranslate(translateX, translateY)
    }

    override suspend fun loadNewGif(context: Context, uri: Uri) {
        flowBasedModel.loadNewGif(context, uri)
        flowBasedModel.resetTranslate()
    }

    override suspend fun clearGif() {
        flowBasedModel.clearGif()
    }

    override suspend fun setNextScale() {
        flowBasedModel.setNextScale()
    }

    override suspend fun setNextRotation() {
        flowBasedModel.setNextRotation()
    }
}

data class ColorPalette(val defaultColor: Color, val colors: List<Color>)

private fun ColorScheme.toColorPalette(): ColorPalette =
    ColorPalette(
        this.defaultColor.rgbToColor(),
        palette.targets.map { target ->
            palette.getColorForTarget(target, android.graphics.Color.BLACK).rgbToColor()
        }.distinct(),
    )
