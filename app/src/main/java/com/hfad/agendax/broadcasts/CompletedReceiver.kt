package com.hfad.agendax.broadcasts

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hfad.agendax.repository.TaskRepository
import com.hfad.agendax.widget.TaskListWidgetProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CompletedReceiver: BroadcastReceiver() {

    @Inject
    lateinit var repository: TaskRepository

    @DelicateCoroutinesApi
    override fun onReceive(context: Context, intent: Intent) {

        val taskUid = intent.getIntExtra("taskUid", 0)

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(taskUid)

        GlobalScope.launch(Dispatchers.IO){
            repository.updateTaskCompletion(taskUid, true)
        }

        TaskListWidgetProvider.sendRefreshBroadcast(context)

    }
}