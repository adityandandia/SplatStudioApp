package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ServerConfig::class, SplatJob::class], version = 1, exportSchema = false)
abstract class SplatDatabase : RoomDatabase() {
    abstract fun splatDao(): SplatDao

    companion object {
        @Volatile
        private var INSTANCE: SplatDatabase? = null

        fun getDatabase(context: Context): SplatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SplatDatabase::class.java,
                    "splat_studio_database"
                )
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
