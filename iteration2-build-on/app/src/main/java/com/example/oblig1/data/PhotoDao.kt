package com.example.oblig1.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.oblig1.PhotoEntry

@Dao
interface PhotoDao {

    /** Returns all entries as LiveData so the UI updates automatically. */
    @Query("SELECT * FROM photo_entries ORDER BY name ASC")
    fun getAll(): LiveData<List<PhotoEntry>>

    /** Synchronous query used for seeding the DB and for the ContentProvider. */
    @Query("SELECT * FROM photo_entries ORDER BY name ASC")
    fun getAllSync(): List<PhotoEntry>

    @Insert
    suspend fun insert(entry: PhotoEntry)

    @Delete
    suspend fun delete(entry: PhotoEntry)

    /** Removes all rows — used to reset state in tests. */
    @Query("DELETE FROM photo_entries")
    suspend fun deleteAll()
}
