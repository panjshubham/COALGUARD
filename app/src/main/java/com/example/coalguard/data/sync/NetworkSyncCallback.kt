package com.example.coalguard.data.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NetworkSyncCallback(private val context: Context) : ConnectivityManager.NetworkCallback() {
    private val syncManager = OfflineSyncManager(context)

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        Log.d("NetworkSyncCallback", "Network became available! Triggering automatic background sync queue...")
        CoroutineScope(Dispatchers.IO).launch {
            syncManager.processSyncQueue()
        }
    }

    fun register() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        connectivityManager?.registerDefaultNetworkCallback(this)
    }

    fun unregister() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        connectivityManager?.unregisterNetworkCallback(this)
    }
}
