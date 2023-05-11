package net.roy.util

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object FileUtils {

    /**
     * Copy a file from a Uri to a file in the application's directory.
     **/
    suspend fun copyFileLocally(
        context: Context,
        uri: Uri
    ): File? = withContext(Dispatchers.IO) {
        val localDir = context.filesDir
        val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        val localFileName = UUID.randomUUID().toString().let {
            if (extension != "") {
                "$it.$extension"
            } else {
                it
            }
        }

        runCatching {
            val file = File(localDir, localFileName).apply {
                createNewFile()
                setWritable(true, true)
            }

            var copiedLength = 0L
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    copiedLength = inputStream.copyTo(outputStream)
                }
            }

            if (copiedLength == 0L) {
                file.delete()
                null
            } else {
                file
            }
        }.getOrNull()
    }
}
