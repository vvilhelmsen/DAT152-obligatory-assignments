package com.example.oblig1

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.oblig1.data.PhotoDatabase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests that the quiz score is updated correctly after answering questions.
 *
 * Test: Launch QuizActivity with a pre-populated database (3 entries).
 *   1. Read the correct answer name from the ViewModel.
 *   2. Click the button that matches the correct answer.
 *   3. Verify the score display shows "1 / 1".
 *   4. Click "Next question" to advance.
 *   5. Read the new correct answer from the ViewModel.
 *   6. Click a button that does NOT match the correct answer (a wrong answer).
 *   7. Verify the score display shows "1 / 2".
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

    @Before
    fun seedDatabase() {
        val db = PhotoDatabase.getInstance(context)
        runBlocking {
            db.photoDao().deleteAll()
            val pkg = context.packageName
            db.photoDao().insert(PhotoEntry(name = "Katt", imageUri = "android.resource://$pkg/${R.drawable.cat}"))
            db.photoDao().insert(PhotoEntry(name = "Hund", imageUri = "android.resource://$pkg/${R.drawable.dog}"))
            db.photoDao().insert(PhotoEntry(name = "Fugl", imageUri = "android.resource://$pkg/${R.drawable.bird}"))
        }
    }

    @Test
    fun scoreUpdatesCorrectlyAfterRightAndWrongAnswer() {
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("answer_option_0").fetchSemanticsNodes().isNotEmpty()
        }

        var correctName = ""
        composeTestRule.activityRule.scenario.onActivity { activity ->
            correctName = activity.viewModel.currentEntry?.name ?: ""
        }

        composeTestRule.onNodeWithText(correctName).performClick()
        composeTestRule.onNodeWithTag("quiz_score").assertTextContains("1 / 1")
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
        composeTestRule.onNodeWithTag("quiz_score").assertTextContains("1 / 2")
    }
}
