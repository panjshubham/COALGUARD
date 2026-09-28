package com.example.coalguard.data.repository

import com.example.coalguard.data.remote.AIServiceApi
import com.example.coalguard.data.remote.PredictionRequest
import com.example.coalguard.data.remote.PredictionResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RiskAssessmentRepository(baseUrl: String = "https://ai.coalguard.internal/") {
    private val api: AIServiceApi by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AIServiceApi::class.java)
    }

    suspend fun checkHealth(): Boolean {
        return try {
            val response = api.checkHealth()
            response.isSuccessful && response.body()?.status == "ok"
        } catch (e: Exception) {
            false
        }
    }

    suspend fun batchPredict(featuresList: List<Map<String, Float>>): List<PredictionResponse>? {
        return try {
            val requests = featuresList.map { PredictionRequest(it) }
            val response = api.batchPredict(requests)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }
}
