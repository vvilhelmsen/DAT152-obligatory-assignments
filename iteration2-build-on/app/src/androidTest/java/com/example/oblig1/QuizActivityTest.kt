package com.example.oblig1

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.oblig1.data.PhotoDatabase
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests that the quiz score is updated correctly after answering questions.
 *
 * Test: Launch QuizActivity with a pre-populated database (3 entries).
 *   1. Seed the database with 3 entries and wait for the question UI to appear.
 *   2. Read the correct answer name from the ViewModel.
 *   3. Click the button that matches the correct answer.
 *   4. Verify the score display shows "1 / 1".
 *   5. Click "Next question" to advance.
 *   6. Read the new correct answer from the ViewModel.
 *   7. Click a button that does NOT match the correct answer (a wrong answer).
 *   8. Verify the score display shows "1 / 2".
 *
 * Expected result: correctCount increments only on correct answers; totalCount
 *                  increments on every answer.
 * Implemented by: [scoreUpdatesCorrectlyAfterRightAndWrongAnswer]
 */
@RunWith(AndroidJUnit4::class)
class QuizActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<QuizActivity>()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun scoreUpdatesCorrectlyAfterRightAndWrongAnswer() {
        // Seed the database now that the activity is running; LiveData will push the
        // update to the ViewModel and LaunchedEffect will call generateQuestion.
        val db = PhotoDatabase.getInstance(context)
        runBlocking {
            db.photoDao().deleteAll()
            val pkg = context.packageName
            db.photoDao().insert(PhotoEntry(name = "Katt", imageUri = "android.resource://$pkg/${R.drawable.cat}"))
            db.photoDao().insert(PhotoEntry(name = "Hund", imageUri = "android.resource://$pkg/${R.drawable.dog}"))
            db.photoDao().insert(PhotoEntry(name = "Fugl", imageUri = "android.resource://$pkg/${R.drawable.bird}"))
        }

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("answer_option_0").fetchSemanticsNodes().isNotEmpty()
        }

        var correctName = ""
        composeTestRule.activityRule.scenario.onActivity { activity ->
            correctName = activity.viewModel.currentEntry?.name ?: ""
        }

        composeTestRule.onNodeWithText(correctName).performClick()
        composeTestRule.onNodeWithTag("quiz_score").assertTextContains("1 / 1", substring = true)
        composeTestRule.onNodeWithTag("next_question_button").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("answer_option_0").fetchSemanticsNodes().isNotEmpty()
        }

        var wrongOption = ""
        composeTestRule.activityRule.scenario.onActivity { activity ->
            val vm = activity.viewModel
            wrongOption = vm.options.first { it != vm.currentEntry?.name }
        }

        composeTestRule.onNodeWithText(wrongOption).performClick()
        composeTestRule.onNodeWithTag("quiz_score").assertTextContains("1 / 2", substring = true)
    }
}
