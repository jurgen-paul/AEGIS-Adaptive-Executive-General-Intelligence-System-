package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Grid3x3
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
    DO_FIRST(
        title = "DO FIRST",
        subtitle = "Urgent & Important",
        actionLabel = "Immediate Execution",
        isUrgent = true,
        isImportant = true,
        icon = Icons.Default.PriorityHigh,
        tag = "do_first"
    ),
    SCHEDULE(
        title = "SCHEDULE",
        subtitle = "Not Urgent & Important",
        actionLabel = "Strategic Planning",
        isUrgent = false,
        isImportant = true,
        icon = Icons.Default.Schedule,
        tag = "schedule"
    ),
    DELEGATE(
        title = "DELEGATE",
        subtitle = "Urgent & Not Important",
        actionLabel = "Quick Hand-off",
        isUrgent = true,
        isImportant = false,
        icon = Icons.Default.Group,
        tag = "delegate"
    ),
    ROUTINE(
        title = "ROUTINE",
        subtitle = "Not Urgent & Not Important",
        actionLabel = "Maintenance & Low Priority",
        isUrgent = false,
        isImportant = false,
        icon = Icons.Default.Repeat,
        tag = "routine"
    );

    companion object {
        fun fromTask(task: AegisTask): EisenhowerQuadrant {
            return when {
                task.isUrgent && task.isImportant -> DO_FIRST
                !task.isUrgent && task.isImportant -> SCHEDULE
                task.isUrgent && !task.isImportant -> DELEGATE
                else -> ROUTINE
            }
        }
    }
}

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
    var initialQuadrantForNewTask by remember { mutableStateOf(EisenhowerQuadrant.DO_FIRST) }

    val doFirstTasks = tasks.filter { it.isUrgent && it.isImportant }
    val scheduleTasks = tasks.filter { !it.isUrgent && it.isImportant }
    val delegateTasks = tasks.filter { it.isUrgent && !it.isImportant }
    val routineTasks = tasks.filter { !it.isUrgent && !it.isImportant }

    val completedCount = tasks.count { it.status == "completed" }
    val pendingCount = tasks.size - completedCount

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
            // Matrix Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Grid3x3,
                            contentDescription = "Eisenhower Matrix Icon",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "EISENHOWER PRIORITY MATRIX",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "Triage: $pendingCount pending • $completedCount completed",
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
                            initialQuadrantForNewTask = EisenhowerQuadrant.DO_FIRST
                            showAddTaskDialog = true
                        }
                        .testTag("add_matrix_task_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
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
                            text = "Add Task",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // 2x2 Quadrant Grid Summary Cards
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Top Row: DO FIRST & SCHEDULE
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuadrantSummaryCard(
                        quadrant = EisenhowerQuadrant.DO_FIRST,
                        taskCount = doFirstTasks.size,
                        pendingCount = doFirstTasks.count { it.status != "completed" },
                        color = Color(0xFFEF5350), // Red
                        isSelected = selectedQuadrantFilter == EisenhowerQuadrant.DO_FIRST,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedQuadrantFilter = if (selectedQuadrantFilter == EisenhowerQuadrant.DO_FIRST) null else EisenhowerQuadrant.DO_FIRST
                        },
                        onQuickAdd = {
                            initialQuadrantForNewTask = EisenhowerQuadrant.DO_FIRST
                            showAddTaskDialog = true
                        }
                    )

                    QuadrantSummaryCard(
                        quadrant = EisenhowerQuadrant.SCHEDULE,
                        taskCount = scheduleTasks.size,
                        pendingCount = scheduleTasks.count { it.status != "completed" },
                        color = MaterialTheme.colorScheme.primary, // Blue/Cyan
                        isSelected = selectedQuadrantFilter == EisenhowerQuadrant.SCHEDULE,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedQuadrantFilter = if (selectedQuadrantFilter == EisenhowerQuadrant.SCHEDULE) null else EisenhowerQuadrant.SCHEDULE
                        },
                        onQuickAdd = {
                            initialQuadrantForNewTask = EisenhowerQuadrant.SCHEDULE
                            showAddTaskDialog = true
                        }
                    )
                }

                // Bottom Row: DELEGATE & ROUTINE
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuadrantSummaryCard(
                        quadrant = EisenhowerQuadrant.DELEGATE,
                        taskCount = delegateTasks.size,
                        pendingCount = delegateTasks.count { it.status != "completed" },
                        color = Color(0xFFFF9800), // Orange
                        isSelected = selectedQuadrantFilter == EisenhowerQuadrant.DELEGATE,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedQuadrantFilter = if (selectedQuadrantFilter == EisenhowerQuadrant.DELEGATE) null else EisenhowerQuadrant.DELEGATE
                        },
                        onQuickAdd = {
                            initialQuadrantForNewTask = EisenhowerQuadrant.DELEGATE
                            showAddTaskDialog = true
                        }
                    )

                    QuadrantSummaryCard(
                        quadrant = EisenhowerQuadrant.ROUTINE,
                        taskCount = routineTasks.size,
                        pendingCount = routineTasks.count { it.status != "completed" },
                        color = Color(0xFF66BB6A), // Green
                        isSelected = selectedQuadrantFilter == EisenhowerQuadrant.ROUTINE,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedQuadrantFilter = if (selectedQuadrantFilter == EisenhowerQuadrant.ROUTINE) null else EisenhowerQuadrant.ROUTINE
                        },
                        onQuickAdd = {
                            initialQuadrantForNewTask = EisenhowerQuadrant.ROUTINE
                            showAddTaskDialog = true
                        }
                    )
                }
            }

            // Quadrant Detail / Task List View
            val displayTasks = when (selectedQuadrantFilter) {
                EisenhowerQuadrant.DO_FIRST -> doFirstTasks
                EisenhowerQuadrant.SCHEDULE -> scheduleTasks
                EisenhowerQuadrant.DELEGATE -> delegateTasks
                EisenhowerQuadrant.ROUTINE -> routineTasks
                null -> tasks
            }

            val activeFilter = selectedQuadrantFilter

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (activeFilter != null) "${activeFilter.title} TASKS (${displayTasks.size})" else "ALL QUADRANT TASKS (${tasks.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 0.5.sp
                    )

                    if (activeFilter != null) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.clickable { selectedQuadrantFilter = null }
                        ) {
                            Text(
                                text = "Show All",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (displayTasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No tasks categorized in this quadrant yet.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        displayTasks.take(6).forEach { task ->
                            MatrixTaskItemRow(
                                task = task,
                                onUpdateStatus = onUpdateStatus,
                                onUpdatePriority = onUpdatePriority,
                                onDeleteTask = onDeleteTask
                            )
                        }
                        if (displayTasks.size > 6) {
                            Text(
                                text = "+ ${displayTasks.size - 6} more tasks in matrix...",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
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
private fun QuadrantSummaryCard(
    quadrant: EisenhowerQuadrant,
    taskCount: Int,
    pendingCount: Int,
    color: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onQuickAdd: () -> Unit
) {
    Card(
        modifier = modifier
            .testTag("quadrant_${quadrant.tag}_card")
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) color else color.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
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
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = quadrant.icon,
                            contentDescription = quadrant.title,
                            tint = color,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Text(
                        text = quadrant.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }

                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(color)
                        .clickable { onQuickAdd() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Quick Add to ${quadrant.title}",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Text(
                text = quadrant.subtitle,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$pendingCount active / $taskCount total",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = if (isSelected) "Filtered" else "View",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant
                )
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

    val quadrantColor = when (currentQuadrant) {
        EisenhowerQuadrant.DO_FIRST -> Color(0xFFEF5350)
        EisenhowerQuadrant.SCHEDULE -> MaterialTheme.colorScheme.primary
        EisenhowerQuadrant.DELEGATE -> Color(0xFFFF9800)
        EisenhowerQuadrant.ROUTINE -> Color(0xFF66BB6A)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("matrix_task_item_${task.id}")
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                if (isCompleted) MaterialTheme.colorScheme.outline.copy(alpha = 0.2f) else quadrantColor.copy(alpha = 0.3f),
                RoundedCornerShape(12.dp)
            ),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = {
                        val nextStatus = if (isCompleted) "pending" else "completed"
                        onUpdateStatus(task, nextStatus)
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Toggle Complete",
                        tint = if (isCompleted) Color(0xFF66BB6A) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (task.description.isNotBlank()) {
                        Text(
                            text = task.description,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Quadrant Selector Pill
                Box {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = quadrantColor.copy(alpha = 0.15f),
                        modifier = Modifier
                            .clickable { showQuadrantMenu = true }
                            .testTag("task_quadrant_pill_${task.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = currentQuadrant.title,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = quadrantColor
                            )
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = "Move Quadrant",
                                tint = quadrantColor,
                                modifier = Modifier.size(12.dp)
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
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Task",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
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
                    text = "Select Eisenhower Quadrant:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    EisenhowerQuadrant.values().forEach { quad ->
                        val isSelected = selectedQuadrant == quad
                        val color = when (quad) {
                            EisenhowerQuadrant.DO_FIRST -> Color(0xFFEF5350)
                            EisenhowerQuadrant.SCHEDULE -> MaterialTheme.colorScheme.primary
                            EisenhowerQuadrant.DELEGATE -> Color(0xFFFF9800)
                            EisenhowerQuadrant.ROUTINE -> Color(0xFF66BB6A)
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedQuadrant = quad }
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) color else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
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
                                        tint = color,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Column {
                                        Text(
                                            text = quad.title,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = color
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
                                        tint = color,
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
