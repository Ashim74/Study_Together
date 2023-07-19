package com.droidnova.studytogether.fragments.yourProgress

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.droidnova.studytogether.Constants
import com.droidnova.studytogether.R
import com.droidnova.studytogether.alarmmanager.DailyAlarmReceiver
import com.droidnova.studytogether.database.ProgressDatabase
import com.droidnova.studytogether.database.ProgressEntity
import com.droidnova.studytogether.databinding.FragmentYourProgressBinding
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class YourProgressFragment : Fragment() {
    private lateinit var binding: FragmentYourProgressBinding

    private val currentElapsedTime: MutableLiveData<Long> by lazy {
        MutableLiveData<Long>()
    }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private var startTime: Long = 0
    private var isRunning: Boolean = false
    private var elapsedTime: Long = 0
    private var historyList = ArrayList<ProgressEntity>()

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentYourProgressBinding.inflate(layoutInflater,container,false)
       // val view = inflater.inflate(R.layout.fragment_your_progress, container, false)
       // tvTime = view.findViewById(R.id.tv_spent_time)
      //  btnStartStop = view.findViewById(R.id.btnStartStudying)
        sharedPreferences = requireActivity().getSharedPreferences("StopwatchPrefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        binding.btnStartStudying.setOnClickListener {
            if (isRunning) {
                binding.btnStartStudying.text = "Start"
                stopStopwatch()
            } else {
                binding.btnStartStudying.text = "Stop"
                startStopwatch()
            }
        }

        binding.llHistory.setOnClickListener {
            findNavController().navigate(R.id.spentTimeHistory)
        }


        currentElapsedTime.observe(viewLifecycleOwner) { elapsedTime ->
           setTime(elapsedTime)
        }

        scheduleDailyAlarm()

        // Create the Runnable that will be executed every 1 minute
       runnable = Runnable {
            // Perform your task here
            // This code will be executed every 1 minute
            makeDummyHistory()

            // Schedule the next execution after 1 minute
            handler.postDelayed(runnable, 6000) // 1 minute = 60,000 milliseconds
        }

        // Start the task immediately
        handler.post(runnable)

        return binding.root

    }

    private fun makeDummyHistory(){
        val sharedPreferences = requireActivity().getSharedPreferences("StopwatchPrefs", Context.MODE_PRIVATE)
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


        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Note: Month starts from 0, so add 1 to get the actual month
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val currentDate = "$day-$month-$year" // Format the date as per your requirement

        val databaseDao = ProgressDatabase.getDatabase(requireContext()).progressDao()

        lifecycleScope.launch {
            //databaseDao.insertProgress(ProgressEntity(0,"updated",System.currentTimeMillis().toString()))
           // val progressEntry = ProgressEntity(0,"updated",System.currentTimeMillis().toString())
            val existingEntries = ArrayList<ProgressEntity>()
            databaseDao.getProgress().collect { list ->
                existingEntries.clear()
                existingEntries.addAll(list)
                val numberOfEntries = existingEntries.size
                val progressEntry = ProgressEntity(0,"updated",System.currentTimeMillis().toString(),numberOfEntries-1)
                if (numberOfEntries < 30) {
                    // Less than 30 entries, insert directly
                    databaseDao.insertProgress(progressEntry)

                } else {
                    // 30 entries reached, replace the oldest entry
                    val oldestEntry = existingEntries[0]
                    databaseDao.deleteProgress(oldestEntry)

                    // Shift remaining entries
                    for (i in 1 until numberOfEntries) {
                        val shiftedEntry = existingEntries[i]
                        shiftedEntry.order = i - 1 // Update the order
                        databaseDao.updateProgress(shiftedEntry)
                    }

                    // Insert the new entry at the last position
                    progressEntry.order = numberOfEntries - 1 // Update the order
                    databaseDao.insertProgress(progressEntry)
                }
            }
        }

        Log.e("myTag list",historyList.toString())
        Log.e("myTag planned",(elapsedTime).toString())

    }

    private fun setTime(elapsedTime:Long){
        val seconds = (elapsedTime / 1000).toInt()
        val minutes = seconds / 60
        val hours = minutes / 60
        val time = String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60)
        binding.tvSpentTime.text = time
    }

    private fun scheduleDailyAlarm() {
        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(requireContext(), DailyAlarmReceiver::class.java)
        alarmIntent.putExtra(Constants.EXTRA_DATA_FROM_YOUR_PROGRESS_FRAGMENT_TO_ALARM_RECEIVER,elapsedTime)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            100,
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Set the alarm time to 12 AM
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        // Schedule the alarm to repeat every day at 12 AM
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun startStopwatch() {
        if (!isRunning) {
            isRunning = true
            startTime = System.currentTimeMillis() - elapsedTime

            editor.putBoolean("isRunning", true)
            editor.putLong("startTime", startTime)
            editor.apply()

            Thread {
                while (isRunning) {
                    val currentTime = System.currentTimeMillis()
                    elapsedTime = currentTime - startTime
                    currentElapsedTime.postValue(elapsedTime)
                    Thread.sleep(1000)
                }
            }.start()
        }
    }

    private fun stopStopwatch() {
        isRunning = false
        editor.putBoolean("isRunning", false)
        editor.putLong("elapsedTime", elapsedTime)
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        val currentTime = System.currentTimeMillis()
        val lastPauseTime = sharedPreferences.getLong("lastPauseTime", 0)
        val timeDifference = currentTime - lastPauseTime

        isRunning = sharedPreferences.getBoolean("isRunning", false)
        elapsedTime = sharedPreferences.getLong("elapsedTime", 0)

        if (isRunning) {
            isRunning = false
            // If the stopwatch was running, add the time difference to the elapsed time
            elapsedTime += timeDifference
            startStopwatch()
            binding.btnStartStudying.text = "Stop"
        } else {
            // If the stopwatch was not running, update the time
            setTime(elapsedTime)
            binding.btnStartStudying.text = "Start"
        }

        // Save the current time as the last pause time
        editor.putLong("lastPauseTime", currentTime)
        editor.apply()
    }

    override fun onPause() {
        super.onPause()
        if (isRunning){
            editor.putBoolean("isRunning", true)
            editor.putLong("elapsedTime", elapsedTime)
            editor.putLong("startTime",System.currentTimeMillis())
            editor.apply()
        }else{
            editor.putBoolean("isRunning", false)
            editor.putLong("elapsedTime", elapsedTime)
            editor.apply()
        }

        Log.e("myTag pause",isRunning.toString())

    }
}