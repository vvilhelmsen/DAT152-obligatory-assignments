package com.example.oblig1

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageViews = listOf(
            R.id.grid1, R.id.grid2, R.id.grid3,
            R.id.grid4, R.id.grid5, R.id.grid6,
            R.id.grid7, R.id.grid8, R.id.grid9
        )

        imageViews.forEach { id ->
            findViewById<ImageView>(id).setOnClickListener {
                val intent = Intent(this, ImagePickedActivity::class.java)
                startActivity(intent)
            }
        }
    }
}