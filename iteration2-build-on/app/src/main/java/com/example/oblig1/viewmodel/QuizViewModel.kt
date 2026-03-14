package com.example.oblig1.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.oblig1.PhotoEntry
import com.example.oblig1.data.PhotoDatabase

/**
 * Holds all quiz state so it survives screen rotation.
 * correctCount and totalCount are preserved across configuration changes.
 */
class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = PhotoDatabase.getInstance(application).photoDao()

    /** All entries — observed by the Activity to trigger question generation. */
    val allEntries = dao.getAll()

    var correctCount by mutableStateOf(0)
        private set
    var totalCount by mutableStateOf(0)
        private set
    var currentEntry by mutableStateOf<PhotoEntry?>(null)
        private set
    var options by mutableStateOf<List<String>>(emptyList())
        private set
    var showResult by mutableStateOf(false)
        private set
    var selectedAnswer by mutableStateOf<String?>(null)
        private set

    /** Picks a random entry and two wrong answers from [entries]. */
    fun generateQuestion(entries: List<PhotoEntry>) {
        if (entries.size < 3) {
            currentEntry = null
            return
        }
        currentEntry = entries.random()
        val wrongNames = entries
            .filter { it.name != currentEntry!!.name }
            .map { it.name }
            .shuffled()
            .take(2)
        options = (listOf(currentEntry!!.name) + wrongNames).shuffled()
        selectedAnswer = null
        showResult = false
    }

    fun submitAnswer(answer: String) {
        selectedAnswer = answer
        showResult = true
        totalCount++
        if (answer == currentEntry?.name) correctCount++
    }
}
