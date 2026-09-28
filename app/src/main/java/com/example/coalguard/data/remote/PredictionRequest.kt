package com.example.coalguard.data.remote

import com.google.gson.annotations.SerializedName

data class PredictionRequest(
    @SerializedName("features")
    val features: Map<String, Float>
)
