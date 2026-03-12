package com.example.quiz_iteration2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.motion.widget.KeyTrigger

class GalleryActivity : ComponentActivity() {
    private var refreshTrigger = mutableStateOf(0)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                GalleryScreen(refreshTrigger.value)
            }
        }
    }

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun GalleryScreen(externalTrigger: Int) {
    val app = application as QuizApplication
    }
}
