package com.geekbrains.tests

import android.content.ComponentName
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.geekbrains.tests.utils.EspressoTestCustomAction
import com.geekbrains.tests.view.details.DetailsActivity
import com.geekbrains.tests.view.search.MainActivity
import com.geekbrains.tests.view.search.SearchResultAdapter
import junit.framework.TestCase
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityEspressoTest {

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        Intents.init()
    }

    @Test
    fun activitySearch_IsWorking() {
        onView(withId(R.id.searchEditText)).perform(click())
        onView(withId(R.id.searchEditText)).perform(replaceText("algol"), closeSoftKeyboard())
        onView(withId(R.id.searchEditText)).perform(pressImeActionButton())

        onView(isRoot()).perform(delay())
        onView(withId(R.id.totalCountTextView)).check(matches(withText("Number of results: 3138")))
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

    @Test
    fun activityTotalCountTextView_NotNull() {
        scenario.onActivity {
            val totalCountTextView = it.findViewById<TextView>(R.id.totalCountTextView)
            TestCase.assertNotNull(totalCountTextView)
        }
    }

    @Test
    fun activityTotalCountTextView_IsCompletelyDisplayed() {
        onView(withId(R.id.searchEditText)).perform(click())
        onView(withId(R.id.searchEditText)).perform(replaceText("algol"), closeSoftKeyboard())
        onView(withId(R.id.searchEditText)).perform(pressImeActionButton())
        onView(isRoot()).perform(delay())

        onView(withId(R.id.totalCountTextView)).check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun activityToDetailsButton_AreEffectiveVisible() {
        onView(withId(R.id.toDetailsActivityButton)).check(
            matches(
                withEffectiveVisibility(
                    Visibility.VISIBLE
                )
            )
        )
    }

    @Test
    fun activityToDetailsButton_NotNull() {
        scenario.onActivity {
            val toDetailsButton = it.findViewById<TextView>(R.id.toDetailsActivityButton)
            TestCase.assertNotNull(toDetailsButton)
        }
    }

    @Test
    fun activityToDetailsButton_IsWorking() {
        onView(withId(R.id.toDetailsActivityButton)).perform(click())

        val context: Context = ApplicationProvider.getApplicationContext()

        intended(hasComponent(ComponentName(context, DetailsActivity::class.java)))
    }

    @Test
    fun recyclerViewTest_ScrollToItem() {
        onView(withId(R.id.searchEditText)).perform(click())
        onView(withId(R.id.searchEditText)).perform(replaceText("uiautomator"), closeSoftKeyboard())
        onView(withId(R.id.searchButton)).perform(click())

        onView(isRoot()).perform(delay())

        onView(withId(R.id.recyclerView))
            .perform(
                RecyclerViewActions.scrollToPosition<SearchResultAdapter.SearchResultViewHolder>(
                    1
                )
            ).check(matches(hasDescendant(withText("xiaocong/uiautomator"))))

        onView(withId(R.id.recyclerView))
            .perform(
                RecyclerViewActions.scrollToPosition<SearchResultAdapter.SearchResultViewHolder>(
                    20
                )
            )
        onView(isRoot()).perform(delay())

        onView(withId(R.id.recyclerView))
            .perform(
                RecyclerViewActions.scrollTo<SearchResultAdapter.SearchResultViewHolder>(
                    hasDescendant(
                        withText("xiaocong/uiautomator")
                    )
                )
            )
    }

    @Test
    fun activitySearch_PerformClickOnItem() {

        onView(withId(R.id.searchEditText)).perform(click())
        onView(withId(R.id.searchEditText)).perform(
            replaceText("uiautomator"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.searchButton)).perform(click())

        onView(isRoot()).perform(delay())

        onView(withId(R.id.recyclerView))
            .perform(
                RecyclerViewActions.scrollTo<SearchResultAdapter.SearchResultViewHolder>(
                    hasDescendant(withText("kkevsekk1/AutoX"))
                )
            )

        onView(withId(R.id.recyclerView))
            .perform(
                RecyclerViewActions.actionOnItem<SearchResultAdapter.SearchResultViewHolder>(
                    hasDescendant(withText("fmca/uiautomator")),
                    EspressoTestCustomAction(R.id.listItemCheckbox)
                )
            )

        onView(isRoot()).perform(delay())
    }


    @After
    fun close() {
        Intents.release()
        scenario.close()
    }
}
