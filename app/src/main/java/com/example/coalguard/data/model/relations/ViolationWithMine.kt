package com.example.coalguard.data.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.coalguard.data.model.Mine
import com.example.coalguard.data.model.Violation

data class ViolationWithMine(
    @Embedded val violation: Violation,
    @Relation(
        parentColumn = "mine_id",
        entityColumn = "id"
    )
    val mine: Mine? = null
)
