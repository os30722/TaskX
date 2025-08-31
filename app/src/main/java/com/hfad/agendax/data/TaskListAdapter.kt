package com.hfad.agendax.data

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ObservableField
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hfad.agendax.databinding.TaskCellBinding
import com.hfad.agendax.util.DateTime
import com.hfad.agendax.vo.Task
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class TaskListAdapter():
    RecyclerView.Adapter<TaskListAdapter.TaskCellHolder>() {

    init {
        setHasStableIds(true)
    }

    private var taskClickCallback: TaskClickListener? = null
    var timeHour24 = false
    var selectionTracker: SelectionTracker<Long>? = null
    private var completedTaskId = ObservableField(-1)
    private var tasks: List<Task> = mutableListOf()

    interface TaskClickListener{
        fun onTaskClicked(task: Task)
        fun onTaskCompleted(task: Task, completed: Boolean)
    }

    fun setTaskClickListener(callback: TaskClickListener){
        this.taskClickCallback = callback
    }

    fun set24Hour(enable: Boolean){
        timeHour24 = enable
    }

    fun submitList(tasks: List<Task>){
        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(
            TaskDiffCallback(
                this.tasks,
                tasks
            )
        )
        this.tasks = tasks
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemId(position: Int): Long = tasks[position].uid.toLong()

    class TaskCellHolder(val binding: TaskCellBinding): RecyclerView.ViewHolder(binding.root){
        companion object {
            fun create(parent: ViewGroup): TaskCellHolder {
                val holder = TaskCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return TaskCellHolder(holder)
            }
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object: ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int {
                    return adapterPosition
                }

                override fun getSelectionKey(): Long? {
                    return itemId
                }
            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskCellHolder {
        return TaskCellHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TaskCellHolder, position: Int) {
        holder.binding.model = tasks[position]
        holder.binding.callback = taskClickCallback
        holder.binding.completedTaskHeaderId = completedTaskId

        selectionTracker?.let {
            holder.binding.isSelected = it.isSelected(tasks[position].uid.toLong())
        }

        if(tasks[position].completed){
            if(position == 0 || !tasks[position - 1].completed) {
                completedTaskId.set(tasks[position].uid)
            }
        } else{
            if(tasks[position].uid == completedTaskId.get()){
                completedTaskId.set(-1)
            }
        }

        completedTaskId.notifyChange()

        holder.binding.taskTime.text = DateTime.getDisplayDate(tasks[position].calendar, timeHour24)

        //Test Purpose
//        holder.binding.executePendingBindings()

    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    class TaskDiffCallback(
        var oldTasks: List<Task>,
        var newTasks: List<Task>
    ): DiffUtil.Callback(){
        override fun getOldListSize(): Int {
            return oldTasks.size
        }

        override fun getNewListSize(): Int {
            return newTasks.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return false
        }

    }


}

