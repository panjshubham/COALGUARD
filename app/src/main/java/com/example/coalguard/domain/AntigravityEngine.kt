package com.example.coalguard.domain

import kotlinx.serialization.Serializable

@Serializable
data class AntigravitySubsidenceResult(
    val mineId: Int,
    val mineName: String,
    val subsidenceRateMmPerYear: Double,
    val benchTiltAngleDeg: Double,
    val insarCoherenceScore: Double,
    val stabilityStatus: String,
    val advisoryNotice: String,
    val isCriticalWarning: Boolean
)

class AntigravityEngine {

    fun evaluateBenchSubsidence(
        mineId: Int,
        mineName: String,
        subsidenceRateMmPerYear: Double,
        benchTiltAngleDeg: Double,
        insarCoherenceScore: Double
    ): AntigravitySubsidenceResult {
        val isCritical = subsidenceRateMmPerYear > 25.0 || benchTiltAngleDeg > 12.0 || insarCoherenceScore < 0.65

        val status = when {
            subsidenceRateMmPerYear > 25.0 || benchTiltAngleDeg > 12.0 -> "CRITICAL_SUBSIDENCE_ALERT"
            subsidenceRateMmPerYear > 10.0 || benchTiltAngleDeg > 5.0 -> "ELEVATED_GROUND_MOVEMENT"
            else -> "STABLE_IN_BENCH_BOUNDS"
        }

        val advisory = when {
            subsidenceRateMmPerYear > 25.0 -> "⚠️ CMR 2017 Reg 108 Warning: Active bench slope deformation detected by satellite InSAR radar (>25mm/yr). Evacuate heavy machinery."
            subsidenceRateMmPerYear > 10.0 -> "Notice: Moderate ground displacement observed. Re-calibrate total station laser prisms."
            else -> "InSAR Satellite Ground Stability Nominal (Coherence: ${(insarCoherenceScore * 100).toInt()}%)."
        }

        return AntigravitySubsidenceResult(
            mineId = mineId,
            mineName = mineName,
            subsidenceRateMmPerYear = subsidenceRateMmPerYear,
            benchTiltAngleDeg = benchTiltAngleDeg,
            insarCoherenceScore = insarCoherenceScore,
            stabilityStatus = status,
            advisoryNotice = advisory,
            isCriticalWarning = isCritical
        )
    }
}
