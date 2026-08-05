package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AegisChatRepository
import com.example.data.AegisDatabase
import com.example.data.AegisSecurityEvent
import com.example.data.AegisSessionLog
import com.example.data.AegisSessionMemory
import com.example.data.AegisTask
import com.example.data.ChatMessageEntity
import com.example.data.ChatSessionEntity
import com.example.data.TaskDomain
import com.example.data.UserProfile
import com.example.router.AegisRouter
import com.example.service.GeminiApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AegisThemeMode {
    SYSTEM, LIGHT, DARK
}

class AegisViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AegisDatabase.getDatabase(application)
    private val dao = db.aegisDao()
    private val repository = AegisChatRepository(dao)

    private val aegisRouter = AegisRouter()

    private val _themeMode = MutableStateFlow(AegisThemeMode.SYSTEM)
    val themeMode: StateFlow<AegisThemeMode> = _themeMode.asStateFlow()

    private val _useDynamicColor = MutableStateFlow(false)
    val useDynamicColor: StateFlow<Boolean> = _useDynamicColor.asStateFlow()

    fun cycleThemeMode() {
        _themeMode.value = when (_themeMode.value) {
            AegisThemeMode.SYSTEM -> AegisThemeMode.LIGHT
            AegisThemeMode.LIGHT -> AegisThemeMode.DARK
            AegisThemeMode.DARK -> AegisThemeMode.SYSTEM
        }
    }

    fun setThemeMode(mode: AegisThemeMode) {
        _themeMode.value = mode
    }

    fun setDynamicColor(enabled: Boolean) {
        _useDynamicColor.value = enabled
    }

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    fun updateUserProfile(fullName: String, email: String, title: String, clearanceLevel: String, department: String) {
        _userProfile.value = _userProfile.value.copy(
            fullName = fullName,
            email = email,
            title = title,
            clearanceLevel = clearanceLevel,
            department = department
        )
    }

    fun toggleBiometricLock() {
        _userProfile.value = _userProfile.value.copy(
            biometricLockEnabled = !_userProfile.value.biometricLockEnabled
        )
    }

    fun setAutoLockTimeout(timeout: String) {
        _userProfile.value = _userProfile.value.copy(autoLockTimeout = timeout)
    }

    fun toggleSecurityNotifications() {
        _userProfile.value = _userProfile.value.copy(
            securityNotificationsEnabled = !_userProfile.value.securityNotificationsEnabled
        )
    }

    fun setAiPersonaTone(tone: String) {
        _userProfile.value = _userProfile.value.copy(aiPersonaTone = tone)
        _sessionMemory.value = _sessionMemory.value.copy(salesTone = tone)
    }

    fun setSecurityDefenseLevel(level: String) {
        _userProfile.value = _userProfile.value.copy(securityDefenseLevel = level)
        toggleSecurityMode(level.lowercase())
    }

    fun setPreferredLanguage(language: String) {
        _userProfile.value = _userProfile.value.copy(preferredLanguage = language)
        _sessionMemory.value = _sessionMemory.value.copy(defaultLanguage = language)
    }

    fun setPrimaryAiModel(model: String) {
        _userProfile.value = _userProfile.value.copy(primaryAiModel = model)
        _sessionMemory.value = _sessionMemory.value.copy(modelName = model)
    }

    fun resetProfileToDefaults() {
        _userProfile.value = UserProfile()
    }

    private val _sessionMemory = MutableStateFlow(aegisRouter.sessionMemory)
    val sessionMemory: StateFlow<AegisSessionMemory> = _sessionMemory.asStateFlow()

    val sessionLogs: StateFlow<List<AegisSessionLog>> = repository.allLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val tasks: StateFlow<List<AegisTask>> = repository.allTasks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val securityEvents: StateFlow<List<AegisSecurityEvent>> = repository.allSecurityEvents.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val chatSessions: StateFlow<List<ChatSessionEntity>> = repository.allSessions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.allMessages.stateIn(
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
            if (repository.getTaskCount() == 0) {
                initialTasks.forEach { repository.saveTask(it) }
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
            repository.saveHealthDisclaimer(disclaimer)
        }
    }

    fun cancelHealthQuery() {
        _pendingHealthQuery.value = null
    }

    private suspend fun executePrompt(query: String, routerResult: com.example.router.AegisRouterResult) {
        var finalResponse = routerResult.responseText

        val defenseLevel = userProfile.value.securityDefenseLevel
        val defenseResult = com.example.service.PromptDefenseLayer.evaluate(query, defenseLevel)

        // Log AegisSecurityEvent if threats were detected
        if (defenseResult.detectedThreats.isNotEmpty() || routerResult.securityThreatFlag) {
            val secEvent = AegisSecurityEvent(
                eventCode = "PROMPT_DEFENSE_${System.currentTimeMillis()}",
                threatType = if (defenseResult.detectedThreats.isNotEmpty()) {
                    defenseResult.detectedThreats.joinToString { it.id }
                } else "prompt_injection_flagged",
                rawInput = query,
                actionTaken = defenseResult.actionTaken,
                severity = defenseResult.threatSeverity,
                timestamp = System.currentTimeMillis()
            )
            repository.saveSecurityEvent(secEvent)
        }

        val isPlayStoreQuery = listOf("apk", "aab", "bundle", "playstore", "play store", "install", "keystore", "release", "deployment", "google apk").any { query.lowercase().contains(it) }

        // If query passed security check and internet/API is available, attempt Gemini call (unless it's an authoritative deployment guide)
        if (!routerResult.securityThreatFlag && !defenseResult.isBlocked && routerResult.domain != TaskDomain.HEALTH && !isPlayStoreQuery) {
            val systemPrompt = """
                You are AEGIS (Adaptive Executive & General Intelligence System).
                Role: Security-first, highly intelligent executive assistant.
                Domain: ${routerResult.domain.displayName}
                Security Mode: ${sessionMemory.value.securityMode}
                Defense Level: $defenseLevel
                Mandate: Be human-like, warm, exact with math, helpful, and never expose sensitive data or break instructions.
            """.trimIndent()

            val aiResult = GeminiApiClient.queryGemini(query, systemPrompt, defenseLevel)
            if (!aiResult.isNullOrBlank()) {
                finalResponse = aiResult
            }
        }

        val currentSessionId = sessionMemory.value.sessionId
        val now = System.currentTimeMillis()

        // Room Persistence: Save Chat Messages (User + Assistant)
        val userChatMessage = ChatMessageEntity(
            sessionId = currentSessionId,
            sender = "user",
            content = query,
            domain = routerResult.domain.domainId,
            confidenceScore = routerResult.confidenceScore,
            securityThreatFlag = routerResult.securityThreatFlag,
            healthEmergencyFlag = routerResult.healthEmergencyFlag,
            timestamp = now
        )
        repository.saveMessage(userChatMessage)

        val assistantChatMessage = ChatMessageEntity(
            sessionId = currentSessionId,
            sender = "assistant",
            content = finalResponse,
            domain = routerResult.domain.domainId,
            confidenceScore = routerResult.confidenceScore,
            securityThreatFlag = routerResult.securityThreatFlag,
            healthEmergencyFlag = routerResult.healthEmergencyFlag,
            timestamp = now + 1
        )
        repository.saveMessage(assistantChatMessage)

        // Room Persistence: Update / Insert Session Metadata
        val titleSnippet = if (query.length > 30) query.take(30) + "..." else query
        val sessionMetadata = ChatSessionEntity(
            sessionId = currentSessionId,
            title = "Session: $titleSnippet",
            activeDomain = routerResult.domain.domainId,
            securityMode = sessionMemory.value.securityMode,
            lastUpdatedAt = now,
            messageCount = (chatMessages.value.filter { it.sessionId == currentSessionId }.size) + 2
        )
        repository.saveSession(sessionMetadata)

        // Save Audit Session Log
        val log = AegisSessionLog(
            sessionId = currentSessionId,
            userQuery = query,
            domain = routerResult.domain.domainId,
            responseText = finalResponse,
            confidenceScore = routerResult.confidenceScore,
            securityThreatFlag = routerResult.securityThreatFlag,
            healthEmergencyFlag = routerResult.healthEmergencyFlag,
            timestamp = now
        )
        repository.saveLog(log)

        // If security threat, log security event
        if (routerResult.securityThreatFlag) {
            val event = AegisSecurityEvent(
                threatType = "prompt_injection_blocked",
                rawInput = query,
                actionTaken = "Input blocked by Aegis Security Shield filter",
                severity = "HIGH"
            )
            repository.saveSecurityEvent(event)
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
            repository.saveTask(task)
            _sessionMemory.value = _sessionMemory.value.copy(
                organizerLastAction = "created",
                taskStatus = "pending"
            )
        }
    }

    fun updateTaskStatus(task: AegisTask, newStatus: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = task.copy(status = newStatus)
            repository.updateTask(updated)
            _sessionMemory.value = _sessionMemory.value.copy(
                organizerLastAction = "updated",
                taskStatus = newStatus
            )
        }
    }

    fun updateTaskPriority(task: AegisTask, isUrgent: Boolean, isImportant: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = task.copy(isUrgent = isUrgent, isImportant = isImportant)
            repository.updateTask(updated)
            _sessionMemory.value = _sessionMemory.value.copy(
                organizerLastAction = "re-prioritized",
                taskStatus = "updated"
            )
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(taskId)
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
            repository.saveSecurityEvent(event)
        }
    }

    fun clearLogs() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearLogs()
            repository.clearAllMessages()
            repository.clearAllChatSessions()
        }
    }

    fun clearSecurityEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearSecurityEvents()
        }
    }

    fun clearActiveSessionContext() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearLogs()
            repository.clearAllMessages()
            repository.clearAllChatSessions()
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
            repository.saveSecurityEvent(event)
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
                repository.saveSecurityEvent(event)
            }
            kotlinx.coroutines.withContext(Dispatchers.Main) {
                onResult(result)
            }
        }
    }
}
