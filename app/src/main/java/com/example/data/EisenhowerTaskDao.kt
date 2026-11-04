package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for persisting and retrieving Eisenhower Priority Matrix
 * tasks from local SQLite Room database across application sessions.
 */
@Dao
interface EisenhowerTaskDao {

    /**
     * Observes all tasks reactive to database modifications, ordered by creation time.
     */
    @Query("SELECT * FROM eisenhower_tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<EisenhowerTask>>

    /**
     * Observes tasks belonging to a specific quadrant ('DO', 'SCHEDULE', 'DELEGATE', 'ELIMINATE').
     */
    @Query("SELECT * FROM eisenhower_tasks WHERE quadrant = :quadrant ORDER BY createdAt DESC")
    fun getTasksByQuadrant(quadrant: String): Flow<List<EisenhowerTask>>

    /**
     * Observes a single task by ID.
     */
    @Query("SELECT * FROM eisenhower_tasks WHERE id = :id LIMIT 1")
    fun getTaskById(id: Long): Flow<EisenhowerTask?>

    /**
     * Count total stored tasks.
     */
    @Query("SELECT COUNT(*) FROM eisenhower_tasks")
    suspend fun getTaskCount(): Int

    /**
     * Inserts or replaces a task.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: EisenhowerTask): Long

    /**
     * Batch insert of tasks.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<EisenhowerTask>)

    /**
     * Updates an existing task.
     */
    @Update
    suspend fun updateTask(task: EisenhowerTask)

    /**
     * Deletes a task by entity reference.
     */
    @Delete
    suspend fun deleteTask(task: EisenhowerTask)

    /**
     * Deletes a task by primary key ID.
     */
    @Query("DELETE FROM eisenhower_tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    /**
     * Deletes all completed tasks.
     */
    @Query("DELETE FROM eisenhower_tasks WHERE status = 'completed'")
    suspend fun deleteCompletedTasks()

    /**
     * Clears all tasks from the table.
     */
    @Query("DELETE FROM eisenhower_tasks")
    suspend fun clearAllTasks()
}
