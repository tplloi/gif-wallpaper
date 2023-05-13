package net.roy.ext

import android.content.Context
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.roy.R

interface AppSettings {
    val powerSavingSettingFlow: Flow<Boolean>
    val thermalThrottleSettingFlow: Flow<Boolean>
    val isThermalThrottleSupported: Boolean
    suspend fun setPowerSaving(enabled: Boolean)
    suspend fun setThermalThrottle(enabled: Boolean)
}

class DataStoreAppSettings(
    private val context: Context,
    ioScope: CoroutineScope
) : AppSettings {
    private val powerSavingKey = booleanPreferencesKey("power_saving")
    private val thermalThrottleKey = booleanPreferencesKey("thermal_throttle")
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        "app_settings",
        scope = ioScope,
    )

    override val powerSavingSettingFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[powerSavingKey] ?: context.resources.getBoolean(R.bool.power_saving_enabled)
    }
    override val thermalThrottleSettingFlow: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[thermalThrottleKey]
                ?: context.resources.getBoolean(R.bool.thermal_throttle_enabled)
        }
    override val isThermalThrottleSupported: Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    override suspend fun setPowerSaving(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[powerSavingKey] = enabled
        }
    }

    override suspend fun setThermalThrottle(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[thermalThrottleKey] = enabled
        }
    }
}
