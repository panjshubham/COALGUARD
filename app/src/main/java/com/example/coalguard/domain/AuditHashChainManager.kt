package com.example.coalguard.domain

import android.content.Context
import com.example.coalguard.data.local.CoalGuardDatabase
import com.example.coalguard.data.model.AuditLedgerEntry
import com.example.coalguard.data.sync.OfflineSyncManager
import com.google.gson.Gson
import java.security.MessageDigest

class AuditHashChainManager(context: Context) {
    private val dao = CoalGuardDatabase.getDatabase(context).coalGuardDao()
    private val syncManager = OfflineSyncManager(context)
    private val gson = Gson()

    suspend fun computeAndRecordHash(recordId: Int, recordType: String, recordData: Any): AuditLedgerEntry {
        val jsonString = gson.toJson(recordData)
        val newHash = sha256(jsonString)
        val prevHash = dao.getLatestDataHash()
        val generatedId = (100000..999999).random()

        val entry = AuditLedgerEntry(
            id = generatedId,
            tableName = recordType,
            recordId = recordId,
            action = "INSERT",
            dataHash = newHash,
            prevHash = prevHash,
            createdAt = System.currentTimeMillis().toString()
        )

        dao.insertAuditEntry(entry)
        syncManager.queueMutation("audit_ledger", "POST", entry)

        return entry
    }

    fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
