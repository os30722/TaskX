package com.hfad.agendax.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.hfad.agendax.App
import com.hfad.agendax.R
import com.hfad.agendax.ui.MainActivity
import com.hfad.agendax.ui.SplashScreenActivity

const val TASK_DETAIL_ACTION = "task_detail_action"
const val DATE_CHANGED = "android.intent.action.DATE_CHANGED"

class TaskListWidgetProvider: AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        val intentAction = intent.action

        if(intentAction.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)){
            val mgr = AppWidgetManager.getInstance(context)
            val cn = ComponentName(context, TaskListWidgetProvider::class.java)
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.task_collection)
        }

        if(intentAction.equals(TASK_DETAIL_ACTION)){
            val taskUid = intent.getIntExtra("task_uid", 0)

            val bundle = bundleOf(
                "taskUid" to taskUid
            )

            val contentIntent = NavDeepLinkBuilder(context)
                .setGraph(R.navigation.main_nav)
                .setDestination(R.id.taskInfo)
                .setArguments(bundle)
                .setComponentName(MainActivity::class.java)
                .createPendingIntent()
            contentIntent.send()

        }

        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach {appWidgetId ->
            val rv = RemoteViews(context.packageName, R.layout.task_list_widget)
            val intent = Intent(context, TaskListRemoteService::class.java)
            rv.setRemoteAdapter(R.id.task_collection, intent)

            val pendingIntent = NavDeepLinkBuilder(context)
                .setGraph(R.navigation.main_nav)
                .setDestination(R.id.newTask)
                .setComponentName(MainActivity::class.java)
                .createPendingIntent()
            rv.setOnClickPendingIntent(R.id.add_button_widget, pendingIntent)

            //Setting pending intent for individual items
            val detailPendingIntent: PendingIntent = Intent(
                context,
                TaskListWidgetProvider::class.java
            ).run {
                // Set the action for the intent.
                // When the user touches a particular view, it will have the effect of
                // broadcasting TOAST_ACTION.
                action = TASK_DETAIL_ACTION
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

                PendingIntent.getBroadcast(context, appWidgetId, this, PendingIntent.FLAG_UPDATE_CURRENT)
            }

            rv.setPendingIntentTemplate(R.id.task_collection, detailPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, rv)
        }
    }

    companion object {
        fun sendRefreshBroadcast(context: Context) {
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            intent.setComponent(ComponentName(context, TaskListWidgetProvider::class.java))
            context.sendBroadcast(intent)
        }
    }
}