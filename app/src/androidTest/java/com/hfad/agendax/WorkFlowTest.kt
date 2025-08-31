package com.hfad.agendax


import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.hfad.agendax.data.TaskListAdapter
import com.hfad.agendax.ui.MainActivity
import com.hfad.agendax.utils.DataBindingIdlingResource
import com.hfad.agendax.utils.setChecked
import org.hamcrest.core.IsNot.not
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@LargeTest
class WorkFlowTest {

    private var taskTitle = "Play Football"
    private var taskDescription = "Carry my stockings and my studs"

    @get:Rule
    val activityScenario = ActivityScenarioRule(MainActivity::class.java)

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun createNewTask() {
        //Crate task
        onView(withId(R.id.new_task_button)).perform(click())
        onView(withId(R.id.notification_input)).perform(setChecked(false))
        onView(withId(R.id.task_title)).perform(typeText(taskTitle))
        onView(withId(R.id.task_description_inptBx)).perform(typeText(taskDescription))
        onView(withId(R.id.task_save)).perform(click())

    }

    @Test
    fun testTaskEntry() {
        //Check Task Entry
        onView(withId(R.id.tasks_list)).perform(
            RecyclerViewActions.actionOnItem<TaskListAdapter.TaskCellHolder>(
                hasDescendant(withText(taskTitle)), click()
            )
        )

        onView(withId(R.id.task_title)).check(matches(withText(taskTitle)))
        onView(withId(R.id.task_detail)).check(matches(withText(taskDescription)))


        Espresso.pressBack()
    }

    @Test
    fun testEditTask() {
        onView(withId(R.id.tasks_list)).perform(
            RecyclerViewActions.actionOnItem<TaskListAdapter.TaskCellHolder>(
                hasDescendant(withText(taskTitle)), click()
            )
        )
        onView(withId(R.id.task_edit)).perform(click())
        taskTitle = "Changed Title"
        taskDescription = "Changed Description"
        onView(withId(R.id.task_title)).perform(replaceText(taskTitle))
        onView(withId(R.id.task_description_inptBx)).perform(replaceText(taskDescription))
        onView(withId(R.id.task_save)).perform(click())

        Espresso.pressBack()
    }

    @After
    fun clearTask() {
        onView(withId(R.id.tasks_list)).perform(
            RecyclerViewActions.actionOnItem<TaskListAdapter.TaskCellHolder>(
                hasDescendant(withText(taskTitle)), click()
            )
        )
        //Check deletion of task
        onView(withId(R.id.task_delete)).perform(click())
        var error: Throwable? = null
        try {
            onView(withId(R.id.tasks_list)).perform(
                RecyclerViewActions.actionOnItem<TaskListAdapter.TaskCellHolder>(
                    hasDescendant(withText(taskTitle)), click()
                )
            )
        } catch (e: Throwable) {
            error = e
        }

        assert(error!!.cause is PerformException)
    }
}