package com.bakar.moodtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moods")
data class MoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Long,
    val moodValue: Int,          // 3 good, 2 normal, 1 bad
    val emotionType: String,     // Happy, Neutral, Sad, Stressed
    val timeOfDay: String,       // Morning, Afternoon, Night
    val note: String? = null
)
