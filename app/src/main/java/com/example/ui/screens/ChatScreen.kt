package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegisSessionLog
import com.example.ui.components.ChatBubble
import com.example.ui.components.HealthDisclaimerBanner
import com.example.ui.components.LiveHealthDisclaimerDetector
import com.example.ui.components.isHealthQueryIntent
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekCardBg
import com.example.ui.theme.SleekDarkPromptBg
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekPrimaryContainer
import com.example.ui.theme.SleekSecurityGreen
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import com.example.ui.theme.SleekThreatRed
import com.example.ui.theme.SleekWarningOrange

data class ChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val sender: String, // "USER" or "AEGIS"
    val text: String,
    val domain: String = "general",
    val isSecurityThreat: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun ChatScreen(
    sessionLogs: List<AegisSessionLog> = emptyList(),
    isGenerating: Boolean = false,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    var voiceStatusMessage by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    // Speech-to-Text Activity Result Launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenTextList = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = spokenTextList?.getOrNull(0)
            if (!spokenText.isNullOrBlank()) {
                voiceStatusMessage = "🎤 Speech captured: \"$spokenText\" (Passed through AEGIS Security Router)"
                onSendMessage(spokenText)
            }
        }
    }

    val triggerSpeechToText = {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "AEGIS Voice Command - Zero-Trust Filter Active")
        }
        try {
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            voiceStatusMessage = "⚠️ Voice input error: ${e.localizedMessage ?: "Recognizer unavailable"}"
        }
    }

    LaunchedEffect(sessionLogs.size, isGenerating) {
        if (sessionLogs.isNotEmpty()) {
            listState.animateScrollToItem(sessionLogs.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBackground)
            .testTag("chat_screen")
    ) {
        // Top Header Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SleekBackground,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "AEGIS Chat",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekPrimary
                    )
                    Text(
                        text = "Unified Intelligence Interface",
                        fontSize = 11.sp,
                        color = SleekTextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SleekCardBg)
                        .border(1.dp, SleekBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SleekSecurityGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SleekTextPrimary
                        )
                    }
                }
            }
        }

        // Play Store (AAB) & APK Ready Banner
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onSendMessage("INSTALL GOOGLE APK BUNDLE ,PLAYSTORE CHATBOT-INTERFACE") },
            color = SleekPrimaryContainer,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SleekPrimary.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📦", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Play Store (.AAB) & APK Ready",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekPrimary
                        )
                        Text(
                            text = "CI/CD Pipeline Operational • Tap for deploy guide",
                            fontSize = 10.sp,
                            color = SleekTextSecondary
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SleekPrimary.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "VIEW GUIDE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekPrimary
                    )
                }
            }
        }

        // Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("chat_message_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (sessionLogs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🛡️", fontSize = 36.sp)
                            Text(
                                text = "Start a conversation with AEGIS",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SleekTextPrimary
                            )
                            Text(
                                text = "Ask questions, analyze logic, or request assistance.",
                                fontSize = 12.sp,
                                color = SleekTextSecondary
                            )
                        }
                    }
                }
            } else {
                items(sessionLogs, key = { it.id }) { log ->
                    ChatMessageLogItem(log = log)
                }
            }

            if (isGenerating) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = SleekPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AEGIS is processing your request...",
                            fontSize = 12.sp,
                            color = SleekTextSecondary
                        )
                    }
                }
            }
        }

        if (voiceStatusMessage != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                color = SleekPrimaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = voiceStatusMessage!!,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SleekPrimary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        // Live Health Disclaimer Auto-Detector while typing medical queries
        LiveHealthDisclaimerDetector(
            currentInputText = inputText,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        // Bottom Input Text Field & Action Button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(32.dp),
            color = SleekDarkPromptBg,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brain Icon Badge
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SleekPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🧠", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            "Type or speak message...",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(4.dp))

                // Microphone Speech-to-Text Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SleekPrimary.copy(alpha = 0.85f))
                        .clickable { triggerSpeechToText() }
                        .testTag("chat_voice_stt_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Speech-to-Text Input",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Send Action Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable {
                            if (inputText.isNotBlank()) {
                                onSendMessage(inputText)
                                inputText = ""
                            }
                        }
                        .testTag("chat_send_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatMessageLogItem(log: AegisSessionLog) {
    val isHealthIntent = isHealthQueryIntent(
        domain = log.domain,
        userQuery = log.userQuery,
        responseText = log.responseText,
        healthEmergencyFlag = log.healthEmergencyFlag
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // User Query Bubble
        ChatBubble(
            message = log.userQuery,
            isUser = true,
            senderName = "YOU"
        )

        // AEGIS Assistant Response Bubble
        ChatBubble(
            message = log.responseText,
            isUser = false,
            senderName = "AEGIS",
            domain = log.domain,
            confidenceScore = log.confidenceScore,
            securityThreatFlag = log.securityThreatFlag,
            healthEmergencyFlag = log.healthEmergencyFlag,
            isHealthIntent = isHealthIntent,
            extraContent = if (isHealthIntent) {
                {
                    HealthDisclaimerBanner(
                        isEmergency = log.healthEmergencyFlag,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            } else null
        )
    }
}
