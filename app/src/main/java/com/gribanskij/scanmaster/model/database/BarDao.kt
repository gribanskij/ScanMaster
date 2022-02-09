package com.gribanskij.scanmaster.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.gribanskij.scanmaster.model.database.entities.BarObject
import kotlinx.coroutines.flow.Flow

@Dao
interface BarDao {
    @Query("SELECT * FROM BarObject ORDER BY sysDate DESC")
    fun getAllBars(): Flow <List<BarObject>>

    @Insert
    fun insertAll(bars: List<BarObject>)

    @Delete
    fun delete(bar: BarObject)
}