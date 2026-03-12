package com.example.oblig1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.oblig1.PhotoEntry
import com.example.oblig1.data.PhotoDatabase
import kotlinx.coroutines.launch

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = PhotoDatabase.getInstance(application).photoDao()

    /** All entries from Room, sorted A-Z. UI observes this and reacts to changes. */
    val entries = dao.getAll()

    fun insert(entry: PhotoEntry) {
        viewModelScope.launch { dao.insert(entry) }
    }

    fun delete(entry: PhotoEntry) {
        viewModelScope.launch { dao.delete(entry) }
    }
}
