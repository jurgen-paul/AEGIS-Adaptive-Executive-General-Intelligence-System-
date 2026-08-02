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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
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
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekOnPrimaryContainer
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekPrimaryContainer
import com.example.ui.theme.SleekSecurityGreen
import com.example.ui.theme.SleekTextSecondary
import com.example.ui.theme.SleekThreatRed

@Composable
fun AegisTopAppBar(
    sessionMemory: AegisSessionMemory,
    threatCount: Int = 0,
    onShieldClick: () -> Unit,
    onMemoryClick: () -> Unit,
    onLockClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("aegis_top_app_bar"),
        color = SleekBackground,
        shadowElevation = 0.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Title & Subtitle
                Column {
                    Text(
                        text = "AEGIS",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekPrimary,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "SYSTEM ACTIVE • ${sessionMemory.securityMode.uppercase()} MODE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextSecondary,
                        letterSpacing = 1.2.sp
                    )
                }

                // Top Right Action Shield Badge Box
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SleekPrimaryContainer),
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
                                    tint = if (threatCount > 0) SleekThreatRed else SleekOnPrimaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    // Session Memory Info Button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, SleekBorder, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onMemoryClick,
                            modifier = Modifier.testTag("aegis_session_memory_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Session Memory",
                                tint = SleekPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Biometric Lock Vault Button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, SleekBorder, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onLockClick,
                            modifier = Modifier.testTag("aegis_lock_vault_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock Application",
                                tint = SleekPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(SleekBorder)
            )
        }
    }
}

