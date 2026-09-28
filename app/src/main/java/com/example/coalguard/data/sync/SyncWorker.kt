package com.example.coalguard.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val syncManager = OfflineSyncManager(applicationContext)
            val success = syncManager.processSyncQueue()
            if (success) Result.success() else Result.retry()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
