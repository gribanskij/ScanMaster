package com.gribanskij.scanmaster.model.database.entities
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Objects(
    @PrimaryKey (autoGenerate = true) val id: Int,
    val classid: Int,
    val ownerid: Int,
    val parentid: Int,
    val linkid: Int,
    val name: String,
    val delete_flag: Boolean,
    val group_flag: Boolean,
    val level_num: Long,
    val code: String,
    val full_name: String?,
    val description: String?,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val sys_date: String
)