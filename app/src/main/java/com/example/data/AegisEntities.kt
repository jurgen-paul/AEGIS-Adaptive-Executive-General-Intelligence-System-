package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "aegis_session_logs")
data class AegisSessionLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String,
    val userQuery: String,
    val domain: String,
    val responseText: String,
    val confidenceScore: Float,
    val securityThreatFlag: Boolean = false,
    val healthEmergencyFlag: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "aegis_tasks")
data class AegisTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "general", // work, security, personal, health
    val isUrgent: Boolean = false,
    val isImportant: Boolean = true,
    val status: String = "pending", // pending, in_progress, completed
    val dueDate: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "aegis_security_events")
data class AegisSecurityEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val eventCode: String = "aegis_security_event_${System.currentTimeMillis()}",
    val threatType: String, // prompt_injection, sensitive_data_leak, unauthorized_bypass
    val rawInput: String,
    val actionTaken: String,
    val severity: String = "HIGH", // LOW, MEDIUM, HIGH, CRITICAL
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "health_history_disclaimers")
data class HealthHistoryDisclaimer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "sales_notes")
data class SalesNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "aegis_chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey val sessionId: String,
    val title: String,
    val activeDomain: String = "conversation",
    val securityMode: String = "strict",
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdatedAt: Long = System.currentTimeMillis(),
    val messageCount: Int = 0,
    val isArchived: Boolean = false
)

@Entity(
    tableName = "aegis_chat_messages",
    indices = [androidx.room.Index("sessionId")]
)
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String,
    val sender: String, // "user" or "assistant"
    val content: String,
    val domain: String = "conversation",
    val confidenceScore: Float = 0.95f,
    val securityThreatFlag: Boolean = false,
    val healthEmergencyFlag: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
