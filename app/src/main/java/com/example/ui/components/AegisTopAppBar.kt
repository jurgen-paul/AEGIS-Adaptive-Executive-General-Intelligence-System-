package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegisSessionMemory
import com.example.ui.AegisThemeMode
import com.example.ui.theme.SleekThreatRed

@Composable
fun AegisTopAppBar(
    sessionMemory: AegisSessionMemory,
    threatCount: Int = 0,
    themeMode: AegisThemeMode = AegisThemeMode.SYSTEM,
    onShieldClick: () -> Unit,
    onMemoryClick: () -> Unit,
    onLockClick: () -> Unit = {},
    onToggleThemeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("aegis_top_app_bar"),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 0.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Title & Subtitle
                Column {
                    Text(
                        text = "AEGIS",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "SYSTEM ACTIVE • ${sessionMemory.securityMode.uppercase()} MODE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.1.sp
                    )
                }

                // Top Right Actions
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Theme Mode Toggle Button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onToggleThemeClick,
                            modifier = Modifier.testTag("aegis_theme_toggle_button")
                        ) {
                            Icon(
                                imageVector = when (themeMode) {
                                    AegisThemeMode.LIGHT -> Icons.Default.LightMode
                                    AegisThemeMode.DARK -> Icons.Default.DarkMode
                                    AegisThemeMode.SYSTEM -> Icons.Default.SettingsSuggest
                                },
                                contentDescription = "Toggle Light/Dark Theme",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Security Shield Button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onShieldClick,
                            modifier = Modifier.testTag("aegis_shield_status_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (threatCount > 0) {
                                        Badge(
                                            containerColor = SleekThreatRed,
                                            contentColor = Color.White
                                        ) {
                                            Text("$threatCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (threatCount > 0) Icons.Default.Warning else Icons.Default.Shield,
                                    contentDescription = "Security Shield Status",
                                    tint = if (threatCount > 0) SleekThreatRed else MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Session Memory Info Button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onMemoryClick,
                            modifier = Modifier.testTag("aegis_session_memory_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Session Memory",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Biometric Lock Vault Button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onLockClick,
                            modifier = Modifier.testTag("aegis_lock_vault_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock Application",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            )
        }
    }
}

