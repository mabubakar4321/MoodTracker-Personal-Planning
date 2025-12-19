package com.bakar.moodtracker.data

import androidx.room.*

@Dao
interface RoutineDao {

    @Insert
    suspend fun insert(routine: RoutineEntity)

    @Update
    suspend fun update(routine: RoutineEntity)

    @Delete
    suspend fun delete(routine: RoutineEntity)

    @Query("SELECT * FROM routines ORDER BY date DESC")
    suspend fun getAll(): List<RoutineEntity>

    @Query("SELECT * FROM routines WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): RoutineEntity?
}
