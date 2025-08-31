package com.hfad.agendax

import android.widget.DatePicker
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.hfad.agendax.ui.home.HomeFragment
import com.hfad.agendax.util.getMonthWord
import com.hfad.agendax.utils.hasDrawable
import com.hfad.agendax.utils.launchFragmentInHiltContainer
import com.hfad.agendax.utils.monitorFragment
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import android.R.string.no
import android.util.Log
import com.hfad.agendax.util.getDayWord


@RunWith(AndroidJUnit4ClassRunner::class)
class HomeFragmentTest {

    @Before
    fun setUp() {
        launchFragmentInHiltContainer<HomeFragment>()
    }

    @Test
    fun testMonthYearLabel() {
        val calendar = Calendar.getInstance()
        onView(withId(R.id.month_text)).check(
            matches(
                withText(
                    getMonthWord(calendar.get(Calendar.MONTH)) + ", " + calendar.get(
                        Calendar.YEAR
                    )
                )
            )
        )

        val dateText = calendar.get(Calendar.DATE).toString()
        onView(allOf(withId(R.id.week_cell_date), withText(dateText))).check(
            matches(hasDrawable())
        )

    }

    @Test
    fun testCurrentDateFinderOnSwiping() {
        //Check swipes
        onView(withId(R.id.current_date_finder)).check(matches(withAlpha(0.0F)))
        onView(withId(R.id.weeks_view)).perform(swipeRight())
        Thread.sleep(1000) //Account for scrolling
        onView(withId(R.id.current_date_finder)).check(matches(withAlpha(1F)))
        onView(withId(R.id.weeks_view)).perform(swipeLeft(), swipeLeft())
        Thread.sleep(1000) //Account for scrolling
        onView(withId(R.id.current_date_finder)).check(matches(withAlpha(1F)))
        onView(withId(R.id.weeks_view)).perform(swipeRight())
        Thread.sleep(1000) //Account for scrolling
        onView(withId(R.id.current_date_finder)).check(matches(withAlpha(0.0F)))

    }

    @Test
    fun testCurrentDateOnSelection() {
        val calendar = Calendar.getInstance()
        var negShift = false
        if (calendar.get(Calendar.DAY_OF_WEEK) == 7)
            negShift = true

        if(negShift)
            calendar.add(Calendar.DATE, -1)
        else
            calendar.add(Calendar.DATE, 1)

        onView(allOf(withId(R.id.week_cell_date), withText(calendar.get(Calendar.DATE).toString())))
            .perform(click())
        onView(withId(R.id.current_date_finder)).check(matches(withAlpha(1F)))

        if(negShift)
            calendar.add(Calendar.DATE, 1)
        else
            calendar.add(Calendar.DATE, -1)


        onView(allOf(withId(R.id.week_cell_date), withText(calendar.get(Calendar.DATE).toString())))
            .perform(click())

        onView(withId(R.id.current_date_finder)).check(matches(withAlpha(0.0F)))

    }

    @Test
    fun testDatePicker() {
        for (i: Int in 0..15) {
            val calendar = generateRandomDate()
            setDate(calendar)
            val dateText = calendar.get(Calendar.DATE).toString()
            val monthText = getMonthWord(calendar.get(Calendar.MONTH)) + ", " + calendar.get(
                Calendar.YEAR
            )
            val dayText = getDayWord(calendar.get(Calendar.DAY_OF_WEEK))

            onView(allOf(withId(R.id.week_cell_date), withText(dateText))).check(
                matches(hasDrawable())
            )

            onView(
                allOf(
                    withId(R.id.week_cell_day),
                    withText(dayText)
                )
            ).check(matches(hasSibling(withText(dateText))))

            onView(withId(R.id.month_text)).check(
                matches(withText(monthText)))

        }

    }

    private fun generateRandomDate(): Calendar {
        val rnd = Random(System.currentTimeMillis())
        val ms = -946771200000L + (Math.abs(rnd.nextLong()) % (200L * 365 * 24 * 60 * 60 * 1000))
        val date = Calendar.getInstance()
        date.timeInMillis = ms
        return date
    }

    private fun setDate(calendar: Calendar) {
        val year = calendar.get(Calendar.YEAR)
        val monthOfYear = calendar.get(Calendar.MONTH) + 1
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        onView(withId(R.id.date_selector_wrapper)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).perform(
            PickerActions.setDate(
                year,
                monthOfYear,
                dayOfMonth
            )
        )
        onView(withId(android.R.id.button1)).perform(click())
    }


}