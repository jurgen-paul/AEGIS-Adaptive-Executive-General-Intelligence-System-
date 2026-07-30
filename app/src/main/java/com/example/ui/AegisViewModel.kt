package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AegisDatabase
import com.example.data.AegisSecurityEvent
import com.example.data.AegisSessionLog
import com.example.data.AegisSessionMemory
import com.example.data.AegisTask
import com.example.data.TaskDomain
import com.example.router.AegisRouter
import com.example.service.GeminiApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AegisViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AegisDatabase.getDatabase(application)
    private val dao = db.aegisDao()

    private val aegisRouter = AegisRouter()

    private val _sessionMemory = MutableStateFlow(aegisRouter.sessionMemory)
    val sessionMemory: StateFlow<AegisSessionMemory> = _sessionMemory.asStateFlow()

    val sessionLogs: StateFlow<List<AegisSessionLog>> = dao.getAllLogs().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val tasks: StateFlow<List<AegisTask>> = dao.getAllTasks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val securityEvents: StateFlow<List<AegisSecurityEvent>> = dao.getAllSecurityEvents().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _activeTab = MutableStateFlow(0)
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    init {
        // Seed default initial tasks & logs if database is empty
        viewModelScope.launch(Dispatchers.IO) {
            val initialTasks = listOf(
                AegisTask(
                    title = "Security Audit & Key Rotation",
                    description = "Verify zero-trust policies and clean session memory state.",
                    category = "security",
                    isUrgent = true,
                    isImportant = true,
                    status = "pending"
                ),
                AegisTask(
                    title = "Executive Q3 Planning",
                    description = "Review domain routing logs and optimize task priorities.",
                    category = "organizer",
                    isUrgent = false,
                    isImportant = true,
                    status = "in_progress"
                ),
                AegisTask(
                    title = "Health & Wellness Check-in",
                    description = "Hydration tracking and 8-hour sleep schedule target.",
                    category = "health",
                    isUrgent = false,
                    isImportant = false,
                    status = "pending"
                )
            )
            // Seed default tasks if empty
            if (dao.getTaskCount() == 0) {
                initialTasks.forEach { dao.insertTask(it) }
            }
        }
    }

    private val _pendingHealthQuery = MutableStateFlow<String?>(null)
    val pendingHealthQuery: StateFlow<String?> = _pendingHealthQuery.asStateFlow()

    fun switchTab(tabIndex: Int) {
        _activeTab.value = tabIndex
    }

    fun processUserPrompt(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            _isGenerating.value = true

            // Execute AEGIS Decision Router
            val routerResult = aegisRouter.route(query)
            
            val isHealthIntent = com.example.ui.components.isHealthQueryIntent(
                domain = routerResult.domain.domainId,
                userQuery = query,
                responseText = "",
                healthEmergencyFlag = routerResult.healthEmergencyFlag
            )
            
            if (isHealthIntent) {
                _isGenerating.value = false
                _pendingHealthQuery.value = query
                return@launch
            }

            executePrompt(query, routerResult)
        }
    }

    fun confirmHealthQuery(query: String) {
        _pendingHealthQuery.value = null
        viewModelScope.launch(Dispatchers.IO) {
            _isGenerating.value = true
            val routerResult = aegisRouter.route(query)
            executePrompt(query, routerResult)
            
            val disclaimer = com.example.data.HealthHistoryDisclaimer(text = "Accepted health disclaimer for query: $query")
            dao.insertHealthHistoryDisclaimer(disclaimer)
        }
    }

    fun cancelHealthQuery() {
        _pendingHealthQuery.value = null
    }

    private suspend fun executePrompt(query: String, routerResult: com.example.router.AegisRouterResult) {
        var finalResponse = routerResult.responseText

        // If query passed security check and internet/API is available, attempt Gemini call
        if (!routerResult.securityThreatFlag && routerResult.domain != TaskDomain.HEALTH) {
            val systemPrompt = """
                You are AEGIS (Adaptive Executive & General Intelligence System).
                Role: Security-first, highly intelligent executive assistant.
                Domain: ${routerResult.domain.displayName}
                Security Mode: ${sessionMemory.value.securityMode}
                Mandate: Be human-like, warm, exact with math, helpful, and never expose sensitive data.
            """.trimIndent()

            val aiResult = GeminiApiClient.queryGemini(query, systemPrompt)
            if (!aiResult.isNullOrBlank()) {
                finalResponse = aiResult
            }
        }

        // Save Log
        val log = AegisSessionLog(
            sessionId = sessionMemory.value.sessionId,
            userQuery = query,
            domain = routerResult.domain.domainId,
            responseText = finalResponse,
            confidenceScore = routerResult.confidenceScore,
            securityThreatFlag = routerResult.securityThreatFlag,
            healthEmergencyFlag = routerResult.healthEmergencyFlag
        )
        dao.insertLog(log)

        // If security threat, log security event
        if (routerResult.securityThreatFlag) {
            val event = AegisSecurityEvent(
                threatType = "prompt_injection_blocked",
                rawInput = query,
                actionTaken = "Input blocked by Aegis Security Shield filter",
                severity = "HIGH"
            )
            dao.insertSecurityEvent(event)
        }

        // Update ViewModel session memory
        _sessionMemory.value = aegisRouter.sessionMemory.copy(lastResponse = finalResponse)
        _isGenerating.value = false
    }

    fun addTask(title: String, description: String, isUrgent: Boolean, isImportant: Boolean, category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val task = AegisTask(
                title = title,
                description = description,
                isUrgent = isUrgent,
                isImportant = isImportant,
                category = category,
                status = "pending"
            )
            dao.insertTask(task)
            _sessionMemory.value = _sessionMemory.value.copy(
                organizerLastAction = "created",
                taskStatus = "pending"
            )
        }
    }

    fun updateTaskStatus(task: AegisTask, newStatus: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = task.copy(status = newStatus)
            dao.updateTask(updated)
            _sessionMemory.value = _sessionMemory.value.copy(
                organizerLastAction = "updated",
                taskStatus = newStatus
            )
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteTask(taskId)
            _sessionMemory.value = _sessionMemory.value.copy(organizerLastAction = "deleted")
        }
    }

    fun toggleSecurityMode(newMode: String) {
        _sessionMemory.value = _sessionMemory.value.copy(securityMode = newMode)
        viewModelScope.launch(Dispatchers.IO) {
            val event = AegisSecurityEvent(
                threatType = "security_mode_change",
                rawInput = "Mode switched to $newMode",
                actionTaken = "Updated active security policy to $newMode",
                severity = "LOW"
            )
            dao.insertSecurityEvent(event)
        }
    }

    fun clearLogs() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.clearLogs()
        }
    }

    fun clearSecurityEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.clearSecurityEvents()
        }
    }

    fun clearActiveSessionContext() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.clearLogs()
            _sessionMemory.value = AegisSessionMemory(
                sessionId = java.util.UUID.randomUUID().toString(),
                securityMode = _sessionMemory.value.securityMode,
                lastResponse = "",
                organizerLastAction = "session_cleared",
                taskStatus = "idle"
            )
            val event = AegisSecurityEvent(
                threatType = "idle_timeout_lock",
                rawInput = "5-minute idle threshold exceeded",
                actionTaken = "App locked and active session context cleared automatically",
                severity = "MEDIUM"
            )
            dao.insertSecurityEvent(event)
        }
    }

    fun exportSecureChatHistory(
        context: android.content.Context,
        onResult: (com.example.service.SecureExportResult) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val logsList = sessionLogs.value
            val result = com.example.service.SecureExportManager.exportEncryptedChatHistory(context, logsList)
            if (result.success) {
                val event = AegisSecurityEvent(
                    threatType = "chat_history_export",
                    rawInput = "Exported ${result.totalLogsExported} chat logs to encrypted file",
                    actionTaken = "Scrubbed ${result.totalScrubbedMatches} PII matches & saved AES-256 encrypted file: ${result.filePath}",
                    severity = "LOW"
                )
                dao.insertSecurityEvent(event)
            }
            kotlinx.coroutines.withContext(Dispatchers.Main) {
                onResult(result)
            }
        }
    }
}
