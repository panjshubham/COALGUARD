package com.example.coalguard.data.repository

import android.content.Context
import android.util.Log
import com.example.coalguard.data.local.CoalGuardDatabase
import com.example.coalguard.data.model.ComplianceItem
import com.example.coalguard.data.model.SystemAlertEntity
import com.example.coalguard.data.remote.SupabaseClientInstance
import com.example.coalguard.data.sync.ConnectivityHelper
import com.example.coalguard.data.sync.OfflineSyncManager
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ComplianceRepository(private val context: Context) {
    private val dao = CoalGuardDatabase.getDatabase(context).coalGuardDao()
    private val syncManager = OfflineSyncManager(context)

    fun getComplianceItems(): Flow<List<ComplianceItem>> = flow {
        if (ConnectivityHelper.isNetworkAvailable(context)) {
            try {
                val remoteItems = SupabaseClientInstance.client.postgrest["compliance_items"]
                    .select()
                    .decodeList<ComplianceItem>()

                remoteItems.forEach { dao.insertComplianceItem(it) }
                emit(remoteItems)
                return@flow
            } catch (e: Exception) {
                Log.e("ComplianceRepository", "Error fetching compliance_items from Supabase", e)
            }
        }

        dao.getAllComplianceItems().collect { cached ->
            emit(cached)
        }
    }

    suspend fun createComplianceItem(item: ComplianceItem) {
        if (ConnectivityHelper.isNetworkAvailable(context)) {
            try {
                SupabaseClientInstance.client.postgrest["compliance_items"].insert(item)
                dao.insertComplianceItem(item)
            } catch (e: Exception) {
                Log.e("ComplianceRepository", "Failed direct insert, queueing offline", e)
                dao.insertComplianceItem(item)
                syncManager.queueMutation("compliance_items", "POST", item)
            }
        } else {
            dao.insertComplianceItem(item)
            syncManager.queueMutation("compliance_items", "POST", item)
        }
    }

    fun getAlerts(): Flow<List<SystemAlertEntity>> = dao.getAllAlerts()
}
