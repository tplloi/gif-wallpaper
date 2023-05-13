package net.roy.render

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.view.SurfaceHolder

/**
 * Simple class do draw a [Drawable] on a [SurfaceHolder]
 */
class SurfaceDrawableRenderer(
    private val holder: SurfaceHolder,
    private val handler: Handler,
    drawable: Drawable? = null,
) : SurfaceHolder.Callback2, Drawable.Callback {
    private var width: Int = 0
    private var height: Int = 0
    private var isVisible = false
    private var hasDimension = false
    private var isCreated = false

    init {
        drawable?.callback = this
        drawable?.setVisible(false, false)
        holder.addCallback(this)
    }

    var drawable: Drawable? = drawable
        @Synchronized set(value) {
            field?.callback = null

            field = value
            if (value != null) {
                value.setBounds(0, 0, width, height)

                value.callback = this
                value.setVisible(isVisible, false)
                if (isVisible) {
                    if (value is Animatable) value.start()
                } else {
                    if (value is Animatable) value.stop()
                }

                if (isVisible) {
                    value.invalidateSelf()
                }
            }
        }
        @Synchronized get

    @Synchronized
    fun visibilityChanged(isVisible: Boolean) {
        this.isVisible = isVisible
        drawable?.setVisible(isVisible, false)

        if (isVisible) {
            (drawable as? Animatable)?.let(Animatable::start)
            drawable?.invalidateSelf()
        } else {
            (drawable as? Animatable)?.let(Animatable::stop)
        }
    }

    @Synchronized
    override fun surfaceCreated(holder: SurfaceHolder) {
        isCreated = true
        drawable?.setVisible(isVisible, true)
    }

    @Synchronized
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        this.width = width
        this.height = height

        drawable?.setBounds(0, 0, width, height)

        hasDimension = true

        drawable?.invalidateSelf()
    }

    @Synchronized
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isCreated = false
        drawable?.callback = null
        drawable?.setVisible(isVisible, false)
    }

    override fun surfaceRedrawNeeded(holder: SurfaceHolder) {
        drawable?.invalidateSelf()
    }

    override fun surfaceRedrawNeededAsync(holder: SurfaceHolder, drawingFinished: Runnable) {
        drawable?.invalidateSelf()
        drawingFinished.run()
    }

    @Synchronized
    private fun drawOnSurface(drawable: Drawable) {
        if (hasDimension && isCreated) {
            val canvas: Canvas? =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    holder.lockHardwareCanvas()
                } else {
                    holder.lockCanvas(null)
                }

            if (canvas != null) {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                drawable.draw(canvas)

                holder.unlockCanvasAndPost(canvas)
            }
        }
    }

    override fun invalidateDrawable(who: Drawable) {
        drawOnSurface(who)
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
        handler.postAtTime(what, who, `when`)
    }

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        handler.removeCallbacks(what, who)
    }
}
