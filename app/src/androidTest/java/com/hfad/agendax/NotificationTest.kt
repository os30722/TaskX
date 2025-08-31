package com.hfad.agendax

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.hfad.agendax.data.TaskListAdapter
import com.hfad.agendax.ui.MainActivity
import com.hfad.agendax.utils.DataBindingIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.test.uiautomator.UiObject2


class NotificationTest {

    private var taskTitle = "Play Football"
    private var taskDescription = "Carry my stockings and my studs"

    @get:Rule
    val activityScenario = ActivityScenarioRule(MainActivity::class.java)

    private val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Before
    fun createNewTask() {
        //Crate task
        onView(ViewMatchers.withId(R.id.new_task_button)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.task_title))
            .perform(ViewActions.typeText(taskTitle))
        onView(ViewMatchers.withId(R.id.task_description_inptBx))
            .perform(ViewActions.typeText(taskDescription))
        onView(ViewMatchers.withId(R.id.task_save)).perform(ViewActions.click())

    }

    @Test
    fun testNotification() {
        mDevice.pressHome()
        mDevice.openNotification()

        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.resources.getString(R.string.app_name)

        mDevice.wait(Until.hasObject(By.textStartsWith(appName)), 10000)


        mDevice.pressBack()

        startActivity()
    }

    private fun startActivity(){
        val scenario: ActivityScenario<MainActivity> = launchActivity()
        scenario.moveToState(Lifecycle.State.RESUMED)
    }


    @After
    fun clearTask() {
        onView(ViewMatchers.withId(R.id.tasks_list)).perform(
            RecyclerViewActions.actionOnItem<TaskListAdapter.TaskCellHolder>(
                ViewMatchers.hasDescendant(ViewMatchers.withText(taskTitle)), ViewActions.click()
            )
        )
        //Check deletion of task
        onView(ViewMatchers.withId(R.id.task_delete)).perform(ViewActions.click())
        var error: Throwable? = null
        try {
            onView(ViewMatchers.withId(R.id.tasks_list)).perform(
                RecyclerViewActions.actionOnItem<TaskListAdapter.TaskCellHolder>(
                    ViewMatchers.hasDescendant(ViewMatchers.withText(taskTitle)),
                    ViewActions.click()
                )
            )
        } catch (e: Throwable) {
            error = e
        }

        assert(error!!.cause is PerformException)
    }


}