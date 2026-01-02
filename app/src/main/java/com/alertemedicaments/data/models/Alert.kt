package com.alertemedicaments.data.models

data class Alert(
    val id: String,
    val userId: String,
    val medicationId: String,
    val medication: Medication?,
    val notifyOnAvailable: Boolean = true,
    val notifyOnTension: Boolean = true,
    val notifyOnRupture: Boolean = true,
    val createdAt: String? = null
)

data class AlertsResponse(
    val alerts: List<Alert>
)

data class CreateAlertRequest(
    val medicationId: String,
    val notifyOnAvailable: Boolean = true,
    val notifyOnTension: Boolean = true,
    val notifyOnRupture: Boolean = true
)

data class AlertResponse(
    val alert: Alert
)

data class DeleteResponse(
    val success: Boolean
)
