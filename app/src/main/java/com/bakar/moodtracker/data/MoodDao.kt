package com.bakar.moodtracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MoodDao {

    // Insert new mood
    @Insert
    suspend fun insert(mood: MoodEntity)

    // Get all moods (for full history list)
    @Query("SELECT * FROM moods ORDER BY date DESC")
    suspend fun getAll(): List<MoodEntity>

    // ✅ Get moods for a selected day (USED FOR DONUT CHART)
    @Query("""
        SELECT * FROM moods 
        WHERE date BETWEEN :start AND :end 
        ORDER BY date DESC
    """)
    suspend fun getByDateRange(
        start: Long,
        end: Long
    ): List<MoodEntity>
}
