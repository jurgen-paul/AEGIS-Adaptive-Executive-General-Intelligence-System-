package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
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
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekCardBg
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import com.example.ui.theme.SleekThreatRed
import com.example.ui.theme.SleekWarningOrange

/**
 * Reusable chat bubble component that visually distinguishes between
 * user and assistant messages in a LazyColumn layout.
 */
@Composable
fun ChatBubble(
    message: String,
    isUser: Boolean,
    modifier: Modifier = Modifier,
    senderName: String? = null,
    domain: String = "conversation",
    confidenceScore: Float = 0.95f,
    securityThreatFlag: Boolean = false,
    healthEmergencyFlag: Boolean = false,
    isHealthIntent: Boolean = false,
    formattedTimestamp: String? = null,
    extraContent: (@Composable () -> Unit)? = null
) {
    if (isUser) {
        UserChatBubble(
            message = message,
            modifier = modifier,
            senderName = senderName ?: "YOU",
            formattedTimestamp = formattedTimestamp
        )
    } else {
        AssistantChatBubble(
            message = message,
            modifier = modifier,
            senderName = senderName ?: "AEGIS",
            domain = domain,
            confidenceScore = confidenceScore,
            securityThreatFlag = securityThreatFlag,
            healthEmergencyFlag = healthEmergencyFlag,
            isHealthIntent = isHealthIntent,
            formattedTimestamp = formattedTimestamp,
            extraContent = extraContent
        )
    }
}

/**
 * Reusable User Chat Bubble (Right-aligned)
 */
@Composable
fun UserChatBubble(
    message: String,
    modifier: Modifier = Modifier,
    senderName: String = "YOU",
    formattedTimestamp: String? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .testTag("chat_bubble_user"),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp),
                color = SleekPrimary,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = senderName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                        if (formattedTimestamp != null) {
                            Text(
                                text = formattedTimestamp,
                                fontSize = 9.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = message,
                        fontSize = 14.sp,
                        color = Color.White,
                        lineHeight = 19.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(SleekPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Avatar",
                    tint = SleekPrimary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

/**
 * Reusable Assistant Chat Bubble (Left-aligned)
 */
@Composable
fun AssistantChatBubble(
    message: String,
    modifier: Modifier = Modifier,
    senderName: String = "AEGIS",
    domain: String = "conversation",
    confidenceScore: Float = 0.95f,
    securityThreatFlag: Boolean = false,
    healthEmergencyFlag: Boolean = false,
    isHealthIntent: Boolean = false,
    formattedTimestamp: String? = null,
    extraContent: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .testTag("chat_bubble_assistant"),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (securityThreatFlag) SleekThreatRed.copy(alpha = 0.15f)
                        else SleekPrimary.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Assistant Shield Avatar",
                    tint = if (securityThreatFlag) SleekThreatRed else SleekPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp),
                color = SleekCardBg,
                border = BorderStroke(
                    width = 1.dp,
                    color = when {
                        securityThreatFlag -> SleekThreatRed
                        isHealthIntent -> SleekWarningOrange
                        else -> SleekBorder
                    }
                ),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$senderName • ${domain.uppercase()}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (securityThreatFlag) SleekThreatRed else SleekPrimary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${(confidenceScore * 100).toInt()}% match",
                                fontSize = 10.sp,
                                color = SleekTextSecondary
                            )
                            if (formattedTimestamp != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = formattedTimestamp,
                                    fontSize = 9.sp,
                                    color = SleekTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (extraContent != null) {
                        extraContent()
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Text(
                        text = message,
                        fontSize = 13.sp,
                        color = SleekTextPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
