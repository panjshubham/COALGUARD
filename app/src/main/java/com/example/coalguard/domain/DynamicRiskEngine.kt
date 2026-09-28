package com.example.coalguard.domain

import com.example.coalguard.data.model.ComplianceItem
import com.example.coalguard.data.model.Violation
import kotlin.math.min

data class RiskFactors(
    val methaneGasPct: Float = 0.5f,
    val ventilationAirSpeedMps: Float = 1.5f,
    val roofInstabilityReported: Boolean = false,
    val openViolations: List<Violation> = emptyList(),
    val complianceItems: List<ComplianceItem> = emptyList()
)

data class DynamicRiskResult(
    val riskScore: Float,
    val riskLevel: String,
    val methaneMultiplier: Float,
    val contributingFactors: Map<String, Float>,
    val topAlert: String
)

class DynamicRiskEngine {

    fun calculateRisk(factors: RiskFactors): DynamicRiskResult {
        var baseScore = 10.0f
        val breakdown = mutableMapOf<String, Float>()

        val openCount = factors.openViolations.count { it.status.lowercase() == "open" }
        val criticalCount = factors.openViolations.count { it.status.lowercase() == "open" && (it.severity.lowercase() == "critical" || it.severity.lowercase() == "high") }
        
        val violationScore = min(openCount * 5.0f + criticalCount * 12.0f, 35.0f)
        breakdown["open_violations"] = violationScore
        baseScore += violationScore

        val overdueCount = factors.complianceItems.count { it.status.lowercase() == "overdue" }
        val complianceScore = min(overdueCount * 8.0f, 25.0f)
        breakdown["overdue_checklists"] = complianceScore
        baseScore += complianceScore

        if (factors.roofInstabilityReported) {
            breakdown["roof_instability_hazard"] = 25.0f
            baseScore += 25.0f
        }

        if (factors.ventilationAirSpeedMps < 1.0f) {
            breakdown["ventilation_deficit"] = 15.0f
            baseScore += 15.0f
        }

        val methaneMultiplier = when {
            factors.methaneGasPct >= 1.2f -> 2.0f
            factors.methaneGasPct >= 0.75f -> 1.5f
            else -> 1.0f
        }

        val finalScore = min(baseScore * methaneMultiplier, 100.0f)

        val riskLevel = when {
            finalScore >= 75.0f -> "CRITICAL"
            finalScore >= 50.0f -> "HIGH"
            finalScore >= 30.0f -> "MEDIUM"
            else -> "LOW"
        }

        val topAlert = when {
            factors.methaneGasPct >= 1.2f -> "CRITICAL: Methane CH4 at ${factors.methaneGasPct}% (2.0x Multiplier Applied)"
            factors.roofInstabilityReported -> "CRITICAL: Roof Instability Hazard Active"
            overdueCount > 0 -> "WARNING: $overdueCount Statutory Checklists Overdue"
            else -> "NOMINAL: Operating within DGMS Statutory Safety Limits"
        }

        return DynamicRiskResult(
            riskScore = finalScore,
            riskLevel = riskLevel,
            methaneMultiplier = methaneMultiplier,
            contributingFactors = breakdown,
            topAlert = topAlert
        )
    }
}
