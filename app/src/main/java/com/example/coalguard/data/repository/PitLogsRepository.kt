package com.example.coalguard.data.repository

import android.content.Context
import android.util.Log
import com.example.coalguard.data.local.CoalGuardDatabase
import com.example.coalguard.data.model.PitInspectorLog
import com.example.coalguard.data.remote.SupabaseClientInstance
import com.example.coalguard.data.sync.ConnectivityHelper
import com.example.coalguard.data.sync.OfflineSyncManager
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PitLogsRepository(private val context: Context) {
    private val dao = CoalGuardDatabase.getDatabase(context).coalGuardDao()
    private val syncManager = OfflineSyncManager(context)

    fun getPitLogs(): Flow<List<PitInspectorLog>> = flow {
        if (ConnectivityHelper.isNetworkAvailable(context)) {
            try {
                val remoteLogs = SupabaseClientInstance.client.postgrest["pit_inspector_logs"]
                    .select()
                    .decodeList<PitInspectorLog>()

                remoteLogs.forEach { dao.insertPitLog(it) }
                emit(remoteLogs)
                return@flow
            } catch (e: Exception) {
                Log.e("PitLogsRepository", "Error fetching pit inspector logs from Supabase", e)
            }
        }

        dao.getAllPitLogs().collect { cached ->
            emit(cached)
        }
    }

    suspend fun createPitLog(log: PitInspectorLog) {
        if (ConnectivityHelper.isNetworkAvailable(context)) {
            try {
                SupabaseClientInstance.client.postgrest["pit_inspector_logs"].insert(log)
                dao.insertPitLog(log)
            } catch (e: Exception) {
                Log.e("PitLogsRepository", "Failed direct insert, queueing offline", e)
                dao.insertPitLog(log)
                syncManager.queueMutation("pit_inspector_logs", "POST", log)
            }
        } else {
            dao.insertPitLog(log)
            syncManager.queueMutation("pit_inspector_logs", "POST", log)
        }
    }
}
