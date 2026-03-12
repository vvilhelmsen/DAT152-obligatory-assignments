package com.example.oblig1

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests that the main menu buttons navigate to the correct activities.
 *
 * Test: Clicking "Galleri" from the main menu opens GalleryActivity.
 * Expected result: An intent targeting GalleryActivity is fired, and
 *                  the GalleryActivity becomes the foreground activity.
 * Implemented by: [clickGalleryButtonOpensGalleryActivity]
 */
@RunWith(AndroidJUnit4::class)
class MainMenuTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun initIntents() {
        Intents.init()
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }

    @Test
    fun clickGalleryButtonOpensGalleryActivity() {
        onView(withId(R.id.btnGallery))
            .check(matches(isDisplayed()))
            .perform(click())

        Intents.intended(hasComponent(GalleryActivity::class.java.name))
    }
}
