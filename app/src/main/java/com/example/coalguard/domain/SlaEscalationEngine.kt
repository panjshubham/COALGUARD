package com.example.coalguard.domain

import com.example.coalguard.data.model.Violation
import java.util.concurrent.TimeUnit

data class SlaEscalationResult(
    val violationId: String,
    val slaHours: Int,
    val hoursRemaining: Long,
    val isBreached: Boolean,
    val escalatedRole: String
)

class SlaEscalationEngine {

    fun evaluateSlaAndEscalation(violation: Violation, currentTimeMs: Long = System.currentTimeMillis()): SlaEscalationResult {
        val severity = violation.severity.lowercase()
        val category = (violation.title + " " + violation.description).lowercase()

        val slaHours = when {
            severity == "critical" -> 2
            severity == "high" || category.contains("ventilation") || category.contains("machinery") -> 8
            else -> 72
        }

        val createdAtMs = violation.createdAt?.toLongOrNull() ?: currentTimeMs
        val elapsedMs = currentTimeMs - createdAtMs
        val elapsedHours = TimeUnit.MILLISECONDS.toHours(elapsedMs)
        val hoursRemaining = slaHours - elapsedHours
        val isBreached = elapsedHours > slaHours

        val escalatedRole = when {
            elapsedHours >= slaHours * 3 -> "DGMS Regulator"
            elapsedHours >= slaHours * 2 -> "General Manager"
            elapsedHours >= slaHours -> "Colliery Manager"
            else -> "Shift Overman"
        }

        return SlaEscalationResult(
            violationId = violation.id.toString(),
            slaHours = slaHours,
            hoursRemaining = hoursRemaining,
            isBreached = isBreached,
            escalatedRole = escalatedRole
        )
    }
}
