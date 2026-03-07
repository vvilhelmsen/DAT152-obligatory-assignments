package com.example.quiz_iteration2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

class GalleryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoodbyeWorld()
        }
    }
}

@Composable
fun GoodbyeWorld() {
    Text(
        text = "Goodbye, World!",
        style = MaterialTheme.typography.headlineMedium,
    )
}