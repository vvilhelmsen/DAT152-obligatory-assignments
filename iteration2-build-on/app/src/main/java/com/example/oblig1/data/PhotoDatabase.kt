package com.example.oblig1.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.oblig1.PhotoEntry

@Database(entities = [PhotoEntry::class], version = 1, exportSchema = false)
abstract class PhotoDatabase : RoomDatabase() {

    abstract fun photoDao(): PhotoDao

    companion object {
        @Volatile
        private var INSTANCE: PhotoDatabase? = null

        fun getInstance(context: Context): PhotoDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    PhotoDatabase::class.java,
                    "photo_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
