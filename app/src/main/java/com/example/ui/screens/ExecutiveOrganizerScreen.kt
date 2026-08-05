package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Grid3x3
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegisSecurityEvent
import com.example.data.AegisTask
import com.example.ui.components.EisenhowerMatrixComponent
import com.example.ui.theme.AegisCyanPrimary
import com.example.ui.theme.AegisOutline
import com.example.ui.theme.AegisShieldGreen
import com.example.ui.theme.AegisSurfaceDark
import com.example.ui.theme.AegisSurfaceVariant
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.AegisThreatRed
import com.example.ui.theme.AegisWarningOrange

data class ExecutiveNotification(
    val id: String,
    val title: String,
    val message: String,
    val icon: ImageVector,
    val levelColor: Color,
    val timestampText: String
)

@Composable
fun ExecutiveOrganizerScreen(
    tasks: List<AegisTask>,
    securityEvents: List<AegisSecurityEvent> = emptyList(),
    onAddTask: (title: String, desc: String, isUrgent: Boolean, isImportant: Boolean, category: String) -> Unit,
    onUpdateStatus: (task: AegisTask, newStatus: String) -> Unit,
    onDeleteTask: (taskId: Long) -> Unit,
    onUpdatePriority: ((task: AegisTask, isUrgent: Boolean, isImportant: Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFilterTab by remember { mutableStateOf(0) } // 0: Executive Briefing, 1: Eisenhower Matrix, 2: All Tasks

    // Calculate executive metrics
    val totalTasks = tasks.size
    val completedTasks = tasks.count { it.status == "completed" }
    val urgentTasks = tasks.count { it.isUrgent && it.status != "completed" }
    val q1Tasks = tasks.filter { it.isUrgent && it.isImportant && it.status != "completed" }
    val completionRate = if (totalTasks > 0) (completedTasks.toFloat() / totalTasks) else 0f

    // Important executive notifications feed
    val systemNotifications = remember(securityEvents, tasks) {
        val list = mutableListOf<ExecutiveNotification>()
        
        // Add Biometric Lock notification
        list.add(
            ExecutiveNotification(
                id = "biometric_4d",
                title = "4D Biometric Glass Vault Active",
                message = "Zero-trust biometric hardware protection enabled for application launch.",
                icon = Icons.Default.Lock,
                levelColor = AegisShieldGreen,
                timestampText = "System Active"
            )
        )

        // Add Health Disclaimer Protocol status
        list.add(
            ExecutiveNotification(
                id = "health_protocol",
                title = "Mandatory Health Protocol Disclaimer",
                message = "Automated health query intent injection active on medical assistant interactions.",
                icon = Icons.Default.HealthAndSafety,
                levelColor = AegisWarningOrange,
                timestampText = "Protocol Enforced"
            )
        )

        // Add Security Events if present
        if (securityEvents.isNotEmpty()) {
            val latest = securityEvents.last()
            list.add(
                ExecutiveNotification(
                    id = "security_evt_${latest.id}",
                    title = "Security Event Logged: ${latest.threatType}",
                    message = latest.actionTaken,
                    icon = Icons.Default.Shield,
                    levelColor = if (latest.severity == "HIGH" || latest.severity == "CRITICAL") AegisThreatRed else AegisWarningOrange,
                    timestampText = "Security Log"
                )
            )
        } else {
            list.add(
                ExecutiveNotification(
                    id = "security_normal",
                    title = "System Security Status Normal",
                    message = "No unhandled prompt injection or sensitive data leaks detected.",
                    icon = Icons.Default.Security,
                    levelColor = AegisCyanPrimary,
                    timestampText = "Real-time Monitor"
                )
            )
        }

        // Urgent Task Alert if Q1 tasks exist
        if (q1Tasks.isNotEmpty()) {
            list.add(
                ExecutiveNotification(
                    id = "urgent_task_alert",
                    title = "Action Required: ${q1Tasks.size} Urgent & Important Items",
                    message = "Top priority: '${q1Tasks.first().title}' requires immediate executive attention.",
                    icon = Icons.Default.Warning,
                    levelColor = AegisThreatRed,
                    timestampText = "High Priority"
                )
            )
        }

        list
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = AegisCyanPrimary,
                contentColor = Color.Black,
                modifier = Modifier.testTag("aegis_add_task_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Executive View Selector Tabs
            TabRow(
                selectedTabIndex = selectedFilterTab,
                containerColor = AegisSurfaceDark,
                contentColor = AegisCyanPrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, AegisOutline, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedFilterTab == 0,
                    onClick = { selectedFilterTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("BRIEFING", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = selectedFilterTab == 1,
                    onClick = { selectedFilterTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Grid3x3, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("MATRIX", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = selectedFilterTab == 2,
                    onClick = { selectedFilterTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ALL TASKS (${tasks.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedFilterTab) {
                0 -> {
                    // Executive Briefing & Important Notifications View
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Executive Summary Dashboard Card
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, AegisCyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                    .testTag("executive_summary_card"),
                                colors = CardDefaults.cardColors(containerColor = AegisSurfaceDark)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "EXECUTIVE BRIEFING SUMMARY",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = AegisCyanPrimary,
                                                letterSpacing = 1.sp
                                            )
                                            Text(
                                                text = "Upcoming tasks & critical notifications overview",
                                                fontSize = 11.sp,
                                                color = AegisTextSecondary
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = AegisShieldGreen.copy(alpha = 0.15f)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(6.dp)
                                                        .clip(CircleShape)
                                                        .background(AegisShieldGreen)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "SYSTEM ONLINE",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = AegisShieldGreen
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // KPI Metrics Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Metric 1: Urgent Items
                                        Surface(
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            color = AegisSurfaceVariant,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, if (urgentTasks > 0) AegisThreatRed.copy(alpha = 0.6f) else AegisOutline)
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text("URGENT FOCUS", fontSize = 9.sp, color = AegisTextSecondary, fontWeight = FontWeight.Bold)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "$urgentTasks",
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = if (urgentTasks > 0) AegisThreatRed else AegisTextPrimary
                                                )
                                            }
                                        }

                                        // Metric 2: Pending Tasks
                                        Surface(
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            color = AegisSurfaceVariant,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, AegisOutline)
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text("PENDING TASKS", fontSize = 9.sp, color = AegisTextSecondary, fontWeight = FontWeight.Bold)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "${totalTasks - completedTasks}",
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = AegisCyanPrimary
                                                )
                                            }
                                        }

                                        // Metric 3: Important Alerts
                                        Surface(
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            color = AegisSurfaceVariant,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, AegisOutline)
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text("ALERTS FEED", fontSize = 9.sp, color = AegisTextSecondary, fontWeight = FontWeight.Bold)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "${systemNotifications.size}",
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = AegisWarningOrange
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Completion Progress Bar
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Task Completion Rate", fontSize = 11.sp, color = AegisTextSecondary)
                                            Text("${(completionRate * 100).toInt()}% ($completedTasks/$totalTasks)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AegisShieldGreen)
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        LinearProgressIndicator(
                                            progress = { completionRate },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = AegisShieldGreen,
                                            trackColor = AegisSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        // Important Notifications Feed Section
                        item {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "IMPORTANT NOTIFICATIONS & ALERTS",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AegisTextPrimary
                                    )
                                    Text(
                                        text = "${systemNotifications.size} Active",
                                        fontSize = 11.sp,
                                        color = AegisCyanPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    systemNotifications.forEach { item ->
                                        ExecutiveNotificationCard(notification = item)
                                    }
                                }
                            }
                        }

                        // Upcoming High-Priority Tasks Section
                        item {
                            Column {
                                Text(
                                    text = "UPCOMING CRITICAL TASKS",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AegisTextPrimary
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                val upcomingTasks = tasks.filter { it.status != "completed" }
                                if (upcomingTasks.isEmpty()) {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = AegisSurfaceDark,
                                        shape = RoundedCornerShape(12.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, AegisOutline)
                                    ) {
                                        Text(
                                            text = "✨ All executive tasks completed! Tap '+' to add a new task.",
                                            fontSize = 12.sp,
                                            color = AegisTextSecondary,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        upcomingTasks.take(5).forEach { task ->
                                            TaskListItem(
                                                task = task,
                                                onUpdateStatus = onUpdateStatus,
                                                onDeleteTask = onDeleteTask
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Eisenhower 4-Quadrant Priority Matrix View
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            EisenhowerMatrixComponent(
                                tasks = tasks,
                                onAddTask = onAddTask,
                                onUpdateStatus = onUpdateStatus,
                                onUpdatePriority = { task, urgent, important ->
                                    onUpdatePriority?.invoke(task, urgent, important)
                                },
                                onDeleteTask = onDeleteTask
                            )
                        }
                    }
                }

                2 -> {
                    // All Tasks List View
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(tasks) { task ->
                            TaskListItem(
                                task = task,
                                onUpdateStatus = onUpdateStatus,
                                onDeleteTask = onDeleteTask
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Task Dialog
    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var isUrgent by remember { mutableStateOf(false) }
        var isImportant by remember { mutableStateOf(true) }
        var category by remember { mutableStateOf("organizer") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Executive Task", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Task Title") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("aegis_task_title_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Is Urgent?")
                        Switch(
                            checked = isUrgent,
                            onCheckedChange = { isUrgent = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = AegisThreatRed)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Is Important?")
                        Switch(
                            checked = isImportant,
                            onCheckedChange = { isImportant = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = AegisCyanPrimary)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onAddTask(title, description, isUrgent, isImportant, category)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AegisCyanPrimary),
                    modifier = Modifier.testTag("aegis_save_task_button")
                ) {
                    Text("Save Task", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ExecutiveNotificationCard(notification: ExecutiveNotification) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("executive_notification_item"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AegisSurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, notification.levelColor.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = notification.levelColor.copy(alpha = 0.15f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = notification.icon,
                        contentDescription = null,
                        tint = notification.levelColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AegisTextPrimary
                    )

                    Text(
                        text = notification.timestampText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = notification.levelColor
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = notification.message,
                    fontSize = 11.sp,
                    color = AegisTextSecondary,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
fun MatrixQuadrantCard(
    title: String,
    subtitle: String,
    accentColor: Color,
    tasks: List<AegisTask>,
    onUpdateStatus: (AegisTask, String) -> Unit,
    onDeleteTask: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = AegisSurfaceDark)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = accentColor)
                Text("${tasks.size}", fontSize = 10.sp, color = AegisTextSecondary, fontWeight = FontWeight.Bold)
            }
            Text(subtitle, fontSize = 9.sp, color = AegisTextSecondary)

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(tasks) { task ->
                    Surface(
                        color = AegisSurfaceVariant,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (task.status == "completed") AegisTextSecondary else AegisTextPrimary
                                )
                            }
                            IconButton(
                                onClick = {
                                    val nextStatus = if (task.status == "completed") "pending" else "completed"
                                    onUpdateStatus(task, nextStatus)
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (task.status == "completed") Icons.Default.CheckCircle else Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (task.status == "completed") AegisShieldGreen else AegisTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskListItem(
    task: AegisTask,
    onUpdateStatus: (AegisTask, String) -> Unit,
    onDeleteTask: (Long) -> Unit
) {
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
            Checkbox(
                checked = task.status == "completed",
                onCheckedChange = { checked ->
                    onUpdateStatus(task, if (checked) "completed" else "pending")
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = AegisShieldGreen,
                    uncheckedColor = AegisOutline
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (task.status == "completed") AegisTextSecondary else AegisTextPrimary
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        fontSize = 12.sp,
                        color = AegisTextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (task.isUrgent) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = AegisThreatRed.copy(alpha = 0.2f)
                        ) {
                            Text("URGENT", fontSize = 9.sp, color = AegisThreatRed, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    }
                    if (task.isImportant) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = AegisCyanPrimary.copy(alpha = 0.2f)
                        ) {
                            Text("IMPORTANT", fontSize = 9.sp, color = AegisCyanPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    }
                }
            }

            IconButton(onClick = { onDeleteTask(task.id) }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = AegisThreatRed.copy(alpha = 0.7f))
            }
        }
    }
}

