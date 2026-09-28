package com.example.coalguard.data.remote

import com.example.coalguard.data.model.WaterInrushResponse
import com.example.coalguard.data.model.WaterSampleRequest
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.IOException
import java.util.concurrent.TimeUnit

interface AIServiceApi {
    @GET("health")
    suspend fun checkHealth(): Response<HealthResponse>

    @POST("batch-predict")
    suspend fun batchPredict(@Body requests: List<PredictionRequest>): Response<List<PredictionResponse>>

    @POST("water-inrush/predict")
    suspend fun predictInrush(@Body request: WaterSampleRequest): Response<WaterInrushResponse>

    @Multipart
    @POST("api/ppe-detect")
    suspend fun detectPpe(@Part file: MultipartBody.Part): Response<PpeDetectionResponse>

    @Multipart
    @POST("api/ocr-trocr")
    suspend fun extractOcrText(@Part file: MultipartBody.Part): Response<OcrResponse>

    @Multipart
    @POST("api/donut-extract")
    suspend fun extractDonutJson(@Part file: MultipartBody.Part): Response<DonutResponse>

    @POST("api/classify-compliance")
    suspend fun classifyCompliance(@Body request: ClassifyComplianceRequest): Response<ClassifyResponse>

    @POST("api/extract-entities")
    suspend fun extractEntities(@Body request: EntityExtractionRequest): Response<EntityExtractionResponse>

    @POST("api/translate")
    suspend fun translateText(@Body request: TranslateTextRequest): Response<TranslateResponse>

    @Multipart
    @POST("api/transcribe")
    suspend fun transcribeAudio(@Part file: MultipartBody.Part): Response<TranscribeResponse>

    @Multipart
    @POST("api/cv/berm-analysis")
    suspend fun analyzeBerm(
        @Part file: MultipartBody.Part,
        @Part("dumper_wheel_dia_m") dumperWheelDiaM: RequestBody
    ): Response<BermAnalysisResponse>
}

class HostFailoverInterceptor : Interceptor {
    private val hostCandidates = listOf(
        "192.168.31.34", // Candidate #1: Active PC Wi-Fi IP matching Phone Subnet 192.168.31.x
        "127.0.0.1",     // Candidate #2: ADB reverse port forwarded over USB
        "192.168.0.56",  // Candidate #3: Alternate PC Wi-Fi IP
        "10.0.2.2"       // Candidate #4: Android Studio Emulator Alias
    )

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url()

        var lastException: Exception? = null

        for (host in hostCandidates) {
            val newUrl = originalUrl.newBuilder()
                .host(host)
                .port(8000)
                .build()

            val newRequest = originalRequest.newBuilder()
                .url(newUrl)
                .build()

            try {
                val response = chain.proceed(newRequest)
                if (response.isSuccessful || response.code() in 400..499) {
                    return response
                }
            } catch (e: Exception) {
                lastException = e
            }
        }

        throw lastException ?: IOException("Failed to connect to AI Service on candidates: $hostCandidates")
    }
}

object RetrofitClient {
    var baseUrl = "http://192.168.31.34:8000/"

    val apiService: AIServiceApi by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor(HostFailoverInterceptor())
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AIServiceApi::class.java)
    }
}

data class PpeDetectedItem(
    val label: String? = null,
    val score: Double? = 0.0
)

data class PpeDetectionResponse(
    val model: String? = null,
    val filename: String? = null,
    val compliance_status: String? = null,
    val severity: String? = null,
    val detected_items: List<PpeDetectedItem>? = null,
    val missing_ppe: List<String>? = null,
    val alert: String? = null,
    val timestamp: String? = null
)

data class OcrResponse(
    val model: String? = null,
    val filename: String? = null,
    val extracted_text: String? = null,
    val confidence_pct: Int? = 0,
    val detected_dates: List<String>? = null,
    val word_count: Int? = 0,
    val timestamp: String? = null
)

data class DonutResponse(
    val model: String? = null,
    val filename: String? = null,
    val structured_output: Map<String, Any>? = null,
    val timestamp: String? = null
)

data class ClassifyComplianceRequest(val text: String)

data class ClassifyResponse(
    val model: String? = null,
    val input_text: String? = null,
    val top_category: String? = null,
    val confidence: Double? = 0.0,
    val timestamp: String? = null
)

data class EntityExtractionRequest(val text: String)

data class EntityItem(
    val entity_group: String? = null,
    val word: String? = null,
    val score: Double? = 0.0
)

data class EntityExtractionResponse(
    val model: String? = null,
    val entities: List<EntityItem>? = null,
    val timestamp: String? = null
)

data class TranslateTextRequest(val text: String, val target_language: String)

data class TranslateResponse(
    val model: String? = null,
    val source_text: String? = null,
    val target_language: String? = null,
    val translated_text: String? = null,
    val timestamp: String? = null
)

data class TranscribeResponse(
    val model: String? = null,
    val filename: String? = null,
    val transcribed_text: String? = null,
    val timestamp: String? = null
)

data class BermAnalysisResponse(
    val model: String? = null,
    val filename: String? = null,
    val measured_berm_height_m: Double? = 0.0,
    val statutory_required_height_m: Double? = 0.0,
    val compliance_status: String? = null,
    val defect_type: String? = null,
    val statutory_regulation: String? = null,
    val findings: List<String>? = null,
    val recommended_action: String? = null,
    val timestamp: String? = null
)
