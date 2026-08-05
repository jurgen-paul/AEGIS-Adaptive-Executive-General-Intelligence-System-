package com.example.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository enforcing clean architecture and abstracting data access for
 * chat sessions, message history, tasks, and security events from Room database.
 */
class AegisChatRepository(private val dao: AegisDao) {

    val allSessions: Flow<List<ChatSessionEntity>> = dao.getAllChatSessions()
    val allMessages: Flow<List<ChatMessageEntity>> = dao.getAllMessages()
    val allLogs: Flow<List<AegisSessionLog>> = dao.getAllLogs()
    val allTasks: Flow<List<AegisTask>> = dao.getAllTasks()
    val allSecurityEvents: Flow<List<AegisSecurityEvent>> = dao.getAllSecurityEvents()

    fun getMessagesForSession(sessionId: String): Flow<List<ChatMessageEntity>> {
        return dao.getMessagesForSession(sessionId)
    }

    fun getSessionById(sessionId: String): Flow<ChatSessionEntity?> {
        return dao.getChatSessionById(sessionId)
    }

    suspend fun saveSession(session: ChatSessionEntity) {
        dao.insertChatSession(session)
    }

    suspend fun updateSession(session: ChatSessionEntity) {
        dao.updateChatSession(session)
    }

    suspend fun deleteSession(sessionId: String) {
        dao.deleteMessagesForSession(sessionId)
        dao.deleteChatSession(sessionId)
    }

    suspend fun saveMessage(message: ChatMessageEntity): Long {
        return dao.insertChatMessage(message)
    }

    suspend fun saveLog(log: AegisSessionLog): Long {
        return dao.insertLog(log)
    }

    suspend fun saveTask(task: AegisTask): Long {
        return dao.insertTask(task)
    }

    suspend fun updateTask(task: AegisTask) {
        dao.updateTask(task)
    }

    suspend fun deleteTask(taskId: Long) {
        dao.deleteTask(taskId)
    }

    suspend fun getTaskCount(): Int {
        return dao.getTaskCount()
    }

    suspend fun saveSecurityEvent(event: AegisSecurityEvent): Long {
        return dao.insertSecurityEvent(event)
    }

    suspend fun saveHealthDisclaimer(disclaimer: HealthHistoryDisclaimer): Long {
        return dao.insertHealthHistoryDisclaimer(disclaimer)
    }

    suspend fun clearLogs() {
        dao.clearLogs()
    }

    suspend fun clearSecurityEvents() {
        dao.clearSecurityEvents()
    }

    suspend fun clearAllMessages() {
        dao.clearAllMessages()
    }

    suspend fun clearAllChatSessions() {
        dao.clearAllChatSessions()
    }
}
