package com.example.oblig1

import android.app.Application
import com.example.oblig1.data.PhotoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuizApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        seedDatabaseIfEmpty()
    }

    /** Inserts the three built-in entries on the very first launch (when the DB is empty). */
    private fun seedDatabaseIfEmpty() {
        val db = PhotoDatabase.getInstance(this)
        CoroutineScope(Dispatchers.IO).launch {
            if (db.photoDao().getAllSync().isEmpty()) {
                val pkg = packageName
                db.photoDao().insert(PhotoEntry(name = "Katt", imageUri = "android.resource://$pkg/${R.drawable.cat}"))
                db.photoDao().insert(PhotoEntry(name = "Hund", imageUri = "android.resource://$pkg/${R.drawable.dog}"))
                db.photoDao().insert(PhotoEntry(name = "Fugl", imageUri = "android.resource://$pkg/${R.drawable.bird}"))
            }
        }
    }
}
