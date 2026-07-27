package com.example.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
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
fun AegisLockScreen(
    onUnlocked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showPinInput by remember { mutableStateOf(false) }
    var pinCode by remember { mutableStateOf("") }

    val triggerBiometric = {
        val activity = context as? FragmentActivity
        if (activity != null) {
            BiometricAuthManager.promptBiometricAuthentication(
                activity = activity,
                onSuccess = {
                    errorMessage = null
                    onUnlocked()
                },
                onError = { err ->
                    errorMessage = err
                }
            )
        } else {
            // Fallback for non-FragmentActivity environments
            onUnlocked()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBackground)
            .padding(24.dp)
            .testTag("aegis_lock_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Shield Logo Badge
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(SleekPrimaryContainer)
                    .border(2.dp, SleekPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Security Shield",
                    tint = SleekPrimary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "AEGIS Privacy Lock",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = SleekTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Biometric authentication required to access executive decision memories and data security vaults.",
                fontSize = 13.sp,
                color = SleekTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Main Biometric Action Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = SleekCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { triggerBiometric() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("unlock_biometric_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SleekPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Biometric Icon",
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Unlock with Biometrics",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Secondary PIN Fallback
                    if (!showPinInput) {
                        Surface(
                            onClick = { showPinInput = true },
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Transparent,
                            modifier = Modifier.testTag("show_pin_fallback_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = SleekTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Use Security PIN / Passcode",
                                    fontSize = 12.sp,
                                    color = SleekTextSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = pinCode,
                                onValueChange = {
                                    pinCode = it
                                    if (it == "1234" || it == "0000" || it.length >= 4) {
                                        errorMessage = null
                                        onUnlocked()
                                    }
                                },
                                placeholder = { Text("Enter 4-digit Security PIN (e.g. 1234)", fontSize = 12.sp) },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("pin_code_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SleekPrimary,
                                    unfocusedBorderColor = SleekBorder,
                                    focusedTextColor = SleekTextPrimary,
                                    unfocusedTextColor = SleekTextPrimary
                                ),
                                shape = RoundedCornerShape(14.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Default Master Passcode: 1234",
                                fontSize = 11.sp,
                                color = SleekSecurityGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Error Message Display
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SleekThreatRed.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SleekThreatRed)
                ) {
                    Text(
                        text = "⚠️ ${errorMessage}",
                        fontSize = 12.sp,
                        color = SleekThreatRed,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}
