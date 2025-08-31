package com.hfad.agendax.utils

import android.view.View
import android.widget.Checkable
import android.widget.TextView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.BaseMatcher
import org.hamcrest.CoreMatchers.isA
import org.hamcrest.Description
import org.hamcrest.Matcher

fun hasDrawable(): Matcher<View> {
    return object: BoundedMatcher<View, TextView>(TextView::class.java){
        override fun describeTo(description: Description?) {
            description?.appendText("has drawable")
        }

        override fun matchesSafely(item: TextView?): Boolean {
            return item?.background != null
        }

    }
}

fun setChecked(checked: Boolean) = object : ViewAction {
    val checkableViewMatcher = object : BaseMatcher<View>() {
        override fun matches(item: Any?): Boolean = isA(Checkable::class.java).matches(item)
        override fun describeTo(description: Description?) {
            description?.appendText("is Checkable instance ")
        }
    }

    override fun getConstraints(): BaseMatcher<View> = checkableViewMatcher
    override fun getDescription(): String? = null
    override fun perform(uiController: UiController?, view: View) {
        val checkableView: Checkable = view as Checkable
        checkableView.isChecked = checked
    }
}