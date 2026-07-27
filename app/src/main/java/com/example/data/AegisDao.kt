package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AegisDao {
    // Session Logs
    @Query("SELECT * FROM aegis_session_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AegisSessionLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AegisSessionLog): Long

    @Query("DELETE FROM aegis_session_logs")
    suspend fun clearLogs()

    // Executive Tasks
    @Query("SELECT * FROM aegis_tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<AegisTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: AegisTask): Long

    @Update
    suspend fun updateTask(task: AegisTask)

    @Query("DELETE FROM aegis_tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: Long)

    // Security Audit Events
    @Query("SELECT * FROM aegis_security_events ORDER BY timestamp DESC")
    fun getAllSecurityEvents(): Flow<List<AegisSecurityEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecurityEvent(event: AegisSecurityEvent): Long

    @Query("DELETE FROM aegis_security_events")
    suspend fun clearSecurityEvents()
}
