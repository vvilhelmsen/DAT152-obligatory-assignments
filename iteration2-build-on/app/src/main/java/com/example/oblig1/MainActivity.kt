package com.example.oblig1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

// Main menu w/ xml
class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
				// Find gallery button and set up click listener
        val btnGallery = findViewById<Button>(R.id.btnGallery)
        btnGallery.setOnClickListener {
            // start gallery activity
            val intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
        }
        
				// Find quiz btn and set up click listener
        val btnQuiz = findViewById<Button>(R.id.btnQuiz)
        btnQuiz.setOnClickListener {
						// start activity
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }
    }
}
