package com.alertemedicaments.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alertemedicaments.data.api.ApiService
import com.alertemedicaments.data.models.RegisterPushTokenRequest
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PushTokenRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService,
    private val authRepository: AuthRepository
) {
    companion object {
        private val PUSH_TOKEN_KEY = stringPreferencesKey("push_token")
    }

    suspend fun saveAndSyncToken(token: String) {
        // Save locally
        context.dataStore.edit { prefs ->
            prefs[PUSH_TOKEN_KEY] = token
        }

        // Sync with server if logged in
        syncTokenWithServer(token)
    }

    suspend fun syncTokenWithServer(token: String? = null) {
        val authToken = authRepository.getToken() ?: return
        val pushToken = token ?: getStoredToken() ?: return

        try {
            apiService.registerPushToken(
                "Bearer $authToken",
                RegisterPushTokenRequest(token = pushToken, platform = "android")
            )
        } catch (e: Exception) {
            // Silent fail - will retry on next app open
        }
    }

    suspend fun getStoredToken(): String? {
        return context.dataStore.data.first()[PUSH_TOKEN_KEY]
    }

    suspend fun refreshAndSyncToken() {
        try {
            val token = FirebaseMessaging.getInstance().token.await()
            saveAndSyncToken(token)
        } catch (e: Exception) {
            // Firebase not configured or unavailable
        }
    }

    suspend fun deleteToken() {
        val authToken = authRepository.getToken()
        val pushToken = getStoredToken()

        if (authToken != null && pushToken != null) {
            try {
                apiService.deletePushToken("Bearer $authToken", pushToken)
            } catch (e: Exception) {
                // Silent fail
            }
        }

        context.dataStore.edit { prefs ->
            prefs.remove(PUSH_TOKEN_KEY)
        }
    }
}
