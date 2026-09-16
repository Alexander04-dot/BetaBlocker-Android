package com.isla2d.betablocker.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesManager(context: Context) {
    companion object {
        private const val PREFERENCES_NAME = "beta_blocker_prefs"
        private val CENSOR_STYLE = stringPreferencesKey("censor_style")
        private val SENSITIVITY = floatPreferencesKey("sensitivity")
        private val SERVICE_ENABLED = booleanPreferencesKey("service_enabled")
        private val CENSOR_COLOR = intPreferencesKey("censor_color")
    }

    private val dataStore = context.preferencesDataStore(name = PREFERENCES_NAME)

    val censorStyleFlow: Flow<String> = dataStore.data.map { prefs ->
        prefs[CENSOR_STYLE] ?: "pixelate"
    }

    val sensitivityFlow: Flow<Float> = dataStore.data.map { prefs ->
        prefs[SENSITIVITY] ?: 50f
    }

    val serviceEnabledFlow: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[SERVICE_ENABLED] ?: false
    }

    val censorColorFlow: Flow<Int> = dataStore.data.map { prefs ->
        prefs[CENSOR_COLOR] ?: 0xFF000000.toInt()
    }

    suspend fun setCensorStyle(style: String) {
        dataStore.edit { prefs ->
            prefs[CENSOR_STYLE] = style
        }
    }

    suspend fun setSensitivity(value: Float) {
        dataStore.edit { prefs ->
            prefs[SENSITIVITY] = value
        }
    }

    suspend fun setServiceEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[SERVICE_ENABLED] = enabled
        }
    }

    suspend fun setCensorColor(color: Int) {
        dataStore.edit { prefs ->
            prefs[CENSOR_COLOR] = color
        }
    }
}
