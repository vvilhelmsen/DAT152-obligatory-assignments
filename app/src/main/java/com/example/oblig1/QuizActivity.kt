package com.example.oblig1

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

class QuizActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                QuizScreen()
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun QuizScreen() {
        val app = application as QuizApplication
        
        var correctCount by remember { mutableStateOf(0) }
        var totalCount by remember { mutableStateOf(0) }
        
        var currentEntry by remember { mutableStateOf<PhotoEntry?>(null) }
        var options by remember { mutableStateOf<List<String>>(emptyList()) }
        var selectedAnswer by remember { mutableStateOf<String?>(null) }
        var showResult by remember { mutableStateOf(false) }
        
        fun generateQuestion() {
            if (app.photoEntries.size < 3) {
                currentEntry = null
                return
            }
            
            // pick random image
            currentEntry = app.photoEntries.random()
            val correctName = currentEntry!!.name
            
						// Find two wrong names from other entries
            val wrongNames = app.photoEntries
                .filter { it.name != correctName }
                .map { it.name }
                .shuffled()
                .take(2)
            
            // Bland sammen riktig og gale svar
            options = (listOf(correctName) + wrongNames).shuffled()
            
            selectedAnswer = null
            showResult = false
        }
        
				// Generate first question when screen loads
        LaunchedEffect(Unit) {
            generateQuestion()
        }
        
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(stringResource(R.string.quiz_title)) })
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // show points
                Text(
                    text = stringResource(R.string.score_format, correctCount, totalCount),
                    style = MaterialTheme.typography.headlineSmall
                )
                
                if (app.photoEntries.size < 3) {
                    Text(stringResource(R.string.need_more_photos))
                } else if (currentEntry == null) {
                    Text("Ingen spørsmål generert. Dette burde ikke skje!")
                } else {
                    // shopw image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentEntry!!.imageResId != 0) {
                            Image(
                                painter = painterResource(id = currentEntry!!.imageResId),
                                contentDescription = stringResource(R.string.quiz_image),
                                modifier = Modifier.fillMaxSize()
                            )
                        } else if (currentEntry!!.imageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(Uri.parse(currentEntry!!.imageUri)),
                                contentDescription = stringResource(R.string.quiz_image),
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    
                    // show result if answered
                    if (showResult) {
                        val isCorrect = selectedAnswer == currentEntry!!.name
                        Text(
                            text = if (isCorrect) {
                                stringResource(R.string.correct_answer)
                            } else {
                                stringResource(R.string.wrong_answer, currentEntry!!.name)
                            },
                            color = if (isCorrect) Color.Green else Color.Red,
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        // next button
                        Button(
                            onClick = {
                                generateQuestion()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.next_question))
                        }
                    } else {
                        // answer options
                        options.forEach { option ->
                            Button(
                                onClick = {
                                    selectedAnswer = option
                                    showResult = true
                                    totalCount++
                                    if (option == currentEntry!!.name) {
                                        correctCount++
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(option)
                            }
                        }
                    }
                }
            }
        }
    }
}
