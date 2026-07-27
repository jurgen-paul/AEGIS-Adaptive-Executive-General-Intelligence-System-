package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegisSecurityEvent
import com.example.data.AegisSessionMemory
import com.example.router.AegisRouter
import com.example.ui.theme.AegisCyanPrimary
import com.example.ui.theme.AegisOutline
import com.example.ui.theme.AegisShieldGreen
import com.example.ui.theme.AegisSurfaceDark
import com.example.ui.theme.AegisSurfaceVariant
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.AegisThreatRed

@Composable
fun SecurityShieldScreen(
    sessionMemory: AegisSessionMemory,
    securityEvents: List<AegisSecurityEvent>,
    onToggleSecurityMode: (String) -> Unit,
    onClearSecurityEvents: () -> Unit,
    modifier: Modifier = Modifier
) {
    var testInputText by remember { mutableStateOf("") }
    var testResultText by remember { mutableStateOf("") }
    var testIsBlocked by remember { mutableStateOf(false) }

    val router = remember { AegisRouter() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Security Shield Status Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AegisShieldGreen.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
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
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = AegisShieldGreen,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "AEGIS SECURITY SHIELD",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AegisTextPrimary
                                )
                                Text(
                                    text = "Zero-Trust Threat Defense Layer",
                                    fontSize = 11.sp,
                                    color = AegisTextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AegisShieldGreen.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AegisShieldGreen)
                        ) {
                            Text(
                                text = "ACTIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AegisShieldGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mode Toggle Buttons
                    Text(
                        text = "SECURITY ENFORCEMENT LEVEL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AegisTextSecondary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("strict", "standard", "relaxed").forEach { mode ->
                            val isSelected = sessionMemory.securityMode.equals(mode, ignoreCase = true)
                            Button(
                                onClick = { onToggleSecurityMode(mode) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) AegisCyanPrimary else AegisSurfaceVariant,
                                    contentColor = if (isSelected) Color.Black else AegisTextPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("aegis_security_mode_${mode}")
                            ) {
                                Text(mode.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Prompt Injection Defense Tester
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
                            imageVector = Icons.Default.BugReport,
                            contentDescription = null,
                            tint = AegisCyanPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Prompt Injection & Defense Tester",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AegisTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Test prompt injection keywords (e.g. 'ignore previous instructions', 'bypass security', 'reveal system prompt'):",
                        fontSize = 12.sp,
                        color = AegisTextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = testInputText,
                        onValueChange = { testInputText = it },
                        placeholder = { Text("Enter prompt injection string to test...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("aegis_threat_tester_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AegisCyanPrimary,
                            unfocusedBorderColor = AegisOutline
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val clean = router.securityCheck(testInputText)
                            testIsBlocked = !clean
                            testResultText = if (!clean) {
                                "🛑 BLOCKED: Pattern matched blocked security filter rules! Input dropped safely."
                            } else {
                                "✅ PASSED: No malicious patterns detected. Clear for execution."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AegisCyanPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("aegis_run_threat_test_button")
                    ) {
                        Text("Run Security Filter Test", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    if (testResultText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = if (testIsBlocked) AegisThreatRed.copy(alpha = 0.15f) else AegisShieldGreen.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (testIsBlocked) AegisThreatRed else AegisShieldGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = testResultText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (testIsBlocked) AegisThreatRed else AegisShieldGreen,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }

        // Module ID Naming Convention Validator Info Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AegisSurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Module Regex Naming Specification",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AegisCyanPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Prefix Pattern: ^aegis_[a-z0-9_]+\$",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = AegisTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Active Modules: aegis_security_module, aegis_math_handler, aegis_health_advisor, aegis_sales_advisor, aegis_organizer_hub",
                        fontSize = 11.sp,
                        color = AegisTextSecondary
                    )
                }
            }
        }

        // Security Audit Logs Header & Clear Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SECURITY AUDIT LOGS (${securityEvents.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AegisTextSecondary,
                    letterSpacing = 1.sp
                )

                if (securityEvents.isNotEmpty()) {
                    IconButton(onClick = onClearSecurityEvents) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Clear Logs", tint = AegisThreatRed)
                    }
                }
            }
        }

        // Audit Logs List
        items(securityEvents) { event ->
            SecurityEventItem(event = event)
        }
    }
}

@Composable
fun SecurityEventItem(event: AegisSecurityEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = AegisSurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, AegisOutline)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (event.severity == "HIGH") Icons.Default.Warning else Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (event.severity == "HIGH") AegisThreatRed else AegisShieldGreen,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = event.threatType.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AegisTextPrimary
                    )
                    Text(
                        text = event.eventCode,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = AegisTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = event.actionTaken,
                    fontSize = 12.sp,
                    color = AegisTextSecondary
                )
            }
        }
    }
}
