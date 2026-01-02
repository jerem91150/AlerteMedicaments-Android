package com.alertemedicaments.data.api

import com.alertemedicaments.data.models.*
import retrofit2.http.*

interface ApiService {

    // ==================== Search ====================
    @GET("search")
    suspend fun searchMedications(
        @Query("q") query: String
    ): MedicationSearchResponse

    @GET("suggestions")
    suspend fun getSuggestions(
        @Query("q") query: String
    ): SuggestionsResponse

    @GET("medications/{id}/alternatives")
    suspend fun getAlternatives(
        @Path("id") medicationId: String
    ): AlternativesResponse

    // ==================== Pharmacies ====================
    @GET("pharmacies/nearby")
    suspend fun getNearbyPharmacies(
        @Query("medicationId") medicationId: String,
        @Query("lat") latitude: Double? = null,
        @Query("lng") longitude: Double? = null,
        @Query("radius") radius: Double = 20.0
    ): PharmaciesResponse

    @POST("pharmacies/report")
    suspend fun reportAvailability(
        @Body report: ReportRequest
    )

    // ==================== Auth ====================
    @POST("auth/mobile/login")
    suspend fun login(
        @Body request: LoginRequest
    ): AuthResponse

    @POST("auth/mobile/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): AuthResponse

    // ==================== Alerts ====================
    @GET("alerts/mobile")
    suspend fun getAlerts(
        @Header("Authorization") token: String
    ): AlertsResponse

    @POST("alerts/mobile")
    suspend fun createAlert(
        @Header("Authorization") token: String,
        @Body request: CreateAlertRequest
    ): AlertResponse

    @DELETE("alerts/mobile")
    suspend fun deleteAlert(
        @Header("Authorization") token: String,
        @Query("id") alertId: String
    ): DeleteResponse

    // ==================== OCR ====================
    @Multipart
    @POST("ocr")
    suspend fun scanPrescription(
        @Header("Authorization") token: String,
        @Part image: okhttp3.MultipartBody.Part
    ): OcrResponse

    // ==================== Push Notifications ====================
    @POST("push-tokens/mobile")
    suspend fun registerPushToken(
        @Header("Authorization") token: String,
        @Body request: RegisterPushTokenRequest
    ): PushTokenResponse

    @DELETE("push-tokens/mobile")
    suspend fun deletePushToken(
        @Header("Authorization") token: String,
        @Query("token") pushToken: String
    ): PushTokenResponse
}

data class ReportRequest(
    val pharmacyId: String,
    val medicationId: String,
    val status: String
)

data class OcrResponse(
    val medications: List<OcrMedication>
)

data class OcrMedication(
    val name: String,
    val dosage: String?,
    val quantity: String?,
    val matchedMedication: Medication?
)
