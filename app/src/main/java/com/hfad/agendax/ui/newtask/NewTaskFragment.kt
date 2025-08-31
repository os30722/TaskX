package com.hfad.agendax.ui.newtask

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.widget.DatePicker
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.hfad.agendax.databinding.FragmentNewTaskBinding
import com.hfad.agendax.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar
import com.hfad.agendax.R
import com.hfad.agendax.broadcasts.AlarmReceiver
import com.hfad.agendax.util.DateTime
import com.hfad.agendax.util.AlarmUtil
import com.hfad.agendax.vo.Task
import kotlinx.coroutines.launch
import android.util.DisplayMetrics
import androidx.fragment.app.setFragmentResult
import com.google.android.gms.ads.AdSize


@AndroidEntryPoint
class NewTaskFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private var binding by viewBinding<FragmentNewTaskBinding>()
    private val viewModel: NewTaskViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewTaskBinding.inflate(inflater, container, false)
        binding.model = viewModel
        binding.lifecycleOwner = this

        loadSettings()
        setHasOptionsMenu(true)
        return binding.root
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()

        inflater.inflate(R.menu.new_task_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    
    private fun loadSettings(){
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val timeFormat = sharedPref.getString("time_format",  "12")
        viewModel.timeFormat24 = (timeFormat == "24" )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar = requireArguments().getLong("calendar", 0L)
        if(calendar != 0L){
            viewModel.setCalendar(calendar)

        }

        val task: Task? = requireArguments().getParcelable("task")
        if(task != null){
            viewModel.setTask(task)
        }

        initAd()
        initDateSetting()
        initTimeSetting()

    }

    private fun initAd(){
        val adRequest = AdRequest.Builder().build()
        val adView = binding.adView
        adView.loadAd(adRequest)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.task_save -> {
                if(viewModel.title.value!!.isEmpty()){
                    Snackbar.make(binding.root, "Please Enter Title", Snackbar.LENGTH_SHORT).show()
                }
                else {
                    viewLifecycleOwner.lifecycleScope.launch {
                        val taskId = viewModel.saveTask()
                        if (viewModel.notification.value!!) {
                            if (DateTime.equalDate(
                                    Calendar.getInstance(),
                                    viewModel.calendar.value!!
                                )
                            )
                                AlarmUtil.setTaskAlarm(
                                    requireContext(),
                                    taskId,
                                    viewModel.title.value!!,
                                    viewModel.details.value!!,
                                    viewModel.calendar.value!!.timeInMillis
                                )
                        } else {
                            AlarmUtil.cancelTaskAlarm(requireContext(), taskId)
                        }

                        setFragmentResult("added_task", Bundle())
                        findNavController().popBackStack()
                    }
                }
            }
        }


        return super.onOptionsItemSelected(item)
    }


    private fun initDateSetting() {
        binding.dateSetting.setOnClickListener {
            viewModel.calendar.value?.let { it ->
                val datePicker =   DatePickerDialog(
                    requireContext(), this, it.get(Calendar.YEAR),
                    it.get(Calendar.MONTH), it.get(Calendar.DAY_OF_MONTH))
                datePicker.show()
            }
        }
    }

    private fun initTimeSetting() {
        binding.timeSetting.setOnClickListener{
            viewModel.calendar.value?.let {
                val timePickerOpt = MaterialTimePicker.Builder()
                    .setHour(it.get(Calendar.HOUR_OF_DAY))
                    .setMinute(it.get(Calendar.MINUTE))


                if(!viewModel.timeFormat24){
                    timePickerOpt.setTimeFormat(TimeFormat.CLOCK_12H)
                } else{
                    timePickerOpt.setTimeFormat(TimeFormat.CLOCK_24H)
                }

                val timePicker = timePickerOpt.build()

                timePicker.show(childFragmentManager, "time_picker")

                timePicker.addOnPositiveButtonClickListener {
                    viewModel.setTime(timePicker.hour, timePicker.minute)
                }

            }

        }
    }

    override fun onDateSet(datePicker: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.setDate(year, month, dayOfMonth)
    }

    override fun onPause() {
        super.onPause()
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.taskDescription.windowToken, 0)
    }


}