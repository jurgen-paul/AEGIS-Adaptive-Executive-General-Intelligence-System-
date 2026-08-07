package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegisTask

enum class EisenhowerQuadrant(
    val title: String,
    val subtitle: String,
    val actionLabel: String,
    val isUrgent: Boolean,
    val isImportant: Boolean,
    val icon: ImageVector,
    val tag: String
) {
    DO(
        title = "DO",
        subtitle = "Urgent & Important",
        actionLabel = "Do Immediately",
        isUrgent = true,
        isImportant = true,
        icon = Icons.Default.PriorityHigh,
        tag = "do"
    ),
    SCHEDULE(
        title = "SCHEDULE",
        subtitle = "Not Urgent & Important",
        actionLabel = "Schedule for Later",
        isUrgent = false,
        isImportant = true,
        icon = Icons.Default.Schedule,
        tag = "schedule"
    ),
    DELEGATE(
        title = "DELEGATE",
        subtitle = "Urgent & Not Important",
        actionLabel = "Delegate to Team",
        isUrgent = true,
        isImportant = false,
        icon = Icons.Default.Group,
        tag = "delegate"
    ),
    DELETE(
        title = "DELETE",
        subtitle = "Not Urgent & Not Important",
        actionLabel = "Eliminate & Purge",
        isUrgent = false,
        isImportant = false,
        icon = Icons.Default.Delete,
        tag = "delete"
    );

    // Backward compatibility getters
    companion object {
        val DO_FIRST get() = DO
        val ROUTINE get() = DELETE

        fun fromTask(task: AegisTask): EisenhowerQuadrant {
            return when {
                task.isUrgent && task.isImportant -> DO
                !task.isUrgent && task.isImportant -> SCHEDULE
                task.isUrgent && !task.isImportant -> DELEGATE
                else -> DELETE
            }
        }
    }
}

/**
 * Material3 Dashboard Component that categorizes tasks into an Eisenhower Priority Matrix (Do, Schedule, Delegate, Delete).
 */
@Composable
fun EisenhowerMatrixComponent(
    tasks: List<AegisTask>,
    onAddTask: (title: String, desc: String, isUrgent: Boolean, isImportant: Boolean, category: String) -> Unit,
    onUpdateStatus: (task: AegisTask, newStatus: String) -> Unit,
    onUpdatePriority: (task: AegisTask, isUrgent: Boolean, isImportant: Boolean) -> Unit,
    onDeleteTask: ((taskId: Long) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedQuadrantFilter by remember { mutableStateOf<EisenhowerQuadrant?>(null) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var initialQuadrantForNewTask by remember { mutableStateOf(EisenhowerQuadrant.DO) }

    val doTasks = tasks.filter { it.isUrgent && it.isImportant }
    val scheduleTasks = tasks.filter { !it.isUrgent && it.isImportant }
    val delegateTasks = tasks.filter { it.isUrgent && !it.isImportant }
    val deleteTasks = tasks.filter { !it.isUrgent && !it.isImportant }

    val totalTasks = tasks.size
    val completedCount = tasks.count { it.status == "completed" }
    val pendingCount = totalTasks - completedCount
    val urgentCount = tasks.count { it.isUrgent }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("eisenhower_matrix_container")
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Dashboard Header & Overview Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Grid3x3,
                            contentDescription = "Eisenhower Matrix Grid",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "EISENHOWER PRIORITY MATRIX",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "Dashboard Matrix • $pendingCount Pending • $completedCount Done",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable {
                            initialQuadrantForNewTask = EisenhowerQuadrant.DO
                            showAddTaskDialog = true
                        }
                        .testTag("add_matrix_task_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Task",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "New Task",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // Quick Filter Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (selectedQuadrantFilter == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { selectedQuadrantFilter = null }
                ) {
                    Text(
                        text = "All ($totalTasks)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedQuadrantFilter == null) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                EisenhowerQuadrant.values().forEach { quad ->
                    val isSelected = selectedQuadrantFilter == quad
                    val qColor = getQuadrantColor(quad)
                    val qCount = when (quad) {
                        EisenhowerQuadrant.DO -> doTasks.size
                        EisenhowerQuadrant.SCHEDULE -> scheduleTasks.size
                        EisenhowerQuadrant.DELEGATE -> delegateTasks.size
                        EisenhowerQuadrant.DELETE -> deleteTasks.size
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) qColor else qColor.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, qColor.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .clickable {
                                selectedQuadrantFilter = if (selectedQuadrantFilter == quad) null else quad
                            }
                            .testTag("filter_chip_${quad.tag}")
                    ) {
                        Text(
                            text = "${quad.title} ($qCount)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else qColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Material3 Grid Layout (2x2 Matrix Quadrants)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Row 1: DO & SCHEDULE
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuadrantGridCard(
                        quadrant = EisenhowerQuadrant.DO,
                        taskList = doTasks,
                        isSelected = selectedQuadrantFilter == EisenhowerQuadrant.DO,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedQuadrantFilter = if (selectedQuadrantFilter == EisenhowerQuadrant.DO) null else EisenhowerQuadrant.DO
                        },
                        onQuickAdd = {
                            initialQuadrantForNewTask = EisenhowerQuadrant.DO
                            showAddTaskDialog = true
                        },
                        onUpdateStatus = onUpdateStatus,
                        onUpdatePriority = onUpdatePriority,
                        onDeleteTask = onDeleteTask
                    )

                    QuadrantGridCard(
                        quadrant = EisenhowerQuadrant.SCHEDULE,
                        taskList = scheduleTasks,
                        isSelected = selectedQuadrantFilter == EisenhowerQuadrant.SCHEDULE,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedQuadrantFilter = if (selectedQuadrantFilter == EisenhowerQuadrant.SCHEDULE) null else EisenhowerQuadrant.SCHEDULE
                        },
                        onQuickAdd = {
                            initialQuadrantForNewTask = EisenhowerQuadrant.SCHEDULE
                            showAddTaskDialog = true
                        },
                        onUpdateStatus = onUpdateStatus,
                        onUpdatePriority = onUpdatePriority,
                        onDeleteTask = onDeleteTask
                    )
                }

                // Row 2: DELEGATE & DELETE
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuadrantGridCard(
                        quadrant = EisenhowerQuadrant.DELEGATE,
                        taskList = delegateTasks,
                        isSelected = selectedQuadrantFilter == EisenhowerQuadrant.DELEGATE,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedQuadrantFilter = if (selectedQuadrantFilter == EisenhowerQuadrant.DELEGATE) null else EisenhowerQuadrant.DELEGATE
                        },
                        onQuickAdd = {
                            initialQuadrantForNewTask = EisenhowerQuadrant.DELEGATE
                            showAddTaskDialog = true
                        },
                        onUpdateStatus = onUpdateStatus,
                        onUpdatePriority = onUpdatePriority,
                        onDeleteTask = onDeleteTask
                    )

                    QuadrantGridCard(
                        quadrant = EisenhowerQuadrant.DELETE,
                        taskList = deleteTasks,
                        isSelected = selectedQuadrantFilter == EisenhowerQuadrant.DELETE,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedQuadrantFilter = if (selectedQuadrantFilter == EisenhowerQuadrant.DELETE) null else EisenhowerQuadrant.DELETE
                        },
                        onQuickAdd = {
                            initialQuadrantForNewTask = EisenhowerQuadrant.DELETE
                            showAddTaskDialog = true
                        },
                        onUpdateStatus = onUpdateStatus,
                        onUpdatePriority = onUpdatePriority,
                        onDeleteTask = onDeleteTask
                    )
                }
            }

            // Expanded Quadrant Details / Selected View
            val displayTasks = when (selectedQuadrantFilter) {
                EisenhowerQuadrant.DO -> doTasks
                EisenhowerQuadrant.SCHEDULE -> scheduleTasks
                EisenhowerQuadrant.DELEGATE -> delegateTasks
                EisenhowerQuadrant.DELETE -> deleteTasks
                null -> tasks
            }

            if (selectedQuadrantFilter != null) {
                val activeQuad = selectedQuadrantFilter!!
                val quadColor = getQuadrantColor(activeQuad)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            quadColor.copy(alpha = 0.08f),
                            RoundedCornerShape(16.dp)
                        )
                        .border(1.dp, quadColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = activeQuad.icon,
                                contentDescription = null,
                                tint = quadColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${activeQuad.title} QUADRANT TASKS (${displayTasks.size})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = quadColor,
                                letterSpacing = 0.5.sp
                            )
                        }

                        TextButton(onClick = { selectedQuadrantFilter = null }) {
                            Text("Clear Filter", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (displayTasks.isEmpty()) {
                        Text(
                            text = "No tasks categorized under '${activeQuad.title}' (${activeQuad.subtitle}).",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            displayTasks.forEach { task ->
                                MatrixTaskItemRow(
                                    task = task,
                                    onUpdateStatus = onUpdateStatus,
                                    onUpdatePriority = onUpdatePriority,
                                    onDeleteTask = onDeleteTask
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Task Dialog with Quadrant Selector
    if (showAddTaskDialog) {
        AddMatrixTaskDialog(
            initialQuadrant = initialQuadrantForNewTask,
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title, desc, urgent, important, category ->
                onAddTask(title, desc, urgent, important, category)
                showAddTaskDialog = false
            }
        )
    }
}

@Composable
private fun QuadrantGridCard(
    quadrant: EisenhowerQuadrant,
    taskList: List<AegisTask>,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onQuickAdd: () -> Unit,
    onUpdateStatus: (task: AegisTask, newStatus: String) -> Unit,
    onUpdatePriority: (task: AegisTask, isUrgent: Boolean, isImportant: Boolean) -> Unit,
    onDeleteTask: ((taskId: Long) -> Unit)?
) {
    val qColor = getQuadrantColor(quadrant)
    val completedCount = taskList.count { it.status == "completed" }
    val pendingCount = taskList.size - completedCount
    val progress = if (taskList.isNotEmpty()) completedCount.toFloat() / taskList.size else 0f

    Card(
        modifier = modifier
            .testTag("quadrant_${quadrant.tag}_card")
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) qColor else qColor.copy(alpha = 0.35f),
                shape = RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) qColor.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Header: Icon, Title & Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(qColor.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = quadrant.icon,
                            contentDescription = quadrant.title,
                            tint = qColor,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    Text(
                        text = quadrant.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = qColor
                    )
                }

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(qColor)
                        .clickable { onQuickAdd() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Quick Add to ${quadrant.title}",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Text(
                text = quadrant.subtitle,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Progress Indicator Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = qColor,
                trackColor = qColor.copy(alpha = 0.18f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$pendingCount active / ${taskList.size} total",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = quadrant.actionLabel,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = qColor
                )
            }

            // List Preview inside Quadrant Card
            if (taskList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    taskList.take(3).forEach { task ->
                        MatrixTaskItemRow(
                            task = task,
                            onUpdateStatus = onUpdateStatus,
                            onUpdatePriority = onUpdatePriority,
                            onDeleteTask = onDeleteTask
                        )
                    }
                    if (taskList.size > 3) {
                        Text(
                            text = "+ ${taskList.size - 3} more",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = qColor,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MatrixTaskItemRow(
    task: AegisTask,
    onUpdateStatus: (task: AegisTask, newStatus: String) -> Unit,
    onUpdatePriority: (task: AegisTask, isUrgent: Boolean, isImportant: Boolean) -> Unit,
    onDeleteTask: ((taskId: Long) -> Unit)?
) {
    var showQuadrantMenu by remember { mutableStateOf(false) }
    val isCompleted = task.status == "completed"
    val currentQuadrant = EisenhowerQuadrant.fromTask(task)
    val quadrantColor = getQuadrantColor(currentQuadrant)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("matrix_task_item_${task.id}")
            .clip(RoundedCornerShape(10.dp))
            .border(
                1.dp,
                if (isCompleted) MaterialTheme.colorScheme.outline.copy(alpha = 0.2f) else quadrantColor.copy(alpha = 0.3f),
                RoundedCornerShape(10.dp)
            ),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(
                    onClick = {
                        val nextStatus = if (isCompleted) "pending" else "completed"
                        onUpdateStatus(task, nextStatus)
                    },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Toggle Complete",
                        tint = if (isCompleted) Color(0xFF66BB6A) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Quadrant Selector Pill
                Box {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = quadrantColor.copy(alpha = 0.15f),
                        modifier = Modifier
                            .clickable { showQuadrantMenu = true }
                            .testTag("task_quadrant_pill_${task.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = currentQuadrant.title,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = quadrantColor
                            )
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = "Move Quadrant",
                                tint = quadrantColor,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showQuadrantMenu,
                        onDismissRequest = { showQuadrantMenu = false }
                    ) {
                        EisenhowerQuadrant.values().forEach { quad ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${quad.title} (${quad.subtitle})",
                                        fontSize = 11.sp,
                                        fontWeight = if (quad == currentQuadrant) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    onUpdatePriority(task, quad.isUrgent, quad.isImportant)
                                    showQuadrantMenu = false
                                }
                            )
                        }
                    }
                }

                if (onDeleteTask != null) {
                    IconButton(
                        onClick = { onDeleteTask(task.id) },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Task",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddMatrixTaskDialog(
    initialQuadrant: EisenhowerQuadrant,
    onDismiss: () -> Unit,
    onConfirm: (title: String, desc: String, isUrgent: Boolean, isImportant: Boolean, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedQuadrant by remember { mutableStateOf(initialQuadrant) }
    var category by remember { mutableStateOf("organizer") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Matrix Task",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_task_title_input")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Categorize in Eisenhower Matrix:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    EisenhowerQuadrant.values().forEach { quad ->
                        val isSelected = selectedQuadrant == quad
                        val qColor = getQuadrantColor(quad)

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) qColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedQuadrant = quad }
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) qColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = quad.icon,
                                        contentDescription = null,
                                        tint = qColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Column {
                                        Text(
                                            text = quad.title,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = qColor
                                        )
                                        Text(
                                            text = quad.subtitle,
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = qColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            title.trim(),
                            description.trim(),
                            selectedQuadrant.isUrgent,
                            selectedQuadrant.isImportant,
                            category
                        )
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.testTag("dialog_confirm_add_task")
            ) {
                Text("Add Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun getQuadrantColor(quadrant: EisenhowerQuadrant): Color {
    return when (quadrant) {
        EisenhowerQuadrant.DO -> Color(0xFFEF5350)       // Red / Crimson
        EisenhowerQuadrant.SCHEDULE -> Color(0xFF2196F3) // Strategic Blue
        EisenhowerQuadrant.DELEGATE -> Color(0xFFFF9800) // Executive Amber
        EisenhowerQuadrant.DELETE -> Color(0xFF78909C)   // Slate Gray / Muted
    }
}
