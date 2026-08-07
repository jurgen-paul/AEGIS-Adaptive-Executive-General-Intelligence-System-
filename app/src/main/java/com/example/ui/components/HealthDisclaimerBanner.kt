package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekThreatRed
import com.example.ui.theme.SleekWarningOrange

/**
 * Checks if a session log or query represents a health-related query intent.
 */
fun isHealthQueryIntent(
    domain: String,
    userQuery: String = "",
    responseText: String = "",
    healthEmergencyFlag: Boolean = false
): Boolean {
    if (healthEmergencyFlag) return true
    if (domain.equals("health", ignoreCase = true)) return true
    val combined = "$userQuery $responseText".lowercase()
    val healthKeywords = listOf(
        "symptom", "health", "doctor", "medicine", "medical", "hospital",
        "pain", "treatment", "clinic", "diagnosis", "wellness", "prescription",
        "emergency", "fever", "disease", "illness", "nutrition", "diet", "sleep"
    )
    return healthKeywords.any { combined.contains(it) }
}

/**
 * Global UI component that injects mandatory health disclaimers at the top of any
 * chat response or screen that triggers a health-related query intent.
 */
@Composable
fun HealthDisclaimerBanner(
    isEmergency: Boolean = false,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null
) {
    val borderColor = if (isEmergency) SleekThreatRed else SleekWarningOrange
    val containerBg = if (isEmergency) SleekThreatRed.copy(alpha = 0.18f) else SleekWarningOrange.copy(alpha = 0.15f)
    val headerTextColor = if (isEmergency) SleekThreatRed else SleekWarningOrange

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .testTag("health_disclaimer_banner"),
        color = containerBg,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isEmergency) Icons.Default.Warning else Icons.Default.HealthAndSafety,
                        contentDescription = "Health Disclaimer Icon",
                        tint = headerTextColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isEmergency) "🚨 EMERGENCY MEDICAL DISCLAIMER" else "⚕️ MANDATORY HEALTH DISCLAIMER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = headerTextColor
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = headerTextColor.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = "NOT MEDICAL ADVICE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = headerTextColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (onDismiss != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(20.dp)
                                .testTag("dismiss_health_banner_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss Banner",
                                tint = headerTextColor,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isEmergency) {
                    "CRITICAL: AEGIS AI outputs are for informational purposes only. If you or someone else is experiencing severe symptoms or a life-threatening medical emergency, CALL EMERGENCY SERVICES (911) IMMEDIATELY."
                } else {
                    "AEGIS outputs are for general wellness and educational purposes only and do NOT constitute professional medical advice, diagnosis, or treatment. Always consult a licensed physician or healthcare provider for medical concerns."
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.95f),
                lineHeight = 15.sp
            )

            if (isEmergency) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SleekThreatRed,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(vertical = 6.dp, horizontal = 10.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneInTalk,
                            contentDescription = "Emergency Dial",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Call Emergency Hotline (911)",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Live / Floating Health Disclaimer Banner that auto-appears dynamically when user is typing medical terms.
 */
@Composable
fun LiveHealthDisclaimerDetector(
    currentInputText: String,
    modifier: Modifier = Modifier
) {
    val healthKeywords = listOf("symptom", "pain", "doctor", "medicine", "medical", "hospital", "fever", "pill", "treatment")
    val isMedicalTyping = healthKeywords.any { currentInputText.lowercase().contains(it) }

    AnimatedVisibility(
        visible = isMedicalTyping,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        HealthDisclaimerBanner(
            isEmergency = false,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

/**
 * Reusable 'HealthDisclaimer' Composable component that displays a mandatory medical warning,
 * integrated into AI responses when health-related topics are detected.
 */
@Composable
fun HealthDisclaimer(
    isEmergency: Boolean = false,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null
) {
    HealthDisclaimerBanner(
        isEmergency = isEmergency,
        modifier = modifier,
        onDismiss = onDismiss
    )
}

