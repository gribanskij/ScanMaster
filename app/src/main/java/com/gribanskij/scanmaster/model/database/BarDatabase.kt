package com.gribanskij.scanmaster.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gribanskij.scanmaster.model.database.entities.BarObject

@Database(entities = [BarObject::class], version = 1)
abstract class BarDatabase : RoomDatabase() {
    abstract fun barDao(): BarDao

    companion object {
        private const val DB_NAME = "barDatabase"
        private var instance: BarDatabase? = null
        fun getInstance(context: Context):BarDatabase? {
            if (instance == null) {
                instance = Room.databaseBuilder(context.applicationContext, BarDatabase::class.java, DB_NAME).build()
            }
            return instance
        }
    }
}