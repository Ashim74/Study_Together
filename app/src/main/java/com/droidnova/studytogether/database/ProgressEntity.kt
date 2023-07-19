package com.droidnova.studytogether.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progress-table")
data class ProgressEntity(
    @PrimaryKey(autoGenerate = true)
    var id:Int,
    val date:String,
    val timeSpentDaily:String,
    var order: Int = 0 // New property to track the order
)
