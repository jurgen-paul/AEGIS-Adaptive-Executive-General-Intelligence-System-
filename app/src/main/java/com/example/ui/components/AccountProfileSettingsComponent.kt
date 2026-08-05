package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfile
import com.example.ui.AegisThemeMode

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AccountProfileSettingsComponent(
    userProfile: UserProfile,
    themeMode: AegisThemeMode,
    useDynamicColor: Boolean,
    onUpdateProfile: (name: String, email: String, title: String, clearance: String, dept: String) -> Unit,
    onToggleBiometricLock: () -> Unit,
    onSetAutoLockTimeout: (String) -> Unit,
    onToggleSecurityNotifications: () -> Unit,
    onSetAiPersonaTone: (String) -> Unit,
    onSetSecurityDefenseLevel: (String) -> Unit,
    onSetPreferredLanguage: (String) -> Unit,
    onSetPrimaryAiModel: (String) -> Unit,
    onThemeModeSelected: (AegisThemeMode) -> Unit,
    onDynamicColorToggled: () -> Unit,
    onResetProfile: () -> Unit,
    onClearLogs: () -> Unit,
    onExportLogs: (() -> Unit)? = null,
    exportStatusText: String? = null,
    modifier: Modifier = Modifier
) {
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showClearLogsConfirmDialog by remember { mutableStateOf(false) }

    val userInitials = userProfile.fullName
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull() }
        .joinToString("")
        .uppercase()
        .ifEmpty { "AV" }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Account Profile Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("account_profile_header_card")
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        // User Avatar Initial Badge
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .border(
                                    2.dp,
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userInitials,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userProfile.fullName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = userProfile.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = userProfile.email,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { showEditProfileDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("edit_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(10.dp))

                // Clearance Level & Department Badges
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = userProfile.clearanceLevel,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = userProfile.department,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }

        // 2. Security & Access Control Settings
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("security_settings_card")
                .border(
                    1.dp,
                    Color(0xFF66BB6A).copy(alpha = 0.4f),
                    RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFF66BB6A)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SECURITY & ACCESS CONTROL",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 0.5.sp
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                // Biometric Lock Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Biometric Vault Authentication",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Gate sensitive storage behind fingerprint/face unlock",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = userProfile.biometricLockEnabled,
                        onCheckedChange = { onToggleBiometricLock() },
                        modifier = Modifier.testTag("biometric_lock_switch")
                    )
                }

                // Auto-Lock Timeout Selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Auto-Lock Timeout: ${userProfile.autoLockTimeout}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Immediately", "1 Minute", "5 Minutes", "15 Minutes", "Never").forEach { timeoutOption ->
                            FilterChip(
                                selected = userProfile.autoLockTimeout == timeoutOption,
                                onClick = { onSetAutoLockTimeout(timeoutOption) },
                                label = { Text(timeoutOption, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                // Security Push Notifications Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Threat & Anomaly Alerts",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Real-time alert logs for prompt injection attempts",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = userProfile.securityNotificationsEnabled,
                        onCheckedChange = { onToggleSecurityNotifications() },
                        modifier = Modifier.testTag("security_alerts_switch")
                    )
                }
            }
        }

        // 3. AI Persona & System Intelligence Settings
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ai_settings_card")
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI PERSONA & ROUTING ENGINE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 0.5.sp
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                // Defense Mode Level
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Prompt Defense Layer Level:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Strict", "Standard", "Relaxed").forEach { level ->
                            FilterChip(
                                selected = userProfile.securityDefenseLevel.lowercase() == level.lowercase(),
                                onClick = { onSetSecurityDefenseLevel(level) },
                                label = { Text(level, fontSize = 11.sp) },
                                leadingIcon = if (userProfile.securityDefenseLevel.lowercase() == level.lowercase()) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                } else null,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // AI Persona & Tone Selection
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "AEGIS Conversation Persona:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Executive & Concise", "Honest & Persuasive", "Analytical & Detailed", "Friendly & Empathetic").forEach { toneOption ->
                            FilterChip(
                                selected = userProfile.aiPersonaTone == toneOption,
                                onClick = { onSetAiPersonaTone(toneOption) },
                                label = { Text(toneOption, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                // Primary AI Model Selection
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Primary Gemini Reasoning Engine:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Gemini 1.5 Pro", "Gemini 1.5 Flash", "Gemini Ultra").forEach { modelOption ->
                            FilterChip(
                                selected = userProfile.primaryAiModel == modelOption,
                                onClick = { onSetPrimaryAiModel(modelOption) },
                                label = { Text(modelOption, fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Preferred Language
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "System Language: ${userProfile.preferredLanguage}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("English (US)", "Spanish", "French", "German", "Japanese").forEach { lang ->
                            FilterChip(
                                selected = userProfile.preferredLanguage == lang,
                                onClick = { onSetPreferredLanguage(lang) },
                                label = { Text(lang, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }

        // 4. Appearance & Theme Settings Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("theme_settings_card")
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "THEME & ACCESSIBILITY",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 0.5.sp
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = themeMode == AegisThemeMode.LIGHT,
                        onClick = { onThemeModeSelected(AegisThemeMode.LIGHT) },
                        label = { Text("Light") },
                        leadingIcon = {
                            Icon(Icons.Default.LightMode, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = themeMode == AegisThemeMode.DARK,
                        onClick = { onThemeModeSelected(AegisThemeMode.DARK) },
                        label = { Text("Dark") },
                        leadingIcon = {
                            Icon(Icons.Default.DarkMode, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = themeMode == AegisThemeMode.SYSTEM,
                        onClick = { onThemeModeSelected(AegisThemeMode.SYSTEM) },
                        label = { Text("System") },
                        leadingIcon = {
                            Icon(Icons.Default.SettingsSuggest, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.fillMaxWidth(0.8f)) {
                        Text(
                            text = "Material You Dynamic Colors",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Adapt app theme to system wallpaper palette (Android 12+)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = useDynamicColor,
                        onCheckedChange = { onDynamicColorToggled() },
                        modifier = Modifier.testTag("dynamic_color_switch_profile")
                    )
                }
            }
        }

        // 5. Encrypted Export & Data Privacy Control
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("privacy_control_card")
                .border(
                    1.dp,
                    Color(0xFF66BB6A).copy(alpha = 0.5f),
                    RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = Color(0xFF66BB6A)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ENCRYPTED CHAT EXPORT & PRIVACY",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 0.5.sp
                    )
                }

                Text(
                    text = "Export local session log history into an AES-256 encrypted .enc file with automated PII scrubbing (Emails, Phones, API Keys, Passwords).",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (exportStatusText != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF66BB6A).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = exportStatusText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Button(
                    onClick = { onExportLogs?.invoke() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("export_chat_history_button")
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export Encrypted Chat (.enc)", color = Color.White, fontWeight = FontWeight.Bold)
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showClearLogsConfirmDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("clear_logs_button")
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear Logs", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { showResetConfirmDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("reset_defaults_button")
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset Profile", fontSize = 11.sp)
                    }
                }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        EditProfileDialog(
            currentProfile = userProfile,
            onDismiss = { showEditProfileDialog = false },
            onConfirm = { name, email, title, clearance, dept ->
                onUpdateProfile(name, email, title, clearance, dept)
                showEditProfileDialog = false
            }
        )
    }

    // Reset Defaults Confirmation Dialog
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Reset Profile to Defaults?") },
            text = { Text("This will restore the default user profile, clearance level, and AI engine preferences.") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetProfile()
                        showResetConfirmDialog = false
                    }
                ) {
                    Text("Reset Defaults")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Clear Logs Confirmation Dialog
    if (showClearLogsConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearLogsConfirmDialog = false },
            title = { Text("Clear All Session Logs?") },
            text = { Text("This will permanently remove all stored chat and security logs from local memory.") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearLogs()
                        showClearLogsConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Clear All Logs")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearLogsConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun EditProfileDialog(
    currentProfile: UserProfile,
    onDismiss: () -> Unit,
    onConfirm: (name: String, email: String, title: String, clearance: String, dept: String) -> Unit
) {
    var name by remember { mutableStateOf(currentProfile.fullName) }
    var email by remember { mutableStateOf(currentProfile.email) }
    var title by remember { mutableStateOf(currentProfile.title) }
    var clearance by remember { mutableStateOf(currentProfile.clearanceLevel) }
    var department by remember { mutableStateOf(currentProfile.department) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Account Profile",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_name_input")
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_email_input")
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Role / Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = clearance,
                    onValueChange = { clearance = it },
                    label = { Text("Clearance Level") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("Department / Division") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank()) {
                        onConfirm(name.trim(), email.trim(), title.trim(), clearance.trim(), department.trim())
                    }
                },
                enabled = name.isNotBlank() && email.isNotBlank(),
                modifier = Modifier.testTag("save_profile_button")
            ) {
                Text("Save Profile")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
