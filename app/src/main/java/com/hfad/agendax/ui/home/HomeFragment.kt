package com.hfad.agendax.ui.home

import android.app.DatePickerDialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Parcelable
import android.text.BoringLayout.make
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.PagerSnapHelper
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.make
import com.hfad.agendax.R
import com.hfad.agendax.WeeksAdapter
import com.hfad.agendax.broadcasts.TimeChangeReceiver
import com.hfad.agendax.data.TaskDetailLookup
import com.hfad.agendax.data.TaskItemKeyProvider
import com.hfad.agendax.data.TaskListAdapter
import com.hfad.agendax.databinding.FragmentHomeBinding
import com.hfad.agendax.util.AlarmUtil
import com.hfad.agendax.util.DateTime
import com.hfad.agendax.util.getMonthWord
import com.hfad.agendax.util.viewBinding
import com.hfad.agendax.vo.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment(), WeeksAdapter.DateSelected, TaskListAdapter.TaskClickListener, DatePickerDialog.OnDateSetListener {

    private var binding by viewBinding<FragmentHomeBinding>()
    private val viewModel: HomeViewModel by viewModels()
    private var weekAdapter: WeeksAdapter? = null
    private lateinit var timeReceiver: TimeChangeReceiver
    private var taskAdapter: TaskListAdapter? = null
    private  var selectionTracker: SelectionTracker<Long>? = null
    private var refreshListState = false
    private lateinit var callback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED)
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED)
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED)

        timeReceiver = TimeChangeReceiver()
        timeReceiver.registerDateChangeListener(object: TimeChangeReceiver.DateChangeListener{
            override fun onDateChange(calendar: Calendar) {
                weekAdapter?.changeCurrentDate(calendar)
                viewModel.currentDate.value = calendar
                if(!viewModel.selectedDate.value!!.equals(calendar)) showDateFinder(true) else showDateFinder(false)
            }

        })
        requireActivity().registerReceiver(timeReceiver, intentFilter)

        viewModel.fetchTasks(viewModel.selectedDate.value!!)

        callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            selectionTracker?.clearSelection()
        }
        callback.isEnabled = false

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.model = viewModel
        binding.lifecycleOwner = this

        weekAdapter = WeeksAdapter()
        taskAdapter = TaskListAdapter()

        loadSettings()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initHorizontalCalendar()
        initTaskList()
        initButtons()

        viewModel.isLoading.observe(viewLifecycleOwner){
            showProgressBar(it!!)
        }

        setFragmentResultListener("added_task"){key, bundle ->
            Toast.makeText(requireContext(), "Task Added", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun loadSettings(){
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val timeFormat = sharedPref.getString("time_format",  "12")
        viewModel.timeFormat24 = (timeFormat == "24" )

        binding.dateSelectorWrapper.setOnClickListener{
            val selectedDate = viewModel.selectedDate.value!!
            val datePicker =   DatePickerDialog(
                requireContext(), this, selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }


        callback.isEnabled = false
    }

    private fun initButtons() {

        binding.newTaskButton.setOnClickListener{
            val calendar = Calendar.getInstance()
            val selectedDate = viewModel.selectedDate.value
            calendar.set(selectedDate?.get(Calendar.YEAR)!!,
                selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DATE)
            )
            val bundle = bundleOf(
                "calendar" to  calendar.timeInMillis
            )
            findNavController().navigate(R.id.action_home_to_newTask, bundle)
        }

        binding.settingsButton.setOnClickListener{
            findNavController().navigate(R.id.action_home_to_settings_nav)
        }

        binding.currentDateFinder.setOnClickListener{
            this.onDateSelected(viewModel.currentDate.value!!)
        }

        binding.closeSelectionBtn.setOnClickListener{
            selectionTracker?.clearSelection()
        }

        binding.deleteSelectionBtn.setOnClickListener{
            selectionTracker?.let {
                viewLifecycleOwner.lifecycleScope.launch {
                    showProgressBar(true)
                    it.selection.forEach { taskUid ->
                        AlarmUtil.cancelTaskAlarm(requireContext(), taskUid.toInt())
                    }
                    viewModel.deleteTasks(it.selection.toList())
                    it.clearSelection()
                    showProgressBar(false)
                }
            }

        }

    }

    private fun initHorizontalCalendar() {
        val recyclerView = binding.weeksView
        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(recyclerView)
        weekAdapter?.setFirstDateListener(object : WeeksAdapter.FirstDate {
            val startDate = viewModel.currentDate.value!!.clone() as Calendar

            init {
                startDate.set(Calendar.DAY_OF_WEEK, startDate.firstDayOfWeek)
                // Taking padding into consideration
                while(startDate.get(Calendar.MONTH) != viewModel.currentDate.value!!.get(Calendar.MONTH)){
                    startDate.add(Calendar.DATE, 1)
                }
            }

            override fun onFirstDateChange(date: Calendar){
                viewModel.currentMonth.value = getMonthWord(date.get(Calendar.MONTH))
                viewModel.currentYear.value = date.get(Calendar.YEAR).toString()
                if(date.equals(startDate) && viewModel.selectedDate.value?.equals(viewModel.currentDate.value)!!){
                    showDateFinder(false)
                } else {
                    showDateFinder(true)
                }

            }
        })
        weekAdapter?.setDateSelectListener(this)
        recyclerView.adapter = weekAdapter
        viewModel.selectedDate.observe(viewLifecycleOwner){
            refreshListState = true
            weekAdapter?.setSelectedDate(it)
            viewModel.fetchTasks(it)
        }

    }

    private fun showDateFinder(show: Boolean) {
        val currentDateFinder = binding.currentDateFinder
        if(show){
            currentDateFinder.animate().alpha(1F)
        } else {
            currentDateFinder.animate().alpha(0.0F)
        }
    }

    private fun showProgressBar(show: Boolean){
        if(show){
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun initTaskList() {
        val recyclerView = binding.tasksList
        recyclerView.adapter = taskAdapter

        taskAdapter?.setTaskClickListener(this)
        taskAdapter?.set24Hour(viewModel.timeFormat24)

        viewModel.tasksData.observe(viewLifecycleOwner) {tasks ->
            var taskListState: Parcelable? = null
            if(!refreshListState) {
                taskListState = recyclerView.layoutManager?.onSaveInstanceState()
            }
            taskAdapter?.submitList(tasks)
            recyclerView.layoutManager?.onRestoreInstanceState(taskListState)
            refreshListState = false

        }

        selectionTracker = SelectionTracker.Builder(
            "task_selection",
            recyclerView,
            TaskItemKeyProvider(recyclerView),
            TaskDetailLookup(recyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        taskAdapter?.selectionTracker = selectionTracker

        selectionTracker?.addObserver(object : SelectionTracker.SelectionObserver<Long>(){
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                val selectionSize = selectionTracker?.selection?.size()!!
                binding.itemsSelectionSize = selectionSize
                callback.isEnabled = selectionSize > 0
            }
        })

    }

    override fun onDateSelected(date: Calendar) {
        viewModel.selectedDate.value = date
        if(date.equals(viewModel.currentDate.value)){
            showDateFinder(false)
        } else {
            showDateFinder(true)
        }
        selectionTracker?.clearSelection()
    }

    override fun onTaskClicked(task: Task) {
        val bundle = bundleOf(
            "taskUid" to task.uid
        )
        findNavController().navigate(R.id.action_home_to_taskInfo, bundle)
    }

    override fun onTaskCompleted(task: Task, completed: Boolean) {
        viewModel.updateTaskCompletion(task.uid, completed)
        if(!completed && (Calendar.getInstance().timeInMillis <= task.calendar.timeInMillis)){
            AlarmUtil.setTaskAlarm(requireContext(), task)
        } else {
            AlarmUtil.cancelTaskAlarm(requireContext(), task.uid)
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        DateTime.stripTime(calendar)
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        this.onDateSelected(calendar)
    }


    override fun onDestroyView() {
        weekAdapter = null
        taskAdapter = null
        selectionTracker = null
        super.onDestroyView()
    }


    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(timeReceiver)
    }


}