package com.alertemedicaments.data.api

import com.alertemedicaments.data.models.*
import retrofit2.http.*

interface ApiService {

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
}

data class ReportRequest(
    val pharmacyId: String,
    val medicationId: String,
    val status: String
)
