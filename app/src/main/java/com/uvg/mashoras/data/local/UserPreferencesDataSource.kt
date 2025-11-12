package com.uvg.mashoras.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore delegado a nivel de contexto
val Context.userPrefsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_prefs"
)

class UserPreferencesDataSource(
    private val dataStore: DataStore<Preferences>
) {

    private val EMAIL_KEY = stringPreferencesKey("user_email")
    private val ROLE_KEY = stringPreferencesKey("user_role")

    fun getEmail(): Flow<String?> =
        dataStore.data.map { prefs -> prefs[EMAIL_KEY] }

    fun getRole(): Flow<String?> =
        dataStore.data.map { prefs -> prefs[ROLE_KEY] }

    suspend fun saveUser(email: String, role: String) {
        dataStore.edit { prefs ->
            prefs[EMAIL_KEY] = email
            prefs[ROLE_KEY] = role
        }
    }

    suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
