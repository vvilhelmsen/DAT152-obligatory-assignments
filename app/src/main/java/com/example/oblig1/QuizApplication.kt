package com.example.oblig1

import android.app.Application

// Application class that lives as long as the app is running
class QuizApplication : Application() {
    
    val photoEntries = mutableListOf<PhotoEntry>()
    
    override fun onCreate() {
        super.onCreate()
        
        photoEntries.add(PhotoEntry("Katt", R.drawable.cat))
        photoEntries.add(PhotoEntry("Hund", R.drawable.dog))
        photoEntries.add(PhotoEntry("Fugl", R.drawable.bird))
    }
}
