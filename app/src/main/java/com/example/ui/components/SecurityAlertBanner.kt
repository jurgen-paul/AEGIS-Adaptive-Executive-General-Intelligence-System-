package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AegisThreatRed
import com.example.ui.theme.AegisWarningOrange

@Composable
fun SecurityAlertBanner(
    isSecurityThreat: Boolean,
    isHealthEmergency: Boolean,
    onInspectSecurity: () -> Unit,
    onEmergencyDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isSecurityThreat || isHealthEmergency,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        val bannerBg = if (isHealthEmergency) AegisWarningOrange.copy(alpha = 0.15f) else AegisThreatRed.copy(alpha = 0.15f)
        val borderColor = if (isHealthEmergency) AegisWarningOrange else AegisThreatRed

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(bannerBg, RoundedCornerShape(12.dp))
                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isHealthEmergency) Icons.Default.HealthAndSafety else Icons.Default.Warning,
                    contentDescription = "Alert",
                    tint = borderColor,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (isHealthEmergency) "🚨 HEALTH EMERGENCY ALERT" else "🛡️ SECURITY SHIELD DEFENSE TRIGGERED",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = borderColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isHealthEmergency)
                            "Symptoms detected match an emergency flag. Please call 911 or emergency services immediately."
                        else
                            "Suspicious input pattern filtered. Prompt injection and data leak defense active.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (isSecurityThreat) {
                    Button(
                        onClick = onInspectSecurity,
                        colors = ButtonDefaults.buttonColors(containerColor = AegisThreatRed),
                        modifier = Modifier.testTag("aegis_inspect_threat_button")
                    ) {
                        Text("Audit", fontSize = 11.sp, color = Color.White)
                    }
                } else {
                    OutlinedButton(
                        onClick = onEmergencyDismiss,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AegisWarningOrange)
                    ) {
                        Text("Dismiss", fontSize = 11.sp, color = AegisWarningOrange)
                    }
                }
            }
        }
    }
}
