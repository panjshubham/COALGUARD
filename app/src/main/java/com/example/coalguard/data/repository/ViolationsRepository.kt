package com.example.coalguard.data.repository

import android.content.Context
import android.util.Log
import com.example.coalguard.data.local.CoalGuardDatabase
import com.example.coalguard.data.model.Violation
import com.example.coalguard.data.remote.SupabaseClientInstance
import com.example.coalguard.data.sync.ConnectivityHelper
import com.example.coalguard.data.sync.OfflineSyncManager
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ViolationsRepository(private val context: Context) {
    private val dao = CoalGuardDatabase.getDatabase(context).coalGuardDao()
    private val syncManager = OfflineSyncManager(context)

    fun getViolations(): Flow<List<Violation>> = flow {
        if (ConnectivityHelper.isNetworkAvailable(context)) {
            try {
                // Try fetching live violations from Supabase
                val remoteViolations = SupabaseClientInstance.client.postgrest["violations"]
                    .select()
                    .decodeList<Violation>()

                // Cache in local Room DB
                remoteViolations.forEach { dao.insertViolation(it) }
                emit(remoteViolations)
                return@flow
            } catch (e: Exception) {
                Log.e("ViolationsRepository", "Failed to fetch from Supabase, falling back to local Room cache", e)
            }
        }

        // Offline or fallback path: collect from Room cache
        dao.getAllViolations().collect { cached ->
            emit(cached)
        }
    }

    suspend fun createViolation(violation: Violation) {
        if (ConnectivityHelper.isNetworkAvailable(context)) {
            try {
                // Insert directly to Supabase
                SupabaseClientInstance.client.postgrest["violations"].insert(violation)
                dao.insertViolation(violation)
            } catch (e: Exception) {
                Log.e("ViolationsRepository", "Failed direct insert, queueing offline", e)
                dao.insertViolation(violation)
                syncManager.queueMutation("violations", "POST", violation)
            }
        } else {
            // Save to Room & queue offline mutation
            dao.insertViolation(violation)
            syncManager.queueMutation("violations", "POST", violation)
        }
    }
}
