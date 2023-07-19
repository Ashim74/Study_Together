package com.droidnova.studytogether.database

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.flow.collect

class ProgressRepository(private val progressDao: ProgressDao) {

    suspend fun insertOrUpdateProgressEntry(progressEntry: ProgressEntity) {
        val existingEntries = ArrayList<ProgressEntity>()
        progressDao.getProgress().collect { list ->
            existingEntries.clear()
            existingEntries.addAll(list)
            val numberOfEntries = existingEntries.size

            if (numberOfEntries < 30) {
                // Less than 30 entries, insert directly
                progressDao.insertProgress(progressEntry)

            } else {
                // 30 entries reached, replace the oldest entry
                val oldestEntry = existingEntries[0]
                progressDao.deleteProgress(oldestEntry)

                // Shift remaining entries
                for (i in 1 until numberOfEntries) {
                    val shiftedEntry = existingEntries[i]
                    shiftedEntry.order = i - 1 // Update the order
                    progressDao.updateProgress(shiftedEntry)
                }

                // Insert the new entry at the last position
                progressEntry.order = numberOfEntries - 1 // Update the order
                progressDao.insertProgress(progressEntry)
            }
        }
    }

}
