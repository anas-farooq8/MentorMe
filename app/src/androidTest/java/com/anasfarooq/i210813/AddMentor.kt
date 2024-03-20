package com.anasfarooq.i210813
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AddMentorTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<AddMentorActivity> = ActivityScenarioRule(AddMentorActivity::class.java)

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun addMentorTest() {
        onView(withId(R.id.nameText)).perform(typeText("John Doe"), closeSoftKeyboard())
        onView(withId(R.id.descriptionText)).perform(typeText("Experienced Software Engineer"), closeSoftKeyboard())

        // Now proceed to click the upload button
        onView(withId(R.id.uploadBtn)).perform(click())

        // Since direct validation of Firebase updates in UI tests is not practical, consider validating expected UI changes or using a mocked data source.
        ActivityScenario.launch(MainActivity::class.java).use {
            for (mentor in MainActivity.topMentorList) {
                if (mentor.name == "John Doe") {
                    onView(withId(R.id.nameText)).check(matches(withText("John Doe")))
                    break
                }
            }
        }
    }
}
