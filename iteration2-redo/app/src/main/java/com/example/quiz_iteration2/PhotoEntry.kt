package com.example.quiz_iteration2
// Data class to hold a photo and its associated name
// Can hold either a resource image (from drawable) or a URI (from phone gallery)
data class PhotoEntry(
    val name: String,
    val imageResId: Int = 0,
    val imageUri: String? = null
)
