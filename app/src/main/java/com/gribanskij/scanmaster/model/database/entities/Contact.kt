package com.gribanskij.scanmaster.model.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact (
    @PrimaryKey val id: Int,
    val title:String,
    val organization:String?,
    val adressid:Int?,
    val emailid:Int?,
    val nameid:Int?,
    val phoneid:Int?,
    val urlid:Int?
    )