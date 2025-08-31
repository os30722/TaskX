package com.hfad.agendax.widget

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.hfad.agendax.R
import com.hfad.agendax.db.TaskDao
import com.hfad.agendax.db.TaskDatabase
import com.hfad.agendax.util.DateTime
import com.hfad.agendax.vo.Task
import java.util.*

class TaskListRemoteFactory(
    private val context: Context,
    private val intent: Intent
): RemoteViewsService.RemoteViewsFactory {

    private var pendingTask: List<Task>? = null

    override fun onCreate() {
    }

    override fun onDataSetChanged() {
        val database = TaskDatabase.getInstance(context)
        val taskDao = database.taskDao()
        val calendar = Calendar.getInstance()
        pendingTask = taskDao.getPendingTasks(Task.getTaskDate(calendar))
    }

    override fun onDestroy() {
        pendingTask = null
    }

    override fun getCount(): Int {
        return  pendingTask?.size ?: 0
    }

    override fun getViewAt(position: Int): RemoteViews? {
        if(position == AdapterView.INVALID_POSITION || pendingTask == null){
            return null
        }

        val fillInIntent = Intent().apply {
            Bundle().also { extras ->
                extras.putInt("task_uid", pendingTask!![position].uid)
                putExtras(extras)
            }
        }

        val rv = RemoteViews(context.packageName, R.layout.widget_list_item)
        rv.setTextViewText(R.id.task_title, pendingTask!![position].title)
        rv.setTextViewText(R.id.task_time, DateTime.getDisplayDate(pendingTask!![position].calendar))
        rv.setOnClickFillInIntent(R.id.task_collection_holder, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        if(pendingTask == null) return position.toLong()
        return pendingTask!![position].uid.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}

class TaskListRemoteService: RemoteViewsService(){
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return TaskListRemoteFactory(this.applicationContext, intent)
    }

}