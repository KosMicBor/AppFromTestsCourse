package com.geekbrains.tests.view.search


import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.geekbrains.tests.R
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class TestRecorderExampleTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testRecorderExampleTest() {
        onView(
            allOf(
                withId(R.id.searchEditText),
                isDisplayed()
            )
        ).perform(replaceText("ui automator"), closeSoftKeyboard())

        onView(
            allOf(
                withId(R.id.searchButton), withText("Search"),
                isDisplayed()
            )
        ).perform(click())

        onView(isRoot()).perform(delay())

        onView(
            allOf(
                withId(R.id.totalCountTextView)
            )
        ).check(matches(withText("Number of results: 226")))

        onView(
            allOf(
                withId(R.id.toDetailsActivityButton), withText("to details"),
                isDisplayed()
            )
        ).perform(click())

        onView(
            allOf(
                withId(R.id.totalCountTextView)
            )
        ).check(matches(withText("Number of results: 226")))

        val decrementButton = onView(
            allOf(
                withId(R.id.decrementButton), withText("-"),
                isDisplayed()
            )
        )
        decrementButton.check(matches(isDisplayed()))

        val incrementButton = onView(
            allOf(
                withId(R.id.incrementButton), withText("+"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        incrementButton.check(matches(isDisplayed()))

        incrementButton.perform(click())

        onView(
            allOf(
                withId(R.id.totalCountTextView)
            )
        ).check(matches(withText("Number of results: 227")))

        decrementButton.perform(click())
        decrementButton.perform(click())

        onView(
            allOf(
                withId(R.id.totalCountTextView)
            )
        ).check(matches(withText("Number of results: 225")))
    }

    private fun delay(): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "wait for $2 seconds"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(2000)
            }
        }
    }
}
