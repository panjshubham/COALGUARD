package com.example.coalguard.data.repository

import android.content.Context
import android.util.Log
import com.example.coalguard.data.local.CoalGuardDatabase
import com.example.coalguard.data.model.Profile
import com.example.coalguard.data.remote.SupabaseClientInstance
import com.example.coalguard.data.sync.ConnectivityHelper
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProfilesRepository(private val context: Context) {
    private val dao = CoalGuardDatabase.getDatabase(context).coalGuardDao()

    fun getProfile(userId: String): Flow<Profile?> = flow {
        if (ConnectivityHelper.isNetworkAvailable(context)) {
            try {
                val remoteProfile = SupabaseClientInstance.client.postgrest["profiles"]
                    .select {
                        filter { eq("id", userId) }
                    }
                    .decodeSingleOrNull<Profile>()

                if (remoteProfile != null) {
                    dao.insertProfile(remoteProfile)
                    emit(remoteProfile)
                    return@flow
                }
            } catch (e: Exception) {
                Log.e("ProfilesRepository", "Error fetching user profile from Supabase", e)
            }
        }

        dao.getProfileFlowById(userId).collect { cached ->
            emit(cached)
        }
    }
}
