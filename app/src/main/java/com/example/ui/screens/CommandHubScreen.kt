package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegisSessionLog
import com.example.data.AegisTask
import com.example.ui.components.EisenhowerMatrixComponent
import com.example.ui.components.HealthDisclaimerBanner
import com.example.ui.components.isHealthQueryIntent
import com.example.data.AegisSessionMemory
import com.example.data.TaskDomain
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekCardBg
import com.example.ui.theme.SleekDarkPromptBg
import com.example.ui.theme.SleekOnPrimaryContainer
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekPrimaryContainer
import com.example.ui.theme.SleekSecurityGreen
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import com.example.ui.theme.SleekThreatRed
import com.example.ui.theme.SleekWarningOrange

@Composable
fun CommandHubScreen(
    sessionMemory: AegisSessionMemory,
    sessionLogs: List<AegisSessionLog>,
    tasks: List<AegisTask> = emptyList(),
    isGenerating: Boolean,
    onSendPrompt: (String) -> Unit,
    onAddTask: ((title: String, desc: String, isUrgent: Boolean, isImportant: Boolean, category: String) -> Unit)? = null,
    onUpdateTaskStatus: ((task: AegisTask, newStatus: String) -> Unit)? = null,
    onUpdateTaskPriority: ((task: AegisTask, isUrgent: Boolean, isImportant: Boolean) -> Unit)? = null,
    onDeleteTask: ((taskId: Long) -> Unit)? = null,
    onExportLogs: (() -> Unit)? = null,
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
                voiceStatusMessage = "🎤 Voice command captured: \"$spokenText\" (Passed through Security Router)"
                onSendPrompt(spokenText)
            }
        }
    }

    val triggerSpeechToText = {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "AEGIS Voice Command - Security Filter Active")
        }
        try {
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            voiceStatusMessage = "⚠️ Voice input error: ${e.localizedMessage ?: "Recognizer unavailable"}"
        }
    }

    // Auto-scroll to bottom on new message
    LaunchedEffect(sessionLogs.size) {
        if (sessionLogs.isNotEmpty()) {
            listState.animateScrollToItem(sessionLogs.size - 1)
        }
    }

    val domainCards = remember {
        listOf(
            Triple("🧮", "Math & Logic", "Exact computation") to "Analyze logic: What is 342 minus 113?",
            Triple("🎨", "Art & Concept", "SVG Generation") to "Can you generate a mountain landscape concept?",
            Triple("💼", "Sales Support", "Objection handling") to "How to handle pricing objections transparently?",
            Triple("❤️", "Care Domain", "General wellness") to "What are best hydration targets for high focus?",
            Triple("📅", "Organizer", "Schedule sync") to "Help me prioritize my top 3 security tasks.",
            Triple("💬", "Talk to AEGIS", "Unified router") to "Introduce yourself and explain Aegis Security."
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBackground)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sleek Security Core Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp)),
                    colors = CardDefaults.cardColors(containerColor = SleekPrimaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(SleekSecurityGreen)
                            )
                            Text(
                                text = "Security Core: Level Strict",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SleekOnPrimaryContainer
                            )
                        }
                        Text(
                            text = "All communications are encrypted. Adaptive monitoring active.",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Normal,
                            color = SleekOnPrimaryContainer,
                            lineHeight = 22.sp
                        )

                        if (onExportLogs != null) {
                            Surface(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onExportLogs() }
                                    .testTag("hub_export_chat_button"),
                                color = SleekPrimary.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = "Export Encrypted Chat",
                                        tint = SleekPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Export Encrypted Chat (Scrubbed)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SleekPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Eisenhower Priority Matrix Dashboard Component
            item {
                EisenhowerMatrixComponent(
                    tasks = tasks,
                    onAddTask = { title, desc, urgent, important, cat ->
                        onAddTask?.invoke(title, desc, urgent, important, cat)
                    },
                    onUpdateStatus = { task, status ->
                        onUpdateTaskStatus?.invoke(task, status)
                    },
                    onUpdatePriority = { task, urgent, important ->
                        onUpdateTaskPriority?.invoke(task, urgent, important)
                    },
                    onDeleteTask = { id ->
                        onDeleteTask?.invoke(id)
                    }
                )
            }

            // Sleek 2-Column Grid Cards for Domains
            item {
                Text(
                    text = "INTELLIGENCE DOMAINS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextSecondary,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val chunks = domainCards.chunked(2)
                    chunks.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowItems.forEach { (meta, promptText) ->
                                val (emoji, title, desc) = meta
                                val isTalkCard = title == "Talk to AEGIS"

                                Surface(
                                    onClick = { onSendPrompt(promptText) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(104.dp)
                                        .testTag("aegis_domain_card_${title.lowercase().replace(" ", "_")}"),
                                    shape = RoundedCornerShape(24.dp),
                                    color = if (isTalkCard) SleekPrimary else SleekCardBg,
                                    border = if (isTalkCard) null else androidx.compose.foundation.BorderStroke(1.dp, SleekBorder)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(14.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = emoji, fontSize = 22.sp)
                                        Column {
                                            Text(
                                                text = title,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isTalkCard) Color.White else SleekTextPrimary
                                            )
                                            Text(
                                                text = desc,
                                                fontSize = 10.sp,
                                                color = if (isTalkCard) Color.White.copy(alpha = 0.7f) else SleekTextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Active Classifier State Widget Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SleekCardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = SleekPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Classifier Router",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekTextPrimary
                                )
                            }

                            val domainEnum = TaskDomain.fromId(sessionMemory.activeDomain)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SleekPrimaryContainer
                            ) {
                                Text(
                                    text = domainEnum.displayName.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekOnPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Confidence: ${(sessionMemory.confidenceScore * 100).toInt()}%",
                                fontSize = 11.sp,
                                color = SleekTextSecondary
                            )
                            Text(
                                text = "Module: aegis_${sessionMemory.activeDomain}_handler",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = SleekTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = SleekBorder)
                        Spacer(modifier = Modifier.height(8.dp))

                        when (sessionMemory.activeDomain) {
                            TaskDomain.MATH.domainId -> {
                                DomainMathWidget(mathResult = sessionMemory.mathResult)
                            }
                            TaskDomain.ART.domainId -> {
                                DomainArtWidget()
                            }
                            TaskDomain.SALES.domainId -> {
                                DomainSalesWidget(salesStage = sessionMemory.salesStage)
                            }
                            TaskDomain.HEALTH.domainId -> {
                                DomainHealthWidget(healthDisclaimer = sessionMemory.healthDisclaimerText)
                            }
                            TaskDomain.ORGANIZER.domainId -> {
                                DomainOrganizerWidget(lastAction = sessionMemory.organizerLastAction)
                            }
                            else -> {
                                Text(
                                    text = "💬 Conversational Memory Active • Context synchronized.",
                                    fontSize = 12.sp,
                                    color = SleekTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Message Logs History
            items(sessionLogs) { log ->
                ChatMessageItem(log = log)
            }
        }

        // Loading Indicator
        AnimatedVisibility(visible = isGenerating) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = SleekPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AEGIS Router computing non-blocking task...",
                    fontSize = 12.sp,
                    color = SleekTextSecondary
                )
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

        // Bottom Sleek Floating Prompt Container Bar
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
                            "Type or speak prompt...",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("aegis_prompt_input"),
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
                        .testTag("aegis_voice_stt_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Speech-to-Text Command",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable {
                            if (inputText.isNotBlank()) {
                                onSendPrompt(inputText)
                                inputText = ""
                            }
                        }
                        .testTag("aegis_send_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Prompt",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(log: AegisSessionLog) {
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
        // User Message Bubble
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp),
                color = SleekPrimary,
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "YOU",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = log.userQuery,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }

        // AEGIS Response Bubble
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp),
                color = SleekCardBg,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (log.securityThreatFlag) SleekThreatRed
                    else if (isHealthIntent) SleekWarningOrange
                    else SleekBorder
                ),
                modifier = Modifier.fillMaxWidth(0.92f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (log.securityThreatFlag) SleekThreatRed else SleekPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AEGIS • ${log.domain.uppercase()}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekPrimary
                            )
                        }

                        Text(
                            text = "${(log.confidenceScore * 100).toInt()}% match",
                            fontSize = 10.sp,
                            color = SleekTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isHealthIntent) {
                        HealthDisclaimerBanner(
                            isEmergency = log.healthEmergencyFlag,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Text(
                        text = log.responseText,
                        fontSize = 13.sp,
                        color = SleekTextPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DomainMathWidget(mathResult: String) {
    Column {
        Text("📐 Exact Computation Result:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SleekPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            color = SleekPrimaryContainer,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (mathResult.isNotBlank()) mathResult else "Exact numeric solver ready.",
                fontSize = 15.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = SleekOnPrimaryContainer,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}

@Composable
fun DomainArtWidget() {
    Column {
        Text("🎨 Vector Art Concept Generator:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SleekPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(SleekPrimary, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Brush, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SVG / Vector Blueprint Generated", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun DomainSalesWidget(salesStage: String) {
    Column {
        Text("💼 Sales Stage Pipeline Tracker:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SleekPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("discovery", "proposal", "objection", "closing").forEach { stage ->
                val isCurrent = stage.equals(salesStage, ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCurrent) SleekPrimary else SleekCardBg,
                    modifier = Modifier.weight(1f).padding(2.dp)
                ) {
                    Text(
                        text = stage.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color = if (isCurrent) Color.White else SleekTextSecondary,
                        modifier = Modifier.padding(vertical = 4.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun DomainHealthWidget(healthDisclaimer: String) {
    HealthDisclaimerBanner(isEmergency = false)
}

@Composable
fun DomainOrganizerWidget(lastAction: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("📅 Executive Schedule Sync", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SleekPrimary)
        Text("Last Action: ${lastAction.uppercase()}", fontSize = 11.sp, color = SleekSecurityGreen, fontWeight = FontWeight.SemiBold)
    }
}

