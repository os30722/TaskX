package com.hfad.agendax.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.preference.PreferenceManager
import com.hfad.agendax.R
import com.hfad.agendax.services.DailyService
import com.hfad.agendax.util.AlarmUtil
import java.util.*

class SplashScreenActivity : AppCompatActivity() {

    val FIRST_RUN_STRING = "first_run"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initSetUp()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initSetUp() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val lastStart = sharedPref.getLong(FIRST_RUN_STRING, 0)

        val currentTime = System.currentTimeMillis()


        // For re-setting alarms on device boot-up or reboot
        if(lastStart < (currentTime - SystemClock.elapsedRealtime())){
            val dailyService = Intent(this, DailyService::class.java)
            dailyService.putExtra("time", currentTime)
            this.startService(dailyService)
        }

        val editor = sharedPref.edit()
        editor.putLong(FIRST_RUN_STRING, currentTime)
        editor.apply()

        val calendar = Calendar.getInstance()
        AlarmUtil.setDayChangeAlarm(this, calendar)


    }
}