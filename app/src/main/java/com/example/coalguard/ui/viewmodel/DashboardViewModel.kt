package com.example.coalguard.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coalguard.data.local.CoalGuardDatabase
import com.example.coalguard.data.model.AuditLedgerEntry
import com.example.coalguard.data.model.ComplianceItem
import com.example.coalguard.data.model.Contractor
import com.example.coalguard.data.model.Inspection
import com.example.coalguard.data.model.Mine
import com.example.coalguard.data.model.Violation
import com.example.coalguard.data.repository.ComplianceRepository
import com.example.coalguard.data.repository.ContractorsRepository
import com.example.coalguard.data.repository.InspectionsRepository
import com.example.coalguard.data.repository.MinesRepository
import com.example.coalguard.data.repository.StatutoryRepository
import com.example.coalguard.data.repository.ViolationsRepository
import com.example.coalguard.domain.DynamicRiskEngine
import com.example.coalguard.domain.DynamicRiskResult
import com.example.coalguard.domain.RiskFactors
import com.example.coalguard.domain.SlaEscalationEngine
import com.example.coalguard.domain.SlaEscalationResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DashboardUiState(
    val violations: List<Violation> = emptyList(),
    val mines: List<Mine> = emptyList(),
    val inspections: List<Inspection> = emptyList(),
    val complianceItems: List<ComplianceItem> = emptyList(),
    val auditEntries: List<AuditLedgerEntry> = emptyList(),
    val contractors: List<Contractor> = emptyList(),
    val dynamicRisk: DynamicRiskResult? = null,
    val slaEscalations: List<SlaEscalationResult> = emptyList(),
    val pendingSyncCount: Int = 0,
    val isLoading: Boolean = false
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val violationsRepository = ViolationsRepository(application)
    private val minesRepository = MinesRepository(application)
    private val inspectionsRepository = InspectionsRepository(application)
    private val complianceRepository = ComplianceRepository(application)
    private val contractorsRepository = ContractorsRepository(application)
    private val statutoryRepository = StatutoryRepository(application)
    private val dao = CoalGuardDatabase.getDatabase(application).coalGuardDao()
    
    private val riskEngine = DynamicRiskEngine()
    private val slaEngine = SlaEscalationEngine()

    val uiState: StateFlow<DashboardUiState> = combine(
        violationsRepository.getViolations(),
        minesRepository.getMines(),
        inspectionsRepository.getInspections(),
        complianceRepository.getComplianceItems(),
        statutoryRepository.getAuditEntries(),
        contractorsRepository.getContractors()
    ) { flows: Array<Any> ->
        @Suppress("UNCHECKED_CAST")
        val violations = flows[0] as List<Violation>
        @Suppress("UNCHECKED_CAST")
        val mines = flows[1] as List<Mine>
        @Suppress("UNCHECKED_CAST")
        val inspections = flows[2] as List<Inspection>
        @Suppress("UNCHECKED_CAST")
        val compliance = flows[3] as List<ComplianceItem>
        @Suppress("UNCHECKED_CAST")
        val auditEntries = flows[4] as List<AuditLedgerEntry>
        @Suppress("UNCHECKED_CAST")
        val contractors = flows[5] as List<Contractor>

        val syncItems = dao.getAllSyncQueueItems()

        val riskResult = riskEngine.calculateRisk(
            RiskFactors(
                methaneGasPct = 0.85f,
                ventilationAirSpeedMps = 1.2f,
                roofInstabilityReported = violations.any { it.severity.lowercase() == "critical" },
                openViolations = violations,
                complianceItems = compliance
            )
        )

        val escalations = violations.map { slaEngine.evaluateSlaAndEscalation(it) }

        DashboardUiState(
            violations = violations,
            mines = mines,
            inspections = inspections,
            complianceItems = compliance,
            auditEntries = auditEntries,
            contractors = contractors,
            dynamicRisk = riskResult,
            slaEscalations = escalations,
            pendingSyncCount = syncItems.size,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(isLoading = true)
    )
}
