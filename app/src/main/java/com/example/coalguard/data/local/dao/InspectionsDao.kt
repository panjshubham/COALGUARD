package com.example.coalguard.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.coalguard.data.model.Inspection

@Dao
interface InspectionsDao {
    @Query("SELECT * FROM inspections")
    fun getAllInspections(): LiveData<List<Inspection>>

    @Query("SELECT * FROM inspections WHERE id = :id")
    fun getInspectionById(id: String): LiveData<Inspection?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(inspection: Inspection)

    @Update
    suspend fun update(inspection: Inspection)

    @Delete
    suspend fun delete(inspection: Inspection)
}
