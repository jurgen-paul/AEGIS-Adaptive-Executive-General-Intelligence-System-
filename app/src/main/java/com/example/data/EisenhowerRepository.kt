package com.example.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository pattern implementation abstracting Room SQLite database access
 * for Eisenhower Priority Matrix tasks.
 */
class EisenhowerRepository(private val dao: EisenhowerTaskDao) {

    /**
     * Continuous reactive Flow of all Eisenhower tasks from Room database.
     */
    val allTasks: Flow<List<EisenhowerTask>> = dao.getAllTasks()

    fun getTasksByQuadrant(quadrant: String): Flow<List<EisenhowerTask>> {
        return dao.getTasksByQuadrant(quadrant)
    }

    fun getTaskById(id: Long): Flow<EisenhowerTask?> {
        return dao.getTaskById(id)
    }

    suspend fun getTaskCount(): Int {
        return dao.getTaskCount()
    }

    suspend fun insertTask(task: EisenhowerTask): Long {
        return dao.insertTask(task)
    }

    suspend fun insertTasks(tasks: List<EisenhowerTask>) {
        dao.insertTasks(tasks)
    }

    suspend fun updateTask(task: EisenhowerTask) {
        dao.updateTask(task)
    }

    suspend fun deleteTask(task: EisenhowerTask) {
        dao.deleteTask(task)
    }

    suspend fun deleteTaskById(id: Long) {
        dao.deleteTaskById(id)
    }

    suspend fun deleteCompletedTasks() {
        dao.deleteCompletedTasks()
    }

    suspend fun clearAllTasks() {
        dao.clearAllTasks()
    }
}
