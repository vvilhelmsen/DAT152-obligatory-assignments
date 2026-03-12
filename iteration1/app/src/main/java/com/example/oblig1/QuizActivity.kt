package com.example.oblig1

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.oblig1.viewmodel.QuizViewModel

class QuizActivity : ComponentActivity() {

    /** Exposed so instrumented tests can read current question state. */
    val viewModel: QuizViewModel by viewModels()

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
        val entries by viewModel.allEntries.observeAsState(emptyList())

        LaunchedEffect(entries) {
            if (entries.size >= 3 && viewModel.currentEntry == null) {
                viewModel.generateQuestion(entries)
            }
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
                Text(
                    text = stringResource(R.string.score_format, viewModel.correctCount, viewModel.totalCount),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.testTag("quiz_score")
                )

                if (entries.size < 3) {
                    Text(stringResource(R.string.need_more_photos))
                } else if (viewModel.currentEntry == null) {
                    Text(stringResource(R.string.need_more_photos))
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(Uri.parse(viewModel.currentEntry!!.imageUri)),
                            contentDescription = stringResource(R.string.quiz_image),
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    if (viewModel.showResult) {
                        val isCorrect = viewModel.selectedAnswer == viewModel.currentEntry!!.name
                        Text(
                            text = if (isCorrect) stringResource(R.string.correct_answer)
                                   else stringResource(R.string.wrong_answer, viewModel.currentEntry!!.name),
                            color = if (isCorrect) Color.Green else Color.Red,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(
                            onClick = { viewModel.generateQuestion(entries) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("next_question_button")
                        ) {
                            Text(stringResource(R.string.next_question))
                        }
                    } else {
                        viewModel.options.forEachIndexed { index, option ->
                            Button(
                                onClick = { viewModel.submitAnswer(option) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("answer_option_$index")
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

