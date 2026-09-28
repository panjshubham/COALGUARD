package com.example.coalguard.data.repository

import android.content.Context
import android.util.Log
import com.example.coalguard.data.local.CoalGuardDatabase
import com.example.coalguard.data.model.Mine
import com.example.coalguard.data.remote.SupabaseClientInstance
import com.example.coalguard.data.sync.ConnectivityHelper
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.*

class MinesRepository(private val context: Context) {
    private val dao = CoalGuardDatabase.getDatabase(context).coalGuardDao()

    fun getMines(): Flow<List<Mine>> = flow {
        if (ConnectivityHelper.isNetworkAvailable(context)) {
            try {
                val remoteMines = SupabaseClientInstance.client.postgrest["mines"]
                    .select()
                    .decodeList<Mine>()

                remoteMines.forEach { dao.insertMine(it) }
                emit(remoteMines)
                return@flow
            } catch (e: Exception) {
                Log.e("MinesRepository", "Error fetching mines from Supabase", e)
            }
        }

        dao.getAllMines().collect { cached ->
            emit(cached)
        }
    }

    suspend fun isWithinMineGeofence(mineId: Int, inspectorLat: Double, inspectorLng: Double): Boolean {
        val mine = dao.getMineById(mineId) ?: return true
        val lat1 = mine.latitude ?: return true
        val lng1 = mine.longitude ?: return true

        val distanceMeters = haversine(lat1, lng1, inspectorLat, inspectorLng)
        return distanceMeters <= mine.radiusM
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val deltaPhi = Math.toRadians(lat2 - lat1)
        val deltaLambda = Math.toRadians(lon2 - lon1)

        val a = sin(deltaPhi / 2.0).pow(2) + cos(phi1) * cos(phi2) * sin(deltaLambda / 2.0).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
