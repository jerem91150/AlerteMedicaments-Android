package com.alertemedicaments.data.models

import com.google.gson.annotations.SerializedName

enum class MedicationStatus {
    @SerializedName("AVAILABLE") AVAILABLE,
    @SerializedName("TENSION") TENSION,
    @SerializedName("RUPTURE") RUPTURE,
    @SerializedName("UNKNOWN") UNKNOWN;

    val displayName: String
        get() = when (this) {
            AVAILABLE -> "Disponible"
            TENSION -> "Tension"
            RUPTURE -> "Rupture"
            UNKNOWN -> "Inconnu"
        }
}

data class Medication(
    val id: String,
    val cisCode: String? = null,
    val name: String,
    val laboratory: String? = null,
    val activeIngredient: String? = null,
    val dosage: String? = null,
    val form: String? = null,
    val status: MedicationStatus = MedicationStatus.UNKNOWN,
    val lastChecked: String? = null,
    val expectedReturn: String? = null
)

data class MedicationSearchResponse(
    val medications: List<Medication>,
    val total: Int? = null
)

data class SuggestionsResponse(
    val suggestions: List<Medication>
)

data class Alternative(
    val id: String,
    val cisCode: String,
    val name: String,
    val laboratory: String?,
    val activeIngredient: String?,
    val status: String,
    val matchType: String
)

data class AlternativesResponse(
    val medication: MedicationBasic,
    val alternatives: List<Alternative>,
    val total: Int
)

data class MedicationBasic(
    val id: String,
    val name: String,
    val activeIngredient: String?,
    val status: String
)
