package com.example.coalguard.data.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.coalguard.data.model.Contractor
import com.example.coalguard.data.model.Inspection

data class InspectionWithContractor(
    @Embedded val inspection: Inspection,
    @Relation(
        parentColumn = "contractor_id",
        entityColumn = "id"
    )
    val contractor: Contractor? = null
)
