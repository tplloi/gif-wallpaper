package net.roy.db

import android.content.Context
import android.net.Uri
import app.redwarp.gif.decoder.Parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import net.redwarp.gifwallpaper.data.WallpaperSettings
import net.roy.util.FileUtils
import java.io.File

object GifLoader {

    private const val FILE_SIZE_THRESHOLD = 5 * 1024 * 1024

    suspend fun loadInitialValue(wallpaperSettings: WallpaperSettings): WallpaperStatus {
        val file: File? = wallpaperSettings.getWallpaperFile()
        return if (file == null) {
            WallpaperStatus.NotSet
        } else {
            loadGifDescriptor(file)
        }
    }

    suspend fun loadNewGif(context: Context, wallpaperSettings: WallpaperSettings, uri: Uri) =
        withContext(Dispatchers.IO) {
            flow {
                emit(WallpaperStatus.Loading)
                val copiedFile = FileUtils.copyFileLocally(context, uri)
                if (copiedFile == null) {
                    emit(WallpaperStatus.NotSet)
                } else {
                    emit(loadGifDescriptor(copiedFile))
                }
                val localFile: File? = wallpaperSettings.getWallpaperFile()

                localFile?.let(this@GifLoader::cleanupOldUri)

                wallpaperSettings.setWallpaperFile(copiedFile)
            }
        }

    suspend fun clearGif(wallpaperSettings: WallpaperSettings) {
        val localFile: File? = wallpaperSettings.getWallpaperFile()
        withContext(Dispatchers.IO) {
            localFile?.let(this@GifLoader::cleanupOldUri)
        }
        wallpaperSettings.setWallpaperFile(null)
    }

    private fun cleanupOldUri(file: File) {
        runCatching {
            if (file.exists()) {
                file.delete()
            }
        }
    }

    private suspend fun loadGifDescriptor(file: File): WallpaperStatus =
        withContext(Dispatchers.IO) {
            runCatching {
                val result = if (file.length() > FILE_SIZE_THRESHOLD) {
                    Parser.parse(file)
                } else {
                    Parser.parse(file.inputStream())
                }

                result.fold(WallpaperStatus::Wallpaper) {
                    WallpaperStatus.NotSet
                }
            }.getOrDefault(WallpaperStatus.NotSet)
        }
}
