package com.example.coalguard.data.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.coalguard.data.model.Mine
import com.example.coalguard.data.model.Violation

data class MineWithViolations(
    @Embedded val mine: Mine,
    @Relation(
        parentColumn = "id",
        entityColumn = "mine_id"
    )
    val violations: List<Violation> = emptyList()
)
