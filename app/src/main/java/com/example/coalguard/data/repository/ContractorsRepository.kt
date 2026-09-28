package com.example.coalguard.data.repository

import android.content.Context
import android.util.Log
import com.example.coalguard.data.local.CoalGuardDatabase
import com.example.coalguard.data.model.Contractor
import com.example.coalguard.data.remote.SupabaseClientInstance
import com.example.coalguard.data.sync.ConnectivityHelper
import com.example.coalguard.data.sync.OfflineSyncManager
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ContractorsRepository(private val context: Context) {
    private val dao = CoalGuardDatabase.getDatabase(context).coalGuardDao()
    private val syncManager = OfflineSyncManager(context)

    fun getContractors(): Flow<List<Contractor>> = flow {
        if (ConnectivityHelper.isNetworkAvailable(context)) {
            try {
                val remoteContractors = SupabaseClientInstance.client.postgrest["contractors"]
                    .select()
                    .decodeList<Contractor>()

                remoteContractors.forEach { dao.insertContractor(it) }
                emit(remoteContractors)
                return@flow
            } catch (e: Exception) {
                Log.e("ContractorsRepository", "Error fetching contractors from Supabase", e)
            }
        }

        dao.getAllContractors().collect { cached ->
            emit(cached)
        }
    }

    suspend fun createContractor(contractor: Contractor) {
        if (ConnectivityHelper.isNetworkAvailable(context)) {
            try {
                SupabaseClientInstance.client.postgrest["contractors"].insert(contractor)
                dao.insertContractor(contractor)
            } catch (e: Exception) {
                Log.e("ContractorsRepository", "Failed direct insert, queueing offline", e)
                dao.insertContractor(contractor)
                syncManager.queueMutation("contractors", "POST", contractor)
            }
        } else {
            dao.insertContractor(contractor)
            syncManager.queueMutation("contractors", "POST", contractor)
        }
    }
}
