package com.example.coalguard.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.coalguard.data.local.dao.*
import com.example.coalguard.data.local.entity.*
import com.example.coalguard.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Mine::class,
        Contractor::class,
        Inspection::class,
        Violation::class,
        SyncEvent::class,
        AuditLedgerEntry::class,
        RiskScore::class,
        AlertEntity::class,
        ComplianceItem::class,
        ContractorIncidentEntity::class,
        UserEntity::class,
        StatutoryRegister::class,
        PitInspectorLog::class,
        Profile::class,
        SystemAlertEntity::class,
        StatutoryForm::class,
        SyncQueueEntity::class,
        WaterTestRecord::class
    ],
    version = 7,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CoalGuardDatabase : RoomDatabase() {
    abstract fun coalGuardDao(): CoalGuardDao
    abstract fun minesDao(): MinesDao
    abstract fun usersDao(): UsersDao
    abstract fun complianceItemsDao(): ComplianceItemsDao
    abstract fun inspectionsDao(): InspectionsDao
    abstract fun violationsDao(): ViolationsDao
    abstract fun contractorsDao(): ContractorsDao
    abstract fun contractorIncidentsDao(): ContractorIncidentsDao
    abstract fun alertsDao(): AlertsDao
    abstract fun riskScoresDao(): RiskScoresDao

    companion object {
        @Volatile
        private var INSTANCE: CoalGuardDatabase? = null

        fun getDatabase(context: Context): CoalGuardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CoalGuardDatabase::class.java,
                    "coalguard_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                populateDatabase(database.coalGuardDao())
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateDatabase(dao: CoalGuardDao) {
            // Mines
            dao.insertMine(Mine(id = 1, name = "Govindpur Colliery (BCCL)", subsidiary = "BCCL", state = "Jharkhand", latitude = 23.81, longitude = 86.41))
            dao.insertMine(Mine(id = 2, name = "Dhori Khas (CCL)", subsidiary = "CCL", state = "Jharkhand", latitude = 23.75, longitude = 85.98))
            dao.insertMine(Mine(id = 3, name = "Karo Special Seam (CCL)", subsidiary = "CCL", state = "Jharkhand", latitude = 23.78, longitude = 86.02))
            dao.insertMine(Mine(id = 4, name = "Tetaria Khar (ECL)", subsidiary = "ECL", state = "West Bengal", latitude = 23.82, longitude = 86.45))

            // Violations
            dao.insertViolation(Violation(id = 215, mineId = 4, category = "production", description = "Stay outside the mines - Unsecured perimeter", severity = "high", status = "open", regulationRef = "CMR-2017-R155", createdAt = "14 Sept 2026, 16:43"))
            dao.insertViolation(Violation(id = 214, mineId = 4, category = "production", description = "Elevated haul road berm erosion near Bench 2", severity = "high", status = "open", regulationRef = "CMR-2017-R155", createdAt = "14 Sept 2026, 16:43"))
            dao.insertViolation(Violation(id = 207, mineId = 1, category = "safety", description = "Exposed high-voltage cable runway in East Pit 3", severity = "high", status = "open", regulationRef = "CMR-2017-R83", createdAt = "14 Sept 2026, 12:56"))
            dao.insertViolation(Violation(id = 206, mineId = 2, category = "environment", description = "Dust PM10 concentration exceeding statutory limits", severity = "high", status = "open", regulationRef = "CMR-2017-R129", createdAt = "14 Sept 2026, 12:49"))
            dao.insertViolation(Violation(id = 205, mineId = 2, category = "labour", description = "Contractor dumper operators deployed without active VTC card", severity = "medium", status = "open", regulationRef = "MINES-VTC-1966", createdAt = "14 Sept 2026, 12:48"))

            // Compliance Items
            dao.insertComplianceItem(ComplianceItem("DIR-2025-1042", 1, "safety", "Installation of Real-Time CH4 Gas Monitoring Telemetry", "2025-10-01", "overdue", "critical", "Shri R. K. Mahapatra", "CMR 2017 Sec 104"))
            dao.insertComplianceItem(ComplianceItem("DIR-2025-1043", 2, "environment", "InSAR Satellite Subsidence Bench Survey Validation", "2025-10-15", "pending", "high", "Dr. Arindam Sen", "DGMS Circular No. 4/2022"))
            dao.insertComplianceItem(ComplianceItem("DIR-2025-1044", 3, "safety", "Hydraulic Roof Support & Strata Barricade Recertification", "2025-10-08", "in_progress", "critical", "Er. V. K. Sharma", "CMR 2017 Reg 124"))
            dao.insertComplianceItem(ComplianceItem("DIR-2025-1045", 4, "production", "Overhead Heavy Machinery Emergency Cut-off Inspection", "2025-09-28", "overdue", "high", "Inspector S. Roy", "DGMS S&T Circular 08"))

            // Contractors
            dao.insertContractor(Contractor(id = 1, name = "M/s RK Earthmovers Pvt Ltd", licenseNo = "LIC-RK-2025", licenseExpiry = "2026-12-31"))
            dao.insertContractor(Contractor(id = 2, name = "M/s Suvidha Drilling Co.", licenseNo = "LIC-SD-2025", licenseExpiry = "2026-11-30"))
            dao.insertContractor(Contractor(id = 3, name = "M/s Bharat Explosives", licenseNo = "LIC-BE-2025", licenseExpiry = "2025-10-15"))
            dao.insertContractor(Contractor(id = 4, name = "M/s Ganesh Haulage", licenseNo = "LIC-GH-2025", licenseExpiry = "2027-01-15"))

            // Inspections
            dao.insertInspection(Inspection(id = 101, mineId = 1, contractorId = 1, date = "2026-09-14", inspectorName = "Er. Rajesh Kumar", syncedAt = "2026-09-14", trackingId = "INSP-2026-9912"))
            dao.insertInspection(Inspection(id = 102, mineId = 2, contractorId = 2, date = "2026-09-14", inspectorName = "Dr. Arindam Sen", syncedAt = "2026-09-14", trackingId = "INSP-2026-9913"))

            // Statutory Registers
            dao.insertStatutoryRegister(StatutoryRegister(id = 1, mineId = 1, registerType = "FORM_IV_MINE_STATUTORY", shift = "Morning (1st)", seamOrPit = "Pit 3 Bench 2", inspectorName = "Smt. Ananya Sen", inspectorRole = "Mine Safety Inspector", statutoryRegulation = "CMR 2017 Reg 115", remarks = "All ventilation and methane sensors checked", hash = "a3f89e2c"))

            // Risk Scores
            dao.insertRiskScore(RiskScore(mineId = 1, score = 84.5, riskLevel = "MEDIUM", explanation = "Ventilation monitoring required"))

            // Audit Ledger Entries
            dao.insertAuditEntry(AuditLedgerEntry(1, tableName = "VIOLATION", recordId = 215, action = "INSERT", dataHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", prevHash = null, createdAt = "1695000000000"))
        }
    }
}
