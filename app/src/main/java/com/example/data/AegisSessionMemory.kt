package com.example.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AegisSessionMemory(
    val sessionId: String = "aegis_session_${System.currentTimeMillis()}",
    val activeDomain: String = TaskDomain.CONVERSATION.domainId,
    val userIntent: String = "general_inquiry",
    val confidenceScore: Float = 0.98f,
    val lastResponse: String = "AEGIS shield active. How can I assist you today?",
    val escalationTriggered: Boolean = false,
    val healthEmergencyFlag: Boolean = false,
    val securityThreatFlag: Boolean = false,
    val taskStatus: String = "completed", // pending, in_progress, completed, failed
    val organizerLastAction: String = "none", // created, updated, deleted, scheduled
    val salesStage: String = "discovery", // discovery, proposal, objection, closing
    val artRequestPending: Boolean = false,
    val mathResult: String = "",
    val memoryLastUpdated: String = getCurrentFormattedTime(),
    // AEGIS String Configuration Parameters
    val agentName: String = "AEGIS",
    val modelName: String = "gemini-3.5-flash",
    val defaultLanguage: String = "en",
    val securityMode: String = "strict", // strict, standard, relaxed
    val artOutputFormat: String = "svg",
    val salesTone: String = "honest_persuasive",
    val healthDisclaimerText: String = "This is general information, not medical advice.",
    val escalationTarget: String = "human_support_agent",
    val organizerDefaultView: String = "daily",
    val apiEndpoint: String = "/v1/chat/completions"
)

fun getCurrentFormattedTime(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(Date())
}
