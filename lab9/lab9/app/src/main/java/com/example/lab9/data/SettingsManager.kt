package com.example.lab9.data

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

// Тип для хранения темы
enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

class SettingsManager(private val context: Context) {

    companion object {
        val THEME_MODE_KEY = intPreferencesKey("theme_mode")
    }

    val appTheme: Flow<AppTheme> = context.dataStore.data
        .map { preferences ->
            val themeValue = preferences[THEME_MODE_KEY] ?: 2 // по умолчанию — системная тема
            AppTheme.values()[themeValue]
        }

    suspend fun saveTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = theme.ordinal
        }
    }
}
