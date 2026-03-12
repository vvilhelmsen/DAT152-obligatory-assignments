package com.example.oblig1

import androidx.room.Entity
import androidx.room.PrimaryKey

/** A name/image pair stored in the Room database. Images are always referenced by URI. */
@Entity(tableName = "photo_entries")
data class PhotoEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val imageUri: String
)
