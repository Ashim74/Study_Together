package com.droidnova.studytogether.alarmmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.droidnova.studytogether.Constants
import com.droidnova.studytogether.database.ProgressDao
import com.droidnova.studytogether.database.ProgressDatabase
import com.droidnova.studytogether.database.ProgressEntity

class DailyAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Perform the action you want to do at 12 AM
        val sharedPreferences = context.getSharedPreferences("StopwatchPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val currentTime = System.currentTimeMillis()
        val lastPauseTime = sharedPreferences.getLong("lastPauseTime", 0)
        val timeDifference = currentTime - lastPauseTime

        val isRunning = sharedPreferences.getBoolean("isRunning", false)
        var elapsedTime = sharedPreferences.getLong("elapsedTime", 0)

        if (isRunning) {
            // If the stopwatch was running, add the time difference to the elapsed time
            elapsedTime += timeDifference
        }
        editor.putLong("elapsedTime", 0)
        editor.putLong("startTime",currentTime)
        editor.putLong("lastPauseTime", currentTime)
        editor.apply()
        Log.d("DailyAlarmReceiver", "Action performed at 12 AM")
    }


}
