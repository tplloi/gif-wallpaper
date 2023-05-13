package net.roy.db

import app.redwarp.gif.decoder.descriptors.GifDescriptor

sealed interface WallpaperStatus {
    object NotSet : WallpaperStatus
    object Loading : WallpaperStatus
    data class Wallpaper(val gifDescriptor: GifDescriptor) : WallpaperStatus
}
