package net.roy

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import net.roy.ext.AppSettings
import net.roy.ext.DataStoreAppSettings
import net.roy.db.FlowBasedModel
import net.roy.db.WallpaperSettings

//TODO ic_launcher
//TODO app color
//TODO rate app
//TODO share app
//TODO more app
//TODo policy
//TODO proguard
//TODO keystore

//TODO firebase
//TODO ad

//done
//pkg name
//ad id manifest
//leak canary

class GifApplication : Application() {

    companion object {
        private lateinit var instance: GifApplication

        val app: GifApplication get() = instance
    }

    private val appScope = CoroutineScope(SupervisorJob())
    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private lateinit var _appSettings: DataStoreAppSettings
    private lateinit var _flowBasedModel: FlowBasedModel
    val appSettings: AppSettings get() = _appSettings
    val model get() = _flowBasedModel

    override fun onCreate() {
        super.onCreate()

        setupViews()
    }

    private fun setupViews() {
        if (BuildConfig.DEBUG) {
            StrictMode.setVmPolicy(
                VmPolicy.Builder()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build(),
            )
            StrictMode.allowThreadDiskReads()

            StrictMode.enableDefaults()
        }

        val appSettings = DataStoreAppSettings(this, ioScope)
        _appSettings = appSettings

        val wallpaperSettings = WallpaperSettings(this, ioScope)
        _flowBasedModel = FlowBasedModel(this, appScope, wallpaperSettings, appSettings)

        instance = this
    }
}
