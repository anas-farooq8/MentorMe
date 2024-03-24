package com.anasfarooq.i210813

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class NewTestCases {
    // Create an IdlingResource for waiting until the authentication is complete
    private val countingIdlingResource = CountingIdlingResource("Authentication")

    private fun withItemCount(expectedCount: Int): ViewAssertion {
        return ViewAssertion { view, noViewFoundException ->
            if (view !is RecyclerView) {
                throw (noViewFoundException
                    ?: IllegalStateException("The view is not a RecyclerView"))
            }
            val adapter = view.adapter
            assertThat("RecyclerView item count", adapter?.itemCount, `is`(expectedCount))
        }
    }

    @Before
    fun setUp() {
        ActivityScenario.launch(MainActivity::class.java)
        Thread.sleep(1500)

        countingIdlingResource.increment();
        // Input email and password, then click "Login" button (reuse previous code here)
        onView(withId(R.id.emailText))
            .perform(typeText("anasfarooq8888@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.passText))
            .perform(typeText("amongus123"), closeSoftKeyboard())

        onView(allOf(withId(R.id.loginBtn), withText("Login"), isDisplayed())).perform(click())

        countingIdlingResource.decrement()
    }

    @Test
    fun viewVisibilityTest() {
        Thread.sleep(8000)
        // Verify that the specific View in HomeActivity is displayed
        onView(withId(R.id.toCheck)).check(matches(isDisplayed()))
    }

    @Test
    fun recyclerViewItemCountTest() {
        Thread.sleep(6000)
        // Assert that the RecyclerView has an item count of 1
        onView(withId(R.id.topMentors)).check(withItemCount(1))
    }

    @Test
    fun myProfileNameTest() {
        Thread.sleep(6000)
        ActivityScenario.launch(MyProfileActivity::class.java)
        Thread.sleep(2000)
        onView(withId(R.id.name)).check(matches(withText("Muhammad Anas Farooq")))
    }

    @After
    fun tearDown() {
        MainActivity.auth.signOut()
    }

}
