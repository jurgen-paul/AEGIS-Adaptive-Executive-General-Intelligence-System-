package com.example.ui.screens

import android.content.Context
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.data.AegisSecurityEvent
import com.example.data.AegisSessionMemory
import com.example.router.AegisRouter
import com.example.service.BiometricAuthManager
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekCardBg
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekPrimaryContainer
import com.example.ui.theme.SleekSecurityGreen
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import com.example.ui.theme.SleekThreatRed

@Composable
fun SecurityShieldScreen(
    sessionMemory: AegisSessionMemory,
    securityEvents: List<AegisSecurityEvent>,
    onToggleSecurityMode: (String) -> Unit,
    onClearSecurityEvents: () -> Unit,
    onLockApp: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var testInputText by remember { mutableStateOf("") }
    var testResultText by remember { mutableStateOf("") }
    var testIsBlocked by remember { mutableStateOf(false) }
    var biometricFeedback by remember { mutableStateOf<String?>(null) }

    val router = remember { AegisRouter() }
    val isBiometricAvailable = remember(context) {
        BiometricAuthManager.isBiometricAvailable(context)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Biometric Privacy Protection Status Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = SleekPrimaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SleekPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Biometric Data Privacy",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekTextPrimary
                                )
                                Text(
                                    text = if (isBiometricAvailable) "Fingerprint / Face Unlock Enrolled" else "Biometric Hardware / PIN Ready",
                                    fontSize = 11.sp,
                                    color = SleekTextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SleekSecurityGreen.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SleekSecurityGreen)
                        ) {
                            Text(
                                text = "PROTECTED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekSecurityGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = "AEGIS restricts memory access and security vaults using biometric authentication to protect sensitive decisions.",
                        fontSize = 12.sp,
                        color = SleekTextSecondary,
                        lineHeight = 16.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val activity = context as? FragmentActivity
                                if (activity != null) {
                                    BiometricAuthManager.promptBiometricAuthentication(
                                        activity = activity,
                                        onSuccess = {
                                            biometricFeedback = "✅ Biometric verification succeeded!"
                                        },
                                        onError = { err ->
                                            biometricFeedback = "⚠️ $err"
                                        }
                                    )
                                } else {
                                    biometricFeedback = "✅ Verification simulated successfully!"
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("verify_biometrics_now_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary)
                        ) {
                            Text("Test Biometrics", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        if (onLockApp != null) {
                            Button(
                                onClick = onLockApp,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .testTag("lock_app_now_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SleekCardBg, contentColor = SleekTextPrimary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Lock App", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (biometricFeedback != null) {
                        Text(
                            text = biometricFeedback!!,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SleekPrimary
                        )
                    }
                }
            }
        }

        // Top Security Shield Status Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SleekBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = SleekCardBg)
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
                                tint = SleekSecurityGreen,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "AEGIS SECURITY SHIELD",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekTextPrimary
                                )
                                Text(
                                    text = "Zero-Trust Threat Defense Layer",
                                    fontSize = 11.sp,
                                    color = SleekTextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SleekSecurityGreen.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SleekSecurityGreen)
                        ) {
                            Text(
                                text = "ACTIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekSecurityGreen,
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
                        color = SleekTextSecondary,
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
                                    containerColor = if (isSelected) SleekPrimary else SleekCardBg,
                                    contentColor = if (isSelected) Color.White else SleekTextPrimary
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
                    .border(1.dp, SleekBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = SleekCardBg)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BugReport,
                            contentDescription = null,
                            tint = SleekPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Prompt Injection & Defense Tester",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Test prompt injection keywords (e.g. 'ignore previous instructions', 'bypass security', 'reveal system prompt'):",
                        fontSize = 12.sp,
                        color = SleekTextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = testInputText,
                        onValueChange = { testInputText = it },
                        placeholder = { Text("Enter prompt injection string to test...", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("aegis_threat_tester_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SleekPrimary,
                            unfocusedBorderColor = SleekBorder,
                            focusedTextColor = SleekTextPrimary,
                            unfocusedTextColor = SleekTextPrimary
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
                        colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("aegis_run_threat_test_button")
                    ) {
                        Text("Run Security Filter Test", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    if (testResultText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = if (testIsBlocked) SleekThreatRed.copy(alpha = 0.15f) else SleekSecurityGreen.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (testIsBlocked) SleekThreatRed else SleekSecurityGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = testResultText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (testIsBlocked) SleekThreatRed else SleekSecurityGreen,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
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
                    color = SleekTextSecondary,
                    letterSpacing = 1.sp
                )

                if (securityEvents.isNotEmpty()) {
                    IconButton(onClick = onClearSecurityEvents) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Clear Logs", tint = SleekThreatRed)
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
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SleekCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (event.severity == "HIGH") Icons.Default.Warning else Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (event.severity == "HIGH") SleekThreatRed else SleekSecurityGreen,
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
                        color = SleekTextPrimary
                    )
                    Text(
                        text = event.eventCode,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = SleekTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = event.actionTaken,
                    fontSize = 12.sp,
                    color = SleekTextSecondary
                )
            }
        }
    }
}
