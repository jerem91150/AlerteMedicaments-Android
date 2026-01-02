package com.alertemedicaments.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.alertemedicaments.data.api.ApiService
import com.alertemedicaments.data.models.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[TOKEN_KEY] != null
    }

    val currentUser: Flow<User?> = context.dataStore.data.map { prefs ->
        val id = prefs[USER_ID_KEY] ?: return@map null
        val email = prefs[USER_EMAIL_KEY] ?: return@map null
        val name = prefs[USER_NAME_KEY]
        User(id = id, email = email, name = name)
    }

    suspend fun getToken(): String? {
        return context.dataStore.data.first()[TOKEN_KEY]
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            saveAuthData(response)
            Result.success(response.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            val response = apiService.register(RegisterRequest(name, email, password))
            saveAuthData(response)
            Result.success(response.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
            prefs.remove(USER_ID_KEY)
            prefs.remove(USER_EMAIL_KEY)
            prefs.remove(USER_NAME_KEY)
        }
    }

    private suspend fun saveAuthData(response: AuthResponse) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = response.token
            prefs[USER_ID_KEY] = response.user.id
            prefs[USER_EMAIL_KEY] = response.user.email
            response.user.name?.let { prefs[USER_NAME_KEY] = it }
        }
    }
}
