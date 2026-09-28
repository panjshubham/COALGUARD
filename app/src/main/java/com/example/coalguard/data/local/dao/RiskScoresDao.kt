package com.example.coalguard.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.coalguard.data.model.RiskScore

@Dao
interface RiskScoresDao {
    @Query("SELECT * FROM risk_scores")
    fun getAllRiskScores(): LiveData<List<RiskScore>>

    @Query("SELECT * FROM risk_scores WHERE mine_id = :mineId")
    fun getRiskScoreByMineId(mineId: Int): LiveData<RiskScore?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(riskScore: RiskScore)

    @Update
    suspend fun update(riskScore: RiskScore)

    @Delete
    suspend fun delete(riskScore: RiskScore)
}
