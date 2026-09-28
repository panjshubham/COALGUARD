package com.example.coalguard.data.repository

import android.content.Context
import android.util.Log
import com.example.coalguard.data.local.CoalGuardDatabase
import com.example.coalguard.data.model.AuditLedgerEntry
import com.example.coalguard.data.model.StatutoryRegister
import com.example.coalguard.data.remote.SupabaseClientInstance
import com.example.coalguard.data.sync.ConnectivityHelper
import com.example.coalguard.data.sync.OfflineSyncManager
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StatutoryRepository(private val context: Context) {
    private val dao = CoalGuardDatabase.getDatabase(context).coalGuardDao()
    private val syncManager = OfflineSyncManager(context)

    fun getAuditEntries(): Flow<List<AuditLedgerEntry>> = flow {
        if (ConnectivityHelper.isNetworkAvailable(context)) {
            try {
                val remoteEntries = SupabaseClientInstance.client.postgrest["audit_ledger"]
                    .select()
                    .decodeList<AuditLedgerEntry>()

                remoteEntries.forEach { dao.insertAuditEntry(it) }
                emit(remoteEntries)
                return@flow
            } catch (e: Exception) {
                Log.e("StatutoryRepository", "Error fetching audit_ledger from Supabase", e)
            }
        }

        dao.getAllAuditEntries().collect { cached ->
            emit(cached)
        }
    }

    suspend fun recordAuditEntry(entry: AuditLedgerEntry) {
        if (ConnectivityHelper.isNetworkAvailable(context)) {
            try {
                SupabaseClientInstance.client.postgrest["audit_ledger"].insert(entry)
                dao.insertAuditEntry(entry)
            } catch (e: Exception) {
                Log.e("StatutoryRepository", "Failed direct audit insert, queueing offline", e)
                dao.insertAuditEntry(entry)
                syncManager.queueMutation("audit_ledger", "POST", entry)
            }
        } else {
            dao.insertAuditEntry(entry)
            syncManager.queueMutation("audit_ledger", "POST", entry)
        }
    }
}
