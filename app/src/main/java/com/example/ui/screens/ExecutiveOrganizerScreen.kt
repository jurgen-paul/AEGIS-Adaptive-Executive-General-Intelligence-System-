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
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegisTask
import com.example.ui.theme.AegisCyanPrimary
import com.example.ui.theme.AegisOutline
import com.example.ui.theme.AegisShieldGreen
import com.example.ui.theme.AegisSurfaceDark
import com.example.ui.theme.AegisSurfaceVariant
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.AegisThreatRed
import com.example.ui.theme.AegisWarningOrange

@Composable
fun ExecutiveOrganizerScreen(
    tasks: List<AegisTask>,
    onAddTask: (title: String, desc: String, isUrgent: Boolean, isImportant: Boolean, category: String) -> Unit,
    onUpdateStatus: (task: AegisTask, newStatus: String) -> Unit,
    onDeleteTask: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFilterTab by remember { mutableStateOf(0) } // 0: Matrix View, 1: List View

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
            // View Mode Selector Tabs
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
                            Icon(imageVector = Icons.Default.Grid3x3, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("EISENHOWER MATRIX", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = selectedFilterTab == 1,
                    onClick = { selectedFilterTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.List, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("TASK LOG (${tasks.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedFilterTab == 0) {
                // Eisenhower 4-Quadrant Priority Matrix View
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Q1: Urgent & Important
                        MatrixQuadrantCard(
                            title = "DO FIRST (Q1)",
                            subtitle = "Urgent & Important",
                            accentColor = AegisThreatRed,
                            tasks = tasks.filter { it.isUrgent && it.isImportant },
                            onUpdateStatus = onUpdateStatus,
                            onDeleteTask = onDeleteTask,
                            modifier = Modifier.weight(1f)
                        )

                        // Q2: Important, Not Urgent
                        MatrixQuadrantCard(
                            title = "SCHEDULE (Q2)",
                            subtitle = "Important, Not Urgent",
                            accentColor = AegisCyanPrimary,
                            tasks = tasks.filter { !it.isUrgent && it.isImportant },
                            onUpdateStatus = onUpdateStatus,
                            onDeleteTask = onDeleteTask,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Q3: Urgent, Not Important
                        MatrixQuadrantCard(
                            title = "DELEGATE (Q3)",
                            subtitle = "Urgent, Not Important",
                            accentColor = AegisWarningOrange,
                            tasks = tasks.filter { it.isUrgent && !it.isImportant },
                            onUpdateStatus = onUpdateStatus,
                            onDeleteTask = onDeleteTask,
                            modifier = Modifier.weight(1f)
                        )

                        // Q4: Neither
                        MatrixQuadrantCard(
                            title = "ROUTINE (Q4)",
                            subtitle = "Low Priority",
                            accentColor = AegisTextSecondary,
                            tasks = tasks.filter { !it.isUrgent && !it.isImportant },
                            onUpdateStatus = onUpdateStatus,
                            onDeleteTask = onDeleteTask,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
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
