package com.example.quiz_iteration2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class GalleryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizGreeter()
        }
    }
}