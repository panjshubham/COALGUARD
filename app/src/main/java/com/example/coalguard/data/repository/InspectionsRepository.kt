package com.example.coalguard.data.repository

import android.content.Context
import android.util.Log
import com.example.coalguard.data.local.CoalGuardDatabase
import com.example.coalguard.data.model.Inspection
import com.example.coalguard.data.remote.SupabaseClientInstance
import com.example.coalguard.data.sync.ConnectivityHelper
import com.example.coalguard.data.sync.OfflineSyncManager
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InspectionsRepository(private val context: Context) {
    private val dao = CoalGuardDatabase.getDatabase(context).coalGuardDao()
    private val syncManager = OfflineSyncManager(context)

    fun getInspections(): Flow<List<Inspection>> = flow {
        if (ConnectivityHelper.isNetworkAvailable(context)) {
            try {
                val remoteInspections = SupabaseClientInstance.client.postgrest["inspections"]
                    .select()
                    .decodeList<Inspection>()

                remoteInspections.forEach { dao.insertInspection(it) }
                emit(remoteInspections)
                return@flow
            } catch (e: Exception) {
                Log.e("InspectionsRepository", "Error fetching inspections from Supabase", e)
            }
        }

        dao.getAllInspections().collect { cached ->
            emit(cached)
        }
    }

    suspend fun createInspection(inspection: Inspection) {
        if (ConnectivityHelper.isNetworkAvailable(context)) {
            try {
                SupabaseClientInstance.client.postgrest["inspections"].insert(inspection)
                dao.insertInspection(inspection)
            } catch (e: Exception) {
                Log.e("InspectionsRepository", "Failed direct insert, queueing offline", e)
                dao.insertInspection(inspection)
                syncManager.queueMutation("inspections", "POST", inspection)
            }
        } else {
            dao.insertInspection(inspection)
            syncManager.queueMutation("inspections", "POST", inspection)
        }
    }
}
