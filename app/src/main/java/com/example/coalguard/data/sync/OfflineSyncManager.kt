package com.example.coalguard.data.sync

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.coalguard.data.local.CoalGuardDatabase
import com.example.coalguard.data.local.SyncQueueEntity
import com.example.coalguard.data.remote.SupabaseClientInstance
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID

class OfflineSyncManager(private val context: Context) {
    private val dao = CoalGuardDatabase.getDatabase(context).coalGuardDao()
    private val gson = Gson()

    suspend fun queueMutation(endpoint: String, method: String, payload: Any) {
        withContext(Dispatchers.IO) {
            val payloadJson = gson.toJson(payload)
            val entity = SyncQueueEntity(
                id = UUID.randomUUID().toString(),
                endpoint = endpoint,
                method = method.uppercase(),
                payloadJson = payloadJson,
                timestamp = System.currentTimeMillis().toString()
            )
            dao.insertSyncQueueItem(entity)
            Log.d("OfflineSyncManager", "Queued mutation [$method] for $endpoint: $payloadJson")
        }
    }

    suspend fun processSyncQueue(): Boolean {
        if (!ConnectivityHelper.isNetworkAvailable(context)) {
            Log.d("OfflineSyncManager", "Device is offline. Skipping sync queue processing.")
            return false
        }

        return withContext(Dispatchers.IO) {
            val queueItems = dao.getAllSyncQueueItems()
            if (queueItems.isEmpty()) {
                return@withContext true
            }

            Log.d("OfflineSyncManager", "Processing ${queueItems.size} pending items in sync queue...")
            var allSuccessful = true

            for (item in queueItems) {
                try {
                    val success = syncSingleItem(item)
                    if (success) {
                        dao.deleteSyncQueueItem(item.id)
                        Log.d("OfflineSyncManager", "Successfully synced item #${item.id}")
                    } else {
                        allSuccessful = false
                        Log.w("OfflineSyncManager", "Failed to sync item #${item.id}")
                    }
                } catch (e: Exception) {
                    allSuccessful = false
                    Log.e("OfflineSyncManager", "Error syncing item #${item.id}", e)
                }
            }

            if (queueItems.isNotEmpty()) {
                val intent = Intent("coalguard:syncComplete")
                context.sendBroadcast(intent)
            }

            allSuccessful
        }
    }

    private suspend fun syncSingleItem(item: SyncQueueEntity): Boolean {
        return try {
            val jsonObject = gson.fromJson(item.payloadJson, JsonObject::class.java)

            // Clean up endpoint-specific non-remote columns to prevent PGRST204 errors
            if (item.endpoint.contains("violations", ignoreCase = true)) {
                jsonObject.remove("data_hash")
                jsonObject.remove("prev_hash")
                jsonObject.remove("dataHash")
                jsonObject.remove("prevHash")
                if (!jsonObject.has("mine_id") || jsonObject.get("mine_id").asInt == 1) {
                    jsonObject.addProperty("mine_id", 42)
                }
            }

            if (item.endpoint.contains("inspections", ignoreCase = true)) {
                jsonObject.remove("type")
                jsonObject.remove("scheduled_date")
                jsonObject.remove("status")
                jsonObject.remove("scheduledDate")
                if (!jsonObject.has("mine_id") || jsonObject.get("mine_id").asInt == 1) {
                    jsonObject.addProperty("mine_id", 42)
                }
                if (!jsonObject.has("contractor_id")) {
                    jsonObject.addProperty("contractor_id", 9)
                }
            }

            // Upload photo if local photo path exists in payload
            val photoLocalPath = if (jsonObject.has("photo_local_path")) jsonObject.get("photo_local_path").asString else null
            if (!photoLocalPath.isNullOrEmpty()) {
                val file = File(photoLocalPath)
                if (file.exists()) {
                    val fileName = "${UUID.randomUUID()}_${file.name}"
                    val bucket = SupabaseClientInstance.client.storage.from("photos")
                    bucket.upload(fileName, file.readBytes())
                    val publicUrl = bucket.publicUrl(fileName)
                    jsonObject.addProperty("photo_url", publicUrl)
                    jsonObject.remove("photo_local_path")
                }
            }

            val finalPayload = jsonObject.toString()
            val jsonElement = Json.parseToJsonElement(finalPayload)
            val table = SupabaseClientInstance.client.postgrest[item.endpoint]

            when (item.method) {
                "POST" -> {
                    table.insert(jsonElement)
                    true
                }
                "PATCH", "PUT" -> {
                    val recordId = if (jsonObject.has("id")) jsonObject.get("id").asString else ""
                    table.update(jsonElement) {
                        filter { eq("id", recordId) }
                    }
                    true
                }
                "DELETE" -> {
                    val recordId = if (jsonObject.has("id")) jsonObject.get("id").asString else ""
                    table.delete {
                        filter { eq("id", recordId) }
                    }
                    true
                }
                else -> {
                    table.insert(jsonElement)
                    true
                }
            }
        } catch (e: Exception) {
            Log.e("OfflineSyncManager", "Single item sync error: ${e.message}", e)
            false
        }
    }
}
