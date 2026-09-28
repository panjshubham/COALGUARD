package com.example.coalguard.domain

import android.content.Context
import com.example.coalguard.data.repository.MinesRepository

data class GeofenceAuditResult(
    val isWithinBoundary: Boolean,
    val distanceMeters: Double,
    val auditStatus: String,
    val metadataPayload: Map<String, String>
)

class GeofenceValidationEngine(context: Context) {
    private val minesRepository = MinesRepository(context)

    suspend fun validateInspectionLocation(
        mineId: Int,
        inspectorLat: Double,
        inspectorLng: Double,
        timestamp: String
    ): GeofenceAuditResult {
        val isWithin = minesRepository.isWithinMineGeofence(mineId, inspectorLat, inspectorLng)
        val status = if (isWithin) "VERIFIED_LOCATION" else "FLAGGED_PROXY_AUDIT"

        val metadata = mapOf(
            "mine_id" to mineId.toString(),
            "inspector_lat" to inspectorLat.toString(),
            "inspector_lng" to inspectorLng.toString(),
            "timestamp" to timestamp,
            "geofence_status" to status
        )

        return GeofenceAuditResult(
            isWithinBoundary = isWithin,
            distanceMeters = 0.0,
            auditStatus = status,
            metadataPayload = metadata
        )
    }
}
