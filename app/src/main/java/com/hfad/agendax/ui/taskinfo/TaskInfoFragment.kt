package com.hfad.agendax.ui.taskinfo

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.AdRequest
import com.hfad.agendax.R
import com.hfad.agendax.databinding.FragmentTaskInfoBinding
import com.hfad.agendax.util.AlarmUtil
import com.hfad.agendax.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TaskInfoFragment : Fragment() {

    private var binding by viewBinding<FragmentTaskInfoBinding>()
    private val viewModel: TaskInfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTaskInfoBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        val taskUid = requireArguments().getInt("taskUid")
        viewModel.fetchTask(taskUid)

        setHasOptionsMenu(true)
        loadSettings()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.task.observe(viewLifecycleOwner) { task ->
            binding.model = task
        }

        initAd()
    }

    private fun initAd(){
        val adRequest = AdRequest.Builder().build()
        val adView = binding.adView
        adView.loadAd(adRequest)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()

        inflater.inflate(R.menu.task_info_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    private fun loadSettings(){
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val timeFormat = sharedPref.getString("time_format",  "12")
        viewModel.timeFormat24 = (timeFormat == "24" )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.task_edit -> {
                val bundle = bundleOf(
                    "task" to viewModel.task.value
                )
                findNavController().navigate(R.id.action_taskInfo_to_newTask, bundle)
            }

            R.id.task_delete -> {
                viewModel.deleteTask()
                AlarmUtil.cancelTaskAlarm(requireContext(), viewModel.task.value!!.uid)
                findNavController().popBackStack()
            }
        }

        return super.onOptionsItemSelected(item)
    }


}