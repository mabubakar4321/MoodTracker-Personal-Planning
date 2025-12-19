package com.bakar.moodtracker.util

object MoodUtils {

    fun getMoodText(value: Int): String {
        return when (value) {
            3 -> "Good"
            2 -> "Normal"
            else -> "Bad"
        }
    }

    fun moodValueFromEmotion(emotion: String): Int {
        return when (emotion) {
            "Happy" -> 3
            "Neutral" -> 2
            "Sad", "Stressed" -> 1
            else -> 2
        }
    }
}
