package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegisSessionMemory
import com.example.data.UserProfile
import com.example.ui.AegisThemeMode
import com.example.ui.components.AccountProfileSettingsComponent
import com.example.ui.theme.AegisCyanPrimary
import com.example.ui.theme.AegisOutline
import com.example.ui.theme.AegisShieldGreen
import com.example.ui.theme.AegisSurfaceDark
import com.example.ui.theme.AegisSurfaceVariant
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.AegisThreatRed

@Composable
fun SessionMemoryScreen(
    sessionMemory: AegisSessionMemory,
    userProfile: UserProfile = UserProfile(),
    themeMode: AegisThemeMode = AegisThemeMode.SYSTEM,
    useDynamicColor: Boolean = false,
    onUpdateProfile: (name: String, email: String, title: String, clearance: String, dept: String) -> Unit = { _, _, _, _, _ -> },
    onToggleBiometricLock: () -> Unit = {},
    onSetAutoLockTimeout: (String) -> Unit = {},
    onToggleSecurityNotifications: () -> Unit = {},
    onSetAiPersonaTone: (String) -> Unit = {},
    onSetSecurityDefenseLevel: (String) -> Unit = {},
    onSetPreferredLanguage: (String) -> Unit = {},
    onSetPrimaryAiModel: (String) -> Unit = {},
    onThemeModeSelected: (AegisThemeMode) -> Unit = {},
    onDynamicColorToggled: () -> Unit = {},
    onResetProfile: () -> Unit = {},
    onClearLogs: () -> Unit,
    onExportLogs: (() -> Unit)? = null,
    exportStatusText: String? = null,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Account Profile & Settings Component
        item {
            AccountProfileSettingsComponent(
                userProfile = userProfile,
                themeMode = themeMode,
                useDynamicColor = useDynamicColor,
                onUpdateProfile = onUpdateProfile,
                onToggleBiometricLock = onToggleBiometricLock,
                onSetAutoLockTimeout = onSetAutoLockTimeout,
                onToggleSecurityNotifications = onToggleSecurityNotifications,
                onSetAiPersonaTone = onSetAiPersonaTone,
                onSetSecurityDefenseLevel = onSetSecurityDefenseLevel,
                onSetPreferredLanguage = onSetPreferredLanguage,
                onSetPrimaryAiModel = onSetPrimaryAiModel,
                onThemeModeSelected = onThemeModeSelected,
                onDynamicColorToggled = onDynamicColorToggled,
                onResetProfile = onResetProfile,
                onClearLogs = onClearLogs,
                onExportLogs = onExportLogs,
                exportStatusText = exportStatusText
            )
        }

        // Active Session State Inspector Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AegisCyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = AegisSurfaceDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = null,
                                tint = AegisCyanPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LIVE SESSION MEMORY STATE",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AegisTextPrimary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AegisCyanPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "LIVE STATE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AegisCyanPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    MemoryParamRow("session_id", sessionMemory.sessionId)
                    MemoryParamRow("active_domain", sessionMemory.activeDomain)
                    MemoryParamRow("user_intent", sessionMemory.userIntent)
                    MemoryParamRow("confidence_score", "${sessionMemory.confidenceScore}")
                    MemoryParamRow("escalation_triggered", "${sessionMemory.escalationTriggered}")
                    MemoryParamRow("health_emergency_flag", "${sessionMemory.healthEmergencyFlag}")
                    MemoryParamRow("security_threat_flag", "${sessionMemory.securityThreatFlag}")
                    MemoryParamRow("task_status", sessionMemory.taskStatus)
                    MemoryParamRow("organizer_last_action", sessionMemory.organizerLastAction)
                    MemoryParamRow("sales_stage", sessionMemory.salesStage)
                    MemoryParamRow("art_request_pending", "${sessionMemory.artRequestPending}")
                    MemoryParamRow("math_result", if (sessionMemory.mathResult.isBlank()) "none" else sessionMemory.mathResult)
                    MemoryParamRow("memory_last_updated", sessionMemory.memoryLastUpdated)
                }
            }
        }

        // Configuration String Parameters Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AegisOutline, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = AegisSurfaceDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = AegisShieldGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AEGIS SYSTEM STRING PARAMETERS",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AegisTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    MemoryParamRow("agent_name", sessionMemory.agentName)
                    MemoryParamRow("model_name", sessionMemory.modelName)
                    MemoryParamRow("default_language", sessionMemory.defaultLanguage)
                    MemoryParamRow("security_mode", sessionMemory.securityMode)
                    MemoryParamRow("art_output_format", sessionMemory.artOutputFormat)
                    MemoryParamRow("sales_tone", sessionMemory.salesTone)
                    MemoryParamRow("health_disclaimer_text", sessionMemory.healthDisclaimerText)
                    MemoryParamRow("escalation_target", sessionMemory.escalationTarget)
                    MemoryParamRow("organizer_default_view", sessionMemory.organizerDefaultView)
                    MemoryParamRow("api_endpoint", sessionMemory.apiEndpoint)
                }
            }
        }
    }
}

@Composable
fun MemoryParamRow(key: String, value: String) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = key,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = AegisCyanPrimary
            )
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AegisTextPrimary
            )
        }
        HorizontalDivider(color = AegisOutline.copy(alpha = 0.3f), thickness = 0.5.dp)
    }
}
