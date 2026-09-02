package com.facto.cashlens.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "cashlens_auth")

@Singleton
class AuthPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")
    private val emailKey = stringPreferencesKey("email")

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[accessTokenKey] = accessToken
            prefs[refreshTokenKey] = refreshToken
        }
    }

    suspend fun saveEmail(email: String) {
        context.dataStore.edit { prefs -> prefs[emailKey] = email }
    }

    suspend fun getAccessToken(): String? =
        context.dataStore.data.map { it[accessTokenKey] }.first()

    suspend fun getRefreshToken(): String? =
        context.dataStore.data.map { it[refreshTokenKey] }.first()

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
