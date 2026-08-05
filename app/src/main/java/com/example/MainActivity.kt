package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AegisViewModel
import com.example.ui.components.AegisLockScreen
import com.example.ui.components.AegisTopAppBar
import com.example.ui.components.SecurityAlertBanner
import com.example.ui.screens.CommandHubScreen
import com.example.ui.screens.ExecutiveOrganizerScreen
import com.example.ui.screens.SecurityShieldScreen
import com.example.ui.screens.SessionMemoryScreen
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekCardBg
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekPrimaryContainer
import com.example.ui.theme.SleekTextSecondary
import com.example.ui.theme.AegisTheme
import kotlinx.coroutines.delay

class MainActivity : FragmentActivity() {

    private val viewModel: AegisViewModel by viewModels()
    private var lastActivityUserInteraction by mutableLongStateOf(System.currentTimeMillis())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val useDynamicColor by viewModel.useDynamicColor.collectAsStateWithLifecycle()
            val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()

            val isDark = when (themeMode) {
                com.example.ui.AegisThemeMode.SYSTEM -> isSystemDark
                com.example.ui.AegisThemeMode.LIGHT -> false
                com.example.ui.AegisThemeMode.DARK -> true
            }

            AegisTheme(
                darkTheme = isDark,
                dynamicColor = useDynamicColor
            ) {
                AegisMainApp(
                    viewModel = viewModel,
                    lastInteractionTimestamp = lastActivityUserInteraction,
                    onUserInteracted = { lastActivityUserInteraction = System.currentTimeMillis() }
                )
            }
        }
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        lastActivityUserInteraction = System.currentTimeMillis()
    }
}

@Composable
fun AegisMainApp(
    viewModel: AegisViewModel,
    lastInteractionTimestamp: Long = System.currentTimeMillis(),
    onUserInteracted: () -> Unit = {}
) {
    var isAppUnlocked by remember { mutableStateOf(false) }

    // 5-minute idle timeout threshold (300,000 milliseconds)
    val IDLE_TIMEOUT_MS = 5 * 60 * 1000L

    // Coroutine effect that checks for idle timeout
    LaunchedEffect(isAppUnlocked, lastInteractionTimestamp) {
        if (isAppUnlocked) {
            while (true) {
                delay(3000L) // Periodically inspect idle status
                val idleDuration = System.currentTimeMillis() - lastInteractionTimestamp
                if (idleDuration >= IDLE_TIMEOUT_MS) {
                    viewModel.clearActiveSessionContext()
                    isAppUnlocked = false
                    break
                }
            }
        }
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    var exportStatusMessage by remember { mutableStateOf<String?>(null) }

    val handleExportChatHistory = {
        viewModel.exportSecureChatHistory(context) { result ->
            if (result.success) {
                exportStatusMessage = "✅ Export Success!\nFile: ${result.filePath}\nRecords: ${result.totalLogsExported} messages\nPII Scrubbed: ${result.totalScrubbedMatches} matches redacted\nEncryption: AES-256-CBC (${result.encryptedFileSizeBytes} bytes)"
            } else {
                exportStatusMessage = "❌ Export Failed: ${result.errorMessage}"
            }
        }
    }

    if (!isAppUnlocked) {
        AegisLockScreen(
            onUnlocked = {
                onUserInteracted()
                isAppUnlocked = true
            }
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent()
                        onUserInteracted()
                    }
                }
            }
    ) {
        val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
        val useDynamicColor by viewModel.useDynamicColor.collectAsStateWithLifecycle()
        val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
        val sessionMemory by viewModel.sessionMemory.collectAsStateWithLifecycle()
        val sessionLogs by viewModel.sessionLogs.collectAsStateWithLifecycle()
        val tasks by viewModel.tasks.collectAsStateWithLifecycle()
        val securityEvents by viewModel.securityEvents.collectAsStateWithLifecycle()
        val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
        val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
        val pendingHealthQuery by viewModel.pendingHealthQuery.collectAsStateWithLifecycle()

        if (pendingHealthQuery != null) {
            AlertDialog(
                onDismissRequest = { viewModel.cancelHealthQuery() },
                title = { 
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.HealthAndSafety, contentDescription = null, tint = com.example.ui.theme.SleekWarningOrange)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Health & Medical Disclaimer", fontWeight = FontWeight.Bold, fontSize = 18.sp) 
                    }
                },
                text = {
                    Text(
                        "AEGIS outputs are for informational and general wellness purposes only and do NOT constitute professional medical advice, diagnosis, or treatment. Always consult a licensed healthcare provider for medical concerns. In emergencies, call 911 immediately.",
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = { viewModel.confirmHealthQuery(pendingHealthQuery!!) }
                    ) {
                        Text("I Understand & Accept", fontWeight = FontWeight.Bold, color = com.example.ui.theme.SleekPrimary)
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(
                        onClick = { viewModel.cancelHealthQuery() }
                    ) {
                        Text("Cancel", color = com.example.ui.theme.SleekTextSecondary)
                    }
                },
                containerColor = com.example.ui.theme.SleekCardBg,
                titleContentColor = com.example.ui.theme.SleekTextPrimary,
                textContentColor = com.example.ui.theme.SleekTextSecondary
            )
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Column {
                    AegisTopAppBar(
                        sessionMemory = sessionMemory,
                        threatCount = securityEvents.size,
                        themeMode = themeMode,
                        onShieldClick = { viewModel.switchTab(2) },
                        onMemoryClick = { viewModel.switchTab(3) },
                        onLockClick = { isAppUnlocked = false },
                        onToggleThemeClick = { viewModel.cycleThemeMode() }
                    )

                    SecurityAlertBanner(
                        isSecurityThreat = sessionMemory.securityThreatFlag,
                        isHealthEmergency = sessionMemory.healthEmergencyFlag,
                        onInspectSecurity = { viewModel.switchTab(2) },
                        onEmergencyDismiss = {
                            viewModel.processUserPrompt("dismiss emergency banner")
                        }
                    )
                }
            },
            bottomBar = {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(androidx.compose.material3.MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    )
                    NavigationBar(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                        contentColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                        tonalElevation = 0.dp,
                        modifier = Modifier.testTag("aegis_bottom_navigation")
                    ) {
                        NavigationBarItem(
                            selected = activeTab == 0,
                            onClick = { viewModel.switchTab(0) },
                            icon = { Text("🏠", fontSize = 18.sp) },
                            label = { Text("Home", fontSize = 10.sp, fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                selectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("aegis_tab_command_hub")
                        )

                        NavigationBarItem(
                            selected = activeTab == 1,
                            onClick = { viewModel.switchTab(1) },
                            icon = { Text("📅", fontSize = 18.sp) },
                            label = { Text("Organizer", fontSize = 10.sp, fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                selectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("aegis_tab_organizer")
                        )

                        NavigationBarItem(
                            selected = activeTab == 2,
                            onClick = { viewModel.switchTab(2) },
                            icon = { Text("🛡️", fontSize = 18.sp) },
                            label = { Text("Vault", fontSize = 10.sp, fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                selectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("aegis_tab_security")
                        )

                        NavigationBarItem(
                            selected = activeTab == 3,
                            onClick = { viewModel.switchTab(3) },
                            icon = { Text("⚙️", fontSize = 18.sp) },
                            label = { Text("Config", fontSize = 10.sp, fontWeight = if (activeTab == 3) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                selectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("aegis_tab_memory")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (activeTab) {
                    0 -> CommandHubScreen(
                        sessionMemory = sessionMemory,
                        sessionLogs = sessionLogs,
                        tasks = tasks,
                        isGenerating = isGenerating,
                        onSendPrompt = { viewModel.processUserPrompt(it) },
                        onAddTask = { title, desc, urgent, important, cat ->
                            viewModel.addTask(title, desc, urgent, important, cat)
                        },
                        onUpdateTaskStatus = { task, status ->
                            viewModel.updateTaskStatus(task, status)
                        },
                        onUpdateTaskPriority = { task, urgent, important ->
                            viewModel.updateTaskPriority(task, urgent, important)
                        },
                        onDeleteTask = { id ->
                            viewModel.deleteTask(id)
                        },
                        onExportLogs = handleExportChatHistory
                    )
                    1 -> ExecutiveOrganizerScreen(
                        tasks = tasks,
                        securityEvents = securityEvents,
                        onAddTask = { title, desc, urgent, important, cat ->
                            viewModel.addTask(title, desc, urgent, important, cat)
                        },
                        onUpdateStatus = { task, status ->
                            viewModel.updateTaskStatus(task, status)
                        },
                        onDeleteTask = { id ->
                            viewModel.deleteTask(id)
                        },
                        onUpdatePriority = { task, urgent, important ->
                            viewModel.updateTaskPriority(task, urgent, important)
                        }
                    )
                    2 -> SecurityShieldScreen(
                        sessionMemory = sessionMemory,
                        securityEvents = securityEvents,
                        onToggleSecurityMode = { viewModel.toggleSecurityMode(it) },
                        onClearSecurityEvents = { viewModel.clearSecurityEvents() },
                        onLockApp = {
                            viewModel.clearActiveSessionContext()
                            isAppUnlocked = false
                        }
                    )
                    3 -> SessionMemoryScreen(
                        sessionMemory = sessionMemory,
                        userProfile = userProfile,
                        themeMode = themeMode,
                        useDynamicColor = useDynamicColor,
                        onUpdateProfile = { name, email, title, clearance, dept ->
                            viewModel.updateUserProfile(name, email, title, clearance, dept)
                        },
                        onToggleBiometricLock = { viewModel.toggleBiometricLock() },
                        onSetAutoLockTimeout = { viewModel.setAutoLockTimeout(it) },
                        onToggleSecurityNotifications = { viewModel.toggleSecurityNotifications() },
                        onSetAiPersonaTone = { viewModel.setAiPersonaTone(it) },
                        onSetSecurityDefenseLevel = { viewModel.setSecurityDefenseLevel(it) },
                        onSetPreferredLanguage = { viewModel.setPreferredLanguage(it) },
                        onSetPrimaryAiModel = { viewModel.setPrimaryAiModel(it) },
                        onThemeModeSelected = { viewModel.setThemeMode(it) },
                        onDynamicColorToggled = { viewModel.setDynamicColor(!useDynamicColor) },
                        onResetProfile = { viewModel.resetProfileToDefaults() },
                        onClearLogs = { viewModel.clearLogs() },
                        onExportLogs = handleExportChatHistory,
                        exportStatusText = exportStatusMessage
                    )
                }
            }
        }
    }
}
