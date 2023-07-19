package com.droidnova.studytogether.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ProgressEntity::class], version = 1)
abstract class ProgressDatabase:RoomDatabase() {

    abstract fun progressDao():ProgressDao


    companion object {
        @Volatile
        private var INSTANCE: ProgressDatabase? = null

        fun getDatabase(context: Context): ProgressDatabase {

            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ProgressDatabase::class.java,
                        "progress-table"
                    )
                        .fallbackToDestructiveMigrationFrom().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}