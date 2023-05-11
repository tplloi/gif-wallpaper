package net.roy.ext

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private val mutableWallpaperActive = MutableStateFlow(false)
val wallpaperActive = mutableWallpaperActive.asStateFlow()

class WallpaperObserver : DefaultLifecycleObserver {
    override fun onCreate(owner: LifecycleOwner) {
        mutableWallpaperActive.value = true
    }

    override fun onDestroy(owner: LifecycleOwner) {
        mutableWallpaperActive.value = false
    }
}
