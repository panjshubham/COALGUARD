package com.example.coalguard.domain

import android.content.Context
import com.example.coalguard.data.local.CoalGuardDatabase
import com.example.coalguard.data.model.ComplianceItem
import com.example.coalguard.data.model.SystemAlertEntity
import com.example.coalguard.data.model.Violation
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

data class SystemAlert(
    val id: String,
    val title: String,
    val description: String,
    val severity: String
)

class AlertsEngine(context: Context) {
    private val dao = CoalGuardDatabase.getDatabase(context).coalGuardDao()

    suspend fun evaluateAndPersistAlerts(
        complianceItems: List<ComplianceItem>,
        violations: List<Violation>
    ): List<SystemAlertEntity> {
        val alerts = mutableListOf<SystemAlertEntity>()
        val currentTime = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // 1. Compliance Deadline: status = overdue -> high severity
        for (item in complianceItems) {
            if (item.status.equals("overdue", ignoreCase = true)) {
                val alert = SystemAlertEntity(
                    id = "alert-comp-${item.id}",
                    title = "Overdue Compliance Item",
                    description = "Compliance item '${item.title}' is overdue.",
                    severity = "high",
                    createdAt = currentTime.toString()
                )
                alerts.add(alert)
                dao.insertAlert(alert)
            }
        }

        // 2. Escalation: open violation older than 7 days -> critical severity
        for (violation in violations) {
            if (violation.status.equals("open", ignoreCase = true)) {
                try {
                    val createdDate = sdf.parse(violation.createdAt)?.time ?: 0L
                    val diffDays = TimeUnit.MILLISECONDS.toDays(currentTime - createdDate)
                    if (diffDays > 7) {
                        val alert = SystemAlertEntity(
                            id = "alert-viol-${violation.id}",
                            title = "Critical Unresolved Violation",
                            description = "Violation '${violation.title}' has been open for $diffDays days.",
                            severity = "critical",
                            createdAt = currentTime.toString()
                        )
                        alerts.add(alert)
                        dao.insertAlert(alert)
                    }
                } catch (e: Exception) {
                    // Ignore date parse issues
                }
            }
        }

        return alerts
    }
}
