package com.karan.schemesaathi.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val KEY_USER_ID = stringPreferencesKey("user_id")
    private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
    private val KEY_IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
    private val KEY_SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
    private val KEY_DEMO_MODE = booleanPreferencesKey("demo_mode")

    val userIdFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_ID]
    }

    val userEmailFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_EMAIL]
    }

    val isOnboardedFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_IS_ONBOARDED] ?: false
    }

    val selectedLanguageFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_SELECTED_LANGUAGE] ?: "en"
    }

    val isDemoModeFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_DEMO_MODE] ?: true // Default to true for easy evaluation
    }

    suspend fun saveSession(userId: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_ID] = userId
            prefs[KEY_USER_EMAIL] = email
        }
    }

    suspend fun setOnboarded(onboarded: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_ONBOARDED] = onboarded
        }
    }

    suspend fun setSelectedLanguage(languageCode: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SELECTED_LANGUAGE] = languageCode
        }
    }

    suspend fun setDemoMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DEMO_MODE] = enabled
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_USER_ID)
            prefs.remove(KEY_USER_EMAIL)
            prefs.remove(KEY_IS_ONBOARDED)
        }
    }
}
