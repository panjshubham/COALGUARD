package com.example.coalguard.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.coalguard.data.local.entity.AlertEntity

@Dao
interface AlertsDao {
    @Query("SELECT * FROM alerts")
    fun getAllAlerts(): LiveData<List<AlertEntity>>

    @Query("SELECT * FROM alerts WHERE id = :id")
    fun getAlertById(id: String): LiveData<AlertEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: AlertEntity)

    @Update
    suspend fun update(alert: AlertEntity)

    @Delete
    suspend fun delete(alert: AlertEntity)
}
