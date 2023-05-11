package net.roy.sv

import android.annotation.SuppressLint
import android.os.Handler
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

/**
 * Helper class to dispatch lifecycle events for our wallpaper engine.
 * Adapted from [androidx.lifecycle.ServiceLifecycleDispatcher].
 */
class EngineLifecycleDispatcher(provider: LifecycleOwner) {
    @SuppressLint("VisibleForTests")
    private val registry: LifecycleRegistry = LifecycleRegistry.createUnsafe(provider)

    @Suppress("DEPRECATION")
    private val handler: Handler = Handler()
    private var lastDispatchRunnable: DispatchRunnable? = null

    private fun postDispatchRunnable(event: Lifecycle.Event) {
        lastDispatchRunnable?.run()
        lastDispatchRunnable = DispatchRunnable(registry, event).also {
            handler.postAtFrontOfQueue(it)
        }
    }

    fun onResume() {
        postDispatchRunnable(Lifecycle.Event.ON_RESUME)
    }

    fun onCreate() {
        postDispatchRunnable(Lifecycle.Event.ON_CREATE)
    }

    fun onStop() {
        postDispatchRunnable(Lifecycle.Event.ON_STOP)
    }

    fun onDestroy() {
        postDispatchRunnable(Lifecycle.Event.ON_DESTROY)
    }

    val lifecycle: Lifecycle
        get() = registry

    internal class DispatchRunnable(
        private val registry: LifecycleRegistry,
        private val event: Lifecycle.Event,
    ) : Runnable {
        private var wasExecuted = false
        override fun run() {
            if (!wasExecuted) {
                registry.handleLifecycleEvent(event)
                wasExecuted = true
            }
        }
    }
}
