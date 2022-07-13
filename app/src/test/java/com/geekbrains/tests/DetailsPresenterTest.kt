package com.geekbrains.tests

import android.os.Build
import android.widget.Button
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.geekbrains.tests.presenter.details.DetailsPresenter
import com.geekbrains.tests.view.details.DetailsActivity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class DetailsPresenterTest {

    private lateinit var presenter: DetailsPresenter

    private lateinit var textView: TextView
    private lateinit var incrementButton: Button

    private lateinit var scenario: ActivityScenario<DetailsActivity>

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(DetailsActivity::class.java)

        presenter = DetailsPresenter()

        scenario.onActivity {
            presenter.onAttach(it)
            textView = it.findViewById(R.id.totalCountTextView)
            incrementButton = it.findViewById(R.id.incrementButton)
        }
    }

    @Test
    fun onAttach_Test() {
        scenario.onActivity {
            presenter.onIncrement()
            assertEquals("Number of results: 1", textView.text)
        }
    }

    @Test
    fun onDetach_Test() {

        presenter.onDetach()

        scenario.onActivity {

            presenter.onIncrement()

            assertNotEquals("Number of results: 1", textView.text.toString())
        }
    }

    @Test
    fun setCounter_Test() {

        scenario.onActivity {
            presenter.setCounter(10)

            presenter.onIncrement()

            assertEquals("Number of results: 11", textView.text.toString())
        }
    }

    @Test
    fun onIncrement_Test() {

        scenario.onActivity {
            presenter.onIncrement()

            val textView = it.findViewById<TextView>(R.id.totalCountTextView)

            assertEquals("Number of results: 1", textView.text.toString())
        }
    }

    @Test
    fun onDecrement_Test() {

        scenario.onActivity {
            presenter.onDecrement()

            val textView = it.findViewById<TextView>(R.id.totalCountTextView)

            assertEquals("Number of results: -1", textView.text.toString())
        }
    }

    @After
    fun omClose() {
        scenario.close()
    }
}