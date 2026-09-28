package com.example.coalguard.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.coalguard.data.model.Violation

@Dao
interface ViolationsDao {
    @Query("SELECT * FROM violations")
    fun getAllViolations(): LiveData<List<Violation>>

    @Query("SELECT * FROM violations WHERE id = :id")
    fun getViolationById(id: String): LiveData<Violation?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(violation: Violation)

    @Update
    suspend fun update(violation: Violation)

    @Delete
    suspend fun delete(violation: Violation)
}
