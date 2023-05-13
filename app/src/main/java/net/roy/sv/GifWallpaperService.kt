package net.roy.sv

import android.app.WallpaperColors
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.roy.GifApplication
import net.roy.R
import net.roy.render.DrawableMapper
import net.roy.render.SurfaceDrawableRenderer
import net.roy.render.createMiniature
import net.roy.util.WallpaperColorsCompat
import net.roy.util.toCompat
import net.roy.util.toReal
import net.roy.ext.WallpaperObserver

class GifWallpaperService : WallpaperService(), LifecycleOwner {
    private val dispatcher = EngineLifecycleDispatcher(this)

    override fun onCreateEngine(): Engine {
        return GifEngine()
    }

    override fun onCreate() {
        super.onCreate()
        dispatcher.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        dispatcher.onDestroy()
    }

    override val lifecycle: Lifecycle
        get() = dispatcher.lifecycle

    inner class GifEngine : Engine() {
        private var surfaceDrawableRenderer: SurfaceDrawableRenderer? = null

        private val handler: Handler = Handler(Looper.getMainLooper())
        private var wallpaperColors: WallpaperColorsCompat? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                getColor(R.color.colorPrimaryDark).colorToWallpaperColor()
            } else {
                null
            }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)

            surfaceDrawableRenderer =
                SurfaceDrawableRenderer(surfaceHolder, handler)

            val modelFlow = GifApplication.app.model

            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch {
                        val drawableOwner =
                            DrawableMapper.serviceMapper(this@GifWallpaperService, modelFlow, this)

                        drawableOwner.drawables.collectLatest { drawable ->
                            surfaceDrawableRenderer?.drawable = drawable
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                        launch {
                            modelFlow.updateFlow.collectLatest {
                                updateWallpaperColors()
                            }
                        }
                    }
                }
            }

            if (!isPreview) {
                lifecycle.addObserver(WallpaperObserver())
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            surfaceDrawableRenderer = null
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible) {
                dispatcher.onResume()
            } else {
                dispatcher.onStop()
            }
            surfaceDrawableRenderer?.visibilityChanged(visible)
        }

        @RequiresApi(Build.VERSION_CODES.O_MR1)
        override fun onComputeColors(): WallpaperColors? {
            return wallpaperColors?.toReal()
        }

        @RequiresApi(Build.VERSION_CODES.O_MR1)
        private suspend fun updateWallpaperColors() {
            withContext(Dispatchers.Default) {
                wallpaperColors = surfaceDrawableRenderer?.drawable?.createMiniature()
                    ?.let(WallpaperColors::fromBitmap)?.toCompat()
                    ?: getColor(R.color.colorPrimaryDark).colorToWallpaperColor()
                withContext(Dispatchers.Main) {
                    notifyColorsChanged()
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O_MR1)
        private fun Int.colorToWallpaperColor(): WallpaperColorsCompat {
            val bitmap = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            canvas.drawColor(this)
            val wallpaperColors = WallpaperColors.fromBitmap(bitmap)
            bitmap.recycle()
            return wallpaperColors.toCompat()
        }
    }
}
