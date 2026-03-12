package com.example.oblig1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.oblig1.data.PhotoDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests that the gallery entry count is correct after adding and deleting entries.
 *
 * Test A — Add entry:
 *   1. Start GalleryActivity with 3 pre-seeded entries.
 *   2. Verify 3 name labels are visible in the grid.
 *   3. Stub the image-picker intent (ACTION_GET_CONTENT) to return the built-in cat drawable URI.
 *   4. Click the "Legg til" button → AddEntryActivity opens.
 *   5. Type "Elefant" into the name field.
 *   6. Click "Select image" → stub fires, cat URI is returned automatically.
 *   7. Click "Save" → AddEntryActivity finishes and control returns to GalleryActivity.
 *   8. Verify 4 name labels are now visible in the grid.
 * Expected result: Gallery shows 4 entries after a successful add.
 * Implemented by: [addEntryIncreasesCount]
 *
 * Test B — Delete entry:
 *   1. Start GalleryActivity with 3 pre-seeded entries.
 *   2. Click the grid item labelled "Katt".
 *   3. Verify the grid now shows only 2 entries.
 * Expected result: Gallery shows 2 entries after deleting one.
 * Implemented by: [deleteEntryDecreasesCount]
 */
@RunWith(AndroidJUnit4::class)
class GalleryActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<GalleryActivity>()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        val db = PhotoDatabase.getInstance(context)
        runBlocking {
            db.photoDao().deleteAll()
            val pkg = context.packageName
            db.photoDao().insert(PhotoEntry(name = "Katt", imageUri = "android.resource://$pkg/${R.drawable.cat}"))
            db.photoDao().insert(PhotoEntry(name = "Hund", imageUri = "android.resource://$pkg/${R.drawable.dog}"))
            db.photoDao().insert(PhotoEntry(name = "Fugl", imageUri = "android.resource://$pkg/${R.drawable.bird}"))
        }
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun addEntryIncreasesCount() {
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Katt").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onAllNodesWithTag("gallery_grid")[0]
            .onChildren()
            .assertCountEquals(3)

        val catUri = Uri.parse("android.resource://${context.packageName}/${R.drawable.cat}")
        val resultData = Intent().apply { data = catUri }
        Intents.intending(hasAction(Intent.ACTION_GET_CONTENT))
            .respondWith(android.app.Instrumentation.ActivityResult(Activity.RESULT_OK, resultData))

        composeTestRule.onNodeWithTag("add_entry_button").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("name_field").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("name_field").performTextInput("Elefant")
        composeTestRule.onNodeWithTag("select_image_button").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            try {
                composeTestRule.onNodeWithTag("save_button").assertIsEnabled()
                true
            } catch (e: AssertionError) { false }
        }
        composeTestRule.onNodeWithTag("save_button").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Elefant").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onAllNodesWithTag("gallery_grid")[0]
            .onChildren()
            .assertCountEquals(4)
    }

    @Test
    fun deleteEntryDecreasesCount() {
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Katt").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("gallery_item_Katt").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Katt").fetchSemanticsNodes().isEmpty()
        }

        composeTestRule.onAllNodesWithTag("gallery_grid")[0]
            .onChildren()
            .assertCountEquals(2)
    }
}
