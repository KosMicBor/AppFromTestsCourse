package com.geekbrains.tests.utils

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import org.hamcrest.Matcher

class EspressoTestCustomAction(private var id: Int = 0) : ViewAction {

    override fun getConstraints(): Matcher<View>? {
        return null
    }

    override fun getDescription(): String {
        return "Click on view with some ID"
    }

    override fun perform(uiController: UiController?, view: View?) {
        val someView = view?.findViewById(id) as View
        someView.performClick()
    }

    fun setNewId(newId: Int) {
        id = newId
    }
}