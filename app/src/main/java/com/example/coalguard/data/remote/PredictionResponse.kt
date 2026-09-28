package com.example.coalguard.data.remote

import com.google.gson.annotations.SerializedName

data class PredictionResponse(
    @SerializedName("prediction")
    val prediction: Float, // risk score 0-100
    @SerializedName("explanation")
    val explanation: String,
    @SerializedName("contributing_factors")
    val contributingFactors: Map<String, Float>
)

data class HealthResponse(
    @SerializedName("status")
    val status: String
)
