package com.example.coalguard.data.local

import androidx.room.*
import com.example.coalguard.data.local.entity.AlertEntity
import com.example.coalguard.data.model.*
import com.example.coalguard.data.model.relations.InspectionWithContractor
import com.example.coalguard.data.model.relations.MineWithViolations
import com.example.coalguard.data.model.relations.ViolationWithMine
import kotlinx.coroutines.flow.Flow

@Dao
interface CoalGuardDao {
    // Violations
    @Query("SELECT * FROM violations")
    fun getAllViolations(): Flow<List<Violation>>

    @Transaction
    @Query("SELECT * FROM violations")
    fun getViolationsWithMines(): Flow<List<ViolationWithMine>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViolation(violation: Violation)

    @Query("SELECT * FROM violations WHERE id = :id")
    suspend fun getViolationById(id: Int): Violation?

    // Compliance Items
    @Query("SELECT * FROM compliance_items")
    fun getAllComplianceItems(): Flow<List<ComplianceItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplianceItem(item: ComplianceItem)

    // Pit Inspector Logs
    @Query("SELECT * FROM pit_inspector_logs ORDER BY timestamp DESC")
    fun getAllPitLogs(): Flow<List<PitInspectorLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPitLog(log: PitInspectorLog)

    // Inspections
    @Query("SELECT * FROM inspections")
    fun getAllInspections(): Flow<List<Inspection>>

    @Transaction
    @Query("SELECT * FROM inspections")
    fun getInspectionsWithContractors(): Flow<List<InspectionWithContractor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspection(inspection: Inspection)

    @Query("SELECT * FROM inspections WHERE id = :id")
    suspend fun getInspectionById(id: Int): Inspection?

    // Mines Master Data
    @Query("SELECT * FROM mines")
    fun getAllMines(): Flow<List<Mine>>

    @Transaction
    @Query("SELECT * FROM mines WHERE id = :mineId")
    fun getMineWithViolations(mineId: Int): Flow<MineWithViolations?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMine(mine: Mine)

    @Query("SELECT * FROM mines WHERE id = :id")
    suspend fun getMineById(id: Int): Mine?

    // Profiles / Users
    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getProfileById(id: String): Profile?

    @Query("SELECT * FROM profiles WHERE id = :id")
    fun getProfileFlowById(id: String): Flow<Profile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: Profile)

    // System Alerts / Notifications (Persisted)
    @Query("SELECT * FROM system_alerts ORDER BY created_at DESC")
    fun getAllAlerts(): Flow<List<SystemAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: SystemAlertEntity)

    @Query("UPDATE system_alerts SET is_read = 1 WHERE id = :alertId")
    suspend fun markAlertAsRead(alertId: String)

    // Contractors
    @Query("SELECT * FROM contractors")
    fun getAllContractors(): Flow<List<Contractor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContractor(contractor: Contractor)

    @Query("SELECT * FROM contractors WHERE id = :id")
    suspend fun getContractorById(id: Int): Contractor?

    // Statutory Forms / Registers
    @Query("SELECT * FROM statutory_forms ORDER BY generated_at DESC")
    fun getAllStatutoryForms(): Flow<List<StatutoryForm>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatutoryForm(form: StatutoryForm)

    @Query("SELECT * FROM statutory_registers ORDER BY created_at DESC")
    fun getAllStatutoryRegisters(): Flow<List<StatutoryRegister>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatutoryRegister(register: StatutoryRegister)

    // Risk Scores
    @Query("SELECT * FROM risk_scores")
    fun getAllRiskScores(): Flow<List<RiskScore>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRiskScore(riskScore: RiskScore)

    // Water Inrush Test Records
    @Query("SELECT * FROM water_test_records ORDER BY timestamp DESC")
    fun getAllWaterTestRecords(): Flow<List<WaterTestRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterTestRecord(record: WaterTestRecord): Long

    @Query("SELECT * FROM water_test_records WHERE is_synced = 0")
    suspend fun getUnsyncedWaterRecords(): List<WaterTestRecord>

    @Query("UPDATE water_test_records SET is_synced = 1 WHERE id = :id")
    suspend fun markWaterRecordSynced(id: Long)

    // Audit Ledger
    @Query("SELECT * FROM audit_ledger ORDER BY created_at DESC")
    fun getAllAuditEntries(): Flow<List<AuditLedgerEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditEntry(entry: AuditLedgerEntry)

    @Query("SELECT data_hash FROM audit_ledger ORDER BY created_at DESC LIMIT 1")
    suspend fun getLatestDataHash(): String?

    // Sync Queue
    @Query("SELECT * FROM cg_sync_queue ORDER BY timestamp ASC")
    suspend fun getAllSyncQueueItems(): List<SyncQueueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncQueueItem(item: SyncQueueEntity)

    @Query("DELETE FROM cg_sync_queue WHERE id = :id")
    suspend fun deleteSyncQueueItem(id: String)
}
