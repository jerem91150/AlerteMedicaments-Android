package com.alertemedicaments.data.models

data class Pharmacy(
    val id: String,
    val name: String,
    val address: String,
    val city: String,
    val postalCode: String,
    val phone: String?,
    val latitude: Double?,
    val longitude: Double?,
    val isOnDuty: Boolean,
    val distance: Double?,
    val availability: PharmacyAvailability?
) {
    val fullAddress: String
        get() = "$address, $postalCode $city"
}

data class PharmacyAvailability(
    val status: String,
    val quantity: Int?,
    val price: Double?,
    val reportedAt: String,
    val verifiedBy: Int
)

data class PharmaciesResponse(
    val pharmacies: List<Pharmacy>,
    val total: Int
)
