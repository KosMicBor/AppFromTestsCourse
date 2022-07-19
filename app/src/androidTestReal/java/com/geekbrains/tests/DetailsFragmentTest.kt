package com.geekbrains.tests

import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.geekbrains.tests.view.details.DetailsFragment
import com.geekbrains.tests.view.search.MainActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DetailsFragmentTest {

    companion object {
        private const val FRAGMENT_TOTAL_COUNT_EXTRA = "FRAGMENT_TOTAL_COUNT_EXTRA"
    }

    private lateinit var fragmentScenario: FragmentScenario<DetailsFragment>

    @Before
    fun setUp() {
        fragmentScenario = launchFragmentInContainer()
    }

    @Test
    fun fragmentTest_Bundle() {
        val fragmentArgs = Bundle().apply {
            putInt(FRAGMENT_TOTAL_COUNT_EXTRA, 10)
        }

        val scenario = launchFragmentInContainer<DetailsFragment>(fragmentArgs)


        scenario.moveToState(Lifecycle.State.RESUMED)

        val assertion = matches(withText("Number of results: 10"))

        onView(withId(R.id.totalCountTextView)).check(assertion)
    }

    @Test
    fun fragmentTest_SetCount() {
        fragmentScenario.onFragment {
            it.setCount(42)
        }

        onView(withId(R.id.totalCountTextView))
            .check(matches(withText("Number of results: 42")))
    }

    @Test
    fun fragmentTest_IncrementButton() {
        onView(withId(R.id.incrementButton)).perform(click())
        onView(withId(R.id.totalCountTextView)).check(matches(withText("Number of results: 1")))
    }

    @Test
    fun fragmentTest_DecrementButton() {
        onView(withId(R.id.decrementButton)).perform(click())
        onView(withId(R.id.totalCountTextView)).check(matches(withText("Number of results: -1")))
    }
}