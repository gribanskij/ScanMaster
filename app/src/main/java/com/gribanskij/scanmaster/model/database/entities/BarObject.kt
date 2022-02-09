package com.gribanskij.scanmaster.model.database.entities
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BarObject(
    @PrimaryKey (autoGenerate = true) val id: Int,
    val barType: Int,
    val jsonValue: String?,
    val rawValue: String,
    val displayValue:String,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val sysDate: String
)