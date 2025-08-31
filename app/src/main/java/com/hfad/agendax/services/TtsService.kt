package com.hfad.agendax.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.core.app.NotificationCompat
import com.hfad.agendax.R
import com.hfad.agendax.TASK_CHANNEL_ID
import com.hfad.agendax.broadcasts.AlarmReceiver
import com.hfad.agendax.repository.TaskRepository
import com.hfad.agendax.vo.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


@AndroidEntryPoint
class TtsService : Service(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var spokenText: String? = null

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val builder = NotificationCompat.Builder(this, TASK_CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Talking")
            .setPriority(NotificationCompat.PRIORITY_HIGH)


        startForeground(1, builder.build())


        spokenText = "Task reminder " + intent.getStringExtra("title")

        if (spokenText != null){
            tts = TextToSpeech(this, this)
            val speechRate = intent.getFloatExtra("speech_rate", 0.8f)
            tts!!.setSpeechRate(speechRate)
        }
        else
            stopSelf()

        return START_NOT_STICKY
    }

    override fun onInit(status: Int) {
        if (tts != null && status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)
            tts!!.setOnUtteranceProgressListener(object: UtteranceProgressListener(){
                override fun onStart(p0: String?) {
                }

                override fun onDone(p0: String?) {
                    stopSelf()
                }

                override fun onError(p0: String?) {
                    stopSelf()
                }

            })
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                tts!!.speak(spokenText, TextToSpeech.QUEUE_ADD, null, "utterance_id")

            }
        }
    }

    override fun onDestroy() {
        tts?.let{
            it.stop()
            it.shutdown()
        }
        super.onDestroy()
    }


}