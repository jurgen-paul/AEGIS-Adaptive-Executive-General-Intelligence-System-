package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Grid3x3
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.ui.theme.AegisCyanPrimary
import com.example.ui.theme.AegisOutline
import com.example.ui.theme.AegisShieldGreen
import com.example.ui.theme.AegisSurfaceDark
import com.example.ui.theme.AegisSurfaceVariant
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.AegisThreatRed
import com.example.ui.theme.AegisWarningOrange

/**
 * Eisenhower Matrix Quadrant specification for categorizing tasks by Urgency and Importance.
 */
enum class MatrixQuadrant(
    val id: String,
    val title: String,
    val subtitle: String,
    val actionText: String,
    val isUrgent: Boolean,
    val isImportant: Boolean,
    val icon: ImageVector,
    val accentColor: Color,
    val badgeLabel: String
) {
    DO(
        id = "do",
        title = "DO FIRST",
        subtitle = "Urgent & Important",
        actionText = "Execute immediately — crisis, core deadlines, security patches",
        isUrgent = true,
        isImportant = true,
        icon = Icons.Default.PriorityHigh,
        accentColor = AegisThreatRed,
        badgeLabel = "Q1: DO"
    ),
    SCHEDULE(
        id = "schedule",
        title = "SCHEDULE",
        subtitle = "Not Urgent & Important",
        actionText = "Plan on calendar — strategic architecture, health, long-term goals",
        isUrgent = false,
        isImportant = true,
        icon = Icons.Default.Schedule,
        accentColor = AegisCyanPrimary,
        badgeLabel = "Q2: SCHEDULE"
    ),
    DELEGATE(
        id = "delegate",
        title = "DELEGATE",
        subtitle = "Urgent & Not Important",
        actionText = "Assign to team / assistant — routine queries, logistics, handoffs",
        isUrgent = true,
        isImportant = false,
        icon = Icons.Default.Group,
        accentColor = AegisWarningOrange,
        badgeLabel = "Q3: DELEGATE"
    ),
    DELETE(
        id = "delete",
        title = "DELETE / ELIMINATE",
        subtitle = "Not Urgent & Not Important",
        actionText = "Drop or archive — distractions, stale logs, trivial tasks",
        isUrgent = false,
        isImportant = false,
        icon = Icons.Default.Delete,
        accentColor = Color(0xFF90A4AE),
        badgeLabel = "Q4: DELETE"
    );

    companion object {
        fun resolve(isUrgent: Boolean, isImportant: Boolean): MatrixQuadrant {
            return when {
                isUrgent && isImportant -> DO
                !isUrgent && isImportant -> SCHEDULE
                isUrgent && !isImportant -> DELEGATE
                else -> DELETE
            }
        }

        fun fromTask(task: AegisTask): MatrixQuadrant {
            return resolve(task.isUrgent, task.isImportant)
        }
    }
}

/**
 * High-performance, Material 3 Task Management Component that categorizes items
 * into the Eisenhower Priority Matrix (Urgent/Important) and saves them using Room.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EisenhowerTaskManagerComponent(
    tasks: List<AegisTask>,
    onAddTask: (title: String, desc: String, isUrgent: Boolean, isImportant: Boolean, category: String) -> Unit,
    onUpdateStatus: (task: AegisTask, newStatus: String) -> Unit,
    onUpdatePriority: (task: AegisTask, isUrgent: Boolean, isImportant: Boolean) -> Unit,
    onDeleteTask: (taskId: Long) -> Unit,
    onClearCompleted: (() -> Unit)? = null,
    onSeedTemplates: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedQuadrantFilter by remember { mutableStateOf<MatrixQuadrant?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showCompletedOnly by remember { mutableStateOf<Boolean?>(null) } // null = all, true = completed, false = active
    var showCreateDialog by remember { mutableStateOf(false) }
    var showConfirmClearDialog by remember { mutableStateOf(false) }
    var isGridView by remember { mutableStateOf(false) }

    // Categorized Tasks
    val doTasks = remember(tasks) { tasks.filter { it.isUrgent && it.isImportant } }
    val scheduleTasks = remember(tasks) { tasks.filter { !it.isUrgent && it.isImportant } }
    val delegateTasks = remember(tasks) { tasks.filter { it.isUrgent && !it.isImportant } }
    val deleteTasks = remember(tasks) { tasks.filter { !it.isUrgent && !it.isImportant } }

    val totalCount = tasks.size
    val completedCount = tasks.count { it.status == "completed" }
    val completionRatio = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    // Filtered tasks for list view
    val displayTasks = remember(tasks, selectedQuadrantFilter, searchQuery, showCompletedOnly) {
        tasks.filter { task ->
            val matchesQuadrant = selectedQuadrantFilter == null || MatrixQuadrant.fromTask(task) == selectedQuadrantFilter
            val matchesSearch = searchQuery.isBlank() || task.title.contains(searchQuery, ignoreCase = true) || task.description.contains(searchQuery, ignoreCase = true)
            val matchesStatus = when (showCompletedOnly) {
                true -> task.status == "completed"
                false -> task.status != "completed"
                null -> true
            }
            matchesQuadrant && matchesSearch && matchesStatus
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("eisenhower_task_manager"),
        colors = CardDefaults.cardColors(containerColor = AegisSurfaceDark),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AegisCyanPrimary.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = AegisCyanPrimary.copy(alpha = 0.2f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Grid3x3,
                                    contentDescription = null,
                                    tint = AegisCyanPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "EISENHOWER TASK MANAGER",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AegisCyanPrimary,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Urgent / Important Priority Matrix",
                                fontSize = 11.sp,
                                color = AegisTextSecondary
                            )
                        }
                    }
                }

                // Room Persistence Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = AegisShieldGreen.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AegisShieldGreen.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = "Room Database",
                            tint = AegisShieldGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Room DB: aegis_tasks",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AegisShieldGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Overall Progress & Matrix Statistics
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = AegisSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, AegisOutline.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Matrix Completion Progress",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AegisTextPrimary
                        )
                        Text(
                            text = "$completedCount / $totalCount completed (${(completionRatio * 100).toInt()}%)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AegisShieldGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { completionRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = AegisShieldGreen,
                        trackColor = AegisSurfaceDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Quadrants Quick Stats Banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuadrantSummaryPill(
                    quadrant = MatrixQuadrant.DO,
                    count = doTasks.size,
                    isSelected = selectedQuadrantFilter == MatrixQuadrant.DO,
                    onClick = {
                        selectedQuadrantFilter = if (selectedQuadrantFilter == MatrixQuadrant.DO) null else MatrixQuadrant.DO
                    },
                    modifier = Modifier.weight(1f)
                )
                QuadrantSummaryPill(
                    quadrant = MatrixQuadrant.SCHEDULE,
                    count = scheduleTasks.size,
                    isSelected = selectedQuadrantFilter == MatrixQuadrant.SCHEDULE,
                    onClick = {
                        selectedQuadrantFilter = if (selectedQuadrantFilter == MatrixQuadrant.SCHEDULE) null else MatrixQuadrant.SCHEDULE
                    },
                    modifier = Modifier.weight(1f)
                )
                QuadrantSummaryPill(
                    quadrant = MatrixQuadrant.DELEGATE,
                    count = delegateTasks.size,
                    isSelected = selectedQuadrantFilter == MatrixQuadrant.DELEGATE,
                    onClick = {
                        selectedQuadrantFilter = if (selectedQuadrantFilter == MatrixQuadrant.DELEGATE) null else MatrixQuadrant.DELEGATE
                    },
                    modifier = Modifier.weight(1f)
                )
                QuadrantSummaryPill(
                    quadrant = MatrixQuadrant.DELETE,
                    count = deleteTasks.size,
                    isSelected = selectedQuadrantFilter == MatrixQuadrant.DELETE,
                    onClick = {
                        selectedQuadrantFilter = if (selectedQuadrantFilter == MatrixQuadrant.DELETE) null else MatrixQuadrant.DELETE
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search & Action Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter items in Room...", fontSize = 12.sp, color = AegisTextSecondary) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = AegisCyanPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = AegisTextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("eisenhower_search_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AegisCyanPrimary,
                        unfocusedBorderColor = AegisOutline,
                        focusedTextColor = AegisTextPrimary,
                        unfocusedTextColor = AegisTextPrimary
                    )
                )

                // Add Task Button
                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AegisCyanPrimary, contentColor = Color.Black),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("eisenhower_new_task_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Task", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Secondary Controls: Grid/List Toggle & Preset Seeds & Clear Completed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(
                        selected = isGridView,
                        onClick = { isGridView = !isGridView },
                        label = { Text(if (isGridView) "2x2 Matrix View" else "List View", fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Grid3x3, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AegisCyanPrimary.copy(alpha = 0.2f),
                            selectedLabelColor = AegisCyanPrimary
                        )
                    )

                    if (selectedQuadrantFilter != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = selectedQuadrantFilter!!.accentColor.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, selectedQuadrantFilter!!.accentColor.copy(alpha = 0.4f)),
                            modifier = Modifier.clickable { selectedQuadrantFilter = null }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Filter: ${selectedQuadrantFilter!!.title}",
                                    fontSize = 11.sp,
                                    color = selectedQuadrantFilter!!.accentColor,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = selectedQuadrantFilter!!.accentColor, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (completedCount > 0 && onClearCompleted != null) {
                        IconButton(
                            onClick = { showConfirmClearDialog = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Archive, contentDescription = "Clear Completed", tint = AegisTextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }

                    if (onSeedTemplates != null) {
                        IconButton(
                            onClick = onSeedTemplates,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Seed Sample Tasks", tint = AegisCyanPrimary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Content Area: Matrix 2x2 Grid or Detailed Filtered List
            if (isGridView) {
                // 2x2 Quadrant Grid View
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuadrantMatrixCard(
                            quadrant = MatrixQuadrant.DO,
                            tasks = doTasks,
                            onUpdateStatus = onUpdateStatus,
                            onUpdatePriority = onUpdatePriority,
                            onDeleteTask = onDeleteTask,
                            modifier = Modifier.weight(1f)
                        )
                        QuadrantMatrixCard(
                            quadrant = MatrixQuadrant.SCHEDULE,
                            tasks = scheduleTasks,
                            onUpdateStatus = onUpdateStatus,
                            onUpdatePriority = onUpdatePriority,
                            onDeleteTask = onDeleteTask,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuadrantMatrixCard(
                            quadrant = MatrixQuadrant.DELEGATE,
                            tasks = delegateTasks,
                            onUpdateStatus = onUpdateStatus,
                            onUpdatePriority = onUpdatePriority,
                            onDeleteTask = onDeleteTask,
                            modifier = Modifier.weight(1f)
                        )
                        QuadrantMatrixCard(
                            quadrant = MatrixQuadrant.DELETE,
                            tasks = deleteTasks,
                            onUpdateStatus = onUpdateStatus,
                            onUpdatePriority = onUpdatePriority,
                            onDeleteTask = onDeleteTask,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                // List View
                if (displayTasks.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = AegisSurfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AegisOutline.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = AegisCyanPrimary.copy(alpha = 0.6f),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No tasks found in this view",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AegisTextPrimary
                            )
                            Text(
                                text = "Create a new task with Urgency and Importance flags to save it to Room.",
                                fontSize = 11.sp,
                                color = AegisTextSecondary
                            )
                            if (onSeedTemplates != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedButton(
                                    onClick = onSeedTemplates,
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AegisCyanPrimary)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Load Executive Templates", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        displayTasks.forEach { task ->
                            EisenhowerTaskListItem(
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

    // Create Task Dialog
    if (showCreateDialog) {
        CreateTaskDialog(
            onDismiss = { showCreateDialog = false },
            onSave = { title, desc, urgent, important, category ->
                onAddTask(title, desc, urgent, important, category)
                showCreateDialog = false
            }
        )
    }

    // Confirm Clear Dialog
    if (showConfirmClearDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmClearDialog = false },
            title = { Text("Clear Completed Tasks?", fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently remove all completed tasks from the local Room database (aegis_tasks).") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearCompleted?.invoke()
                        showConfirmClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AegisThreatRed)
                ) {
                    Text("Clear from Room")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmClearDialog = false }) {
                    Text("Cancel", color = AegisTextSecondary)
                }
            },
            containerColor = AegisSurfaceDark,
            titleContentColor = AegisTextPrimary,
            textContentColor = AegisTextSecondary
        )
    }
}

/**
 * Summary pill for quick filtering by Quadrant.
 */
@Composable
private fun QuadrantSummaryPill(
    quadrant: MatrixQuadrant,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .testTag("eisenhower_quadrant_chip_${quadrant.id}"),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) quadrant.accentColor.copy(alpha = 0.25f) else AegisSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) quadrant.accentColor else AegisOutline.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = quadrant.title.split(" ").first(),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) quadrant.accentColor else AegisTextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = count.toString(),
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = quadrant.accentColor
            )
        }
    }
}

/**
 * Single task item in list view.
 */
@Composable
private fun EisenhowerTaskListItem(
    task: AegisTask,
    onUpdateStatus: (task: AegisTask, newStatus: String) -> Unit,
    onUpdatePriority: (task: AegisTask, isUrgent: Boolean, isImportant: Boolean) -> Unit,
    onDeleteTask: (taskId: Long) -> Unit
) {
    val quadrant = MatrixQuadrant.fromTask(task)
    val isCompleted = task.status == "completed"
    var showPriorityMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_item_${task.id}"),
        shape = RoundedCornerShape(10.dp),
        color = if (isCompleted) AegisSurfaceVariant.copy(alpha = 0.4f) else AegisSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCompleted) AegisOutline.copy(alpha = 0.4f) else quadrant.accentColor.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { checked ->
                    onUpdateStatus(task, if (checked) "completed" else "pending")
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = AegisShieldGreen,
                    uncheckedColor = AegisOutline,
                    checkmarkColor = Color.Black
                ),
                modifier = Modifier.testTag("task_checkbox_${task.id}")
            )

            Spacer(modifier = Modifier.width(6.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = task.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) AegisTextSecondary else AegisTextPrimary,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Quadrant Indicator Badge with Move Dropdown
                    Box {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = quadrant.accentColor.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, quadrant.accentColor.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { showPriorityMenu = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = quadrant.badgeLabel,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = quadrant.accentColor
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Move Priority",
                                    tint = quadrant.accentColor,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showPriorityMenu,
                            onDismissRequest = { showPriorityMenu = false }
                        ) {
                            Text(
                                "Move to Quadrant in Room:",
                                fontSize = 10.sp,
                                color = AegisTextSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                            MatrixQuadrant.values().forEach { q ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(q.icon, contentDescription = null, tint = q.accentColor, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(q.title, fontSize = 12.sp, color = q.accentColor, fontWeight = FontWeight.Bold)
                                        }
                                    },
                                    onClick = {
                                        onUpdatePriority(task, q.isUrgent, q.isImportant)
                                        showPriorityMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = task.description,
                        fontSize = 11.sp,
                        color = AegisTextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Category Chip
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = AegisSurfaceDark
                        ) {
                            Text(
                                text = task.category.uppercase(),
                                fontSize = 8.sp,
                                color = AegisTextSecondary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }

                        // Urgency & Importance Badges
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (task.isUrgent) AegisThreatRed.copy(alpha = 0.15f) else AegisSurfaceDark
                        ) {
                            Text(
                                text = if (task.isUrgent) "⚡ URGENT" else "⌛ NOT URGENT",
                                fontSize = 8.sp,
                                color = if (task.isUrgent) AegisThreatRed else AegisTextSecondary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (task.isImportant) AegisCyanPrimary.copy(alpha = 0.15f) else AegisSurfaceDark
                        ) {
                            Text(
                                text = if (task.isImportant) "★ IMPORTANT" else "☆ NOT IMPORTANT",
                                fontSize = 8.sp,
                                color = if (task.isImportant) AegisCyanPrimary else AegisTextSecondary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Delete Button
                    IconButton(
                        onClick = { onDeleteTask(task.id) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete from Room",
                            tint = AegisTextSecondary.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Compact quadrant container in 2x2 grid view.
 */
@Composable
private fun QuadrantMatrixCard(
    quadrant: MatrixQuadrant,
    tasks: List<AegisTask>,
    onUpdateStatus: (task: AegisTask, newStatus: String) -> Unit,
    onUpdatePriority: (task: AegisTask, isUrgent: Boolean, isImportant: Boolean) -> Unit,
    onDeleteTask: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = AegisSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, quadrant.accentColor.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Quadrant Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = quadrant.icon,
                        contentDescription = null,
                        tint = quadrant.accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = quadrant.title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = quadrant.accentColor
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = quadrant.accentColor.copy(alpha = 0.2f),
                    modifier = Modifier.size(20.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = tasks.size.toString(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = quadrant.accentColor
                        )
                    }
                }
            }

            Text(
                text = quadrant.subtitle,
                fontSize = 9.sp,
                color = AegisTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No items in ${quadrant.badgeLabel}",
                        fontSize = 10.sp,
                        color = AegisTextSecondary.copy(alpha = 0.6f)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    tasks.take(4).forEach { task ->
                        val isDone = task.status == "completed"
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AegisSurfaceDark,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateStatus(task, if (isDone) "pending" else "completed")
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.FlashOn,
                                    contentDescription = null,
                                    tint = if (isDone) AegisShieldGreen else quadrant.accentColor,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = task.title,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isDone) AegisTextSecondary else AegisTextPrimary,
                                    textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                    if (tasks.size > 4) {
                        Text(
                            text = "+${tasks.size - 4} more in Room",
                            fontSize = 9.sp,
                            color = AegisTextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Modal dialog for creating and saving an Eisenhower task directly into Room.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CreateTaskDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, desc: String, urgent: Boolean, important: Boolean, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isUrgent by remember { mutableStateOf(true) }
    var isImportant by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("security") }

    val resolvedQuadrant = MatrixQuadrant.resolve(isUrgent, isImportant)
    val categories = listOf("security", "executive", "engineering", "sales", "personal")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = null, tint = AegisCyanPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Task to Room DB", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title *") },
                    placeholder = { Text("e.g. Audit Zero-Trust Keys") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("eisenhower_task_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AegisCyanPrimary,
                        unfocusedBorderColor = AegisOutline
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("Optional details, steps, or context") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("eisenhower_task_desc_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AegisCyanPrimary,
                        unfocusedBorderColor = AegisOutline
                    )
                )

                // Category Selection
                Text("Category", fontSize = 11.sp, color = AegisTextSecondary, fontWeight = FontWeight.Bold)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.uppercase(), fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AegisCyanPrimary.copy(alpha = 0.2f),
                                selectedLabelColor = AegisCyanPrimary
                            )
                        )
                    }
                }

                HorizontalDivider(color = AegisOutline.copy(alpha = 0.5f))

                // Urgent Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Is Urgent? (Time-sensitive)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Requires immediate attention", fontSize = 10.sp, color = AegisTextSecondary)
                    }
                    Switch(
                        checked = isUrgent,
                        onCheckedChange = { isUrgent = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AegisThreatRed,
                            checkedTrackColor = AegisThreatRed.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.testTag("eisenhower_urgent_switch")
                    )
                }

                // Important Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Is Important? (High-value)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Contributes to long-term impact", fontSize = 10.sp, color = AegisTextSecondary)
                    }
                    Switch(
                        checked = isImportant,
                        onCheckedChange = { isImportant = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AegisCyanPrimary,
                            checkedTrackColor = AegisCyanPrimary.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.testTag("eisenhower_important_switch")
                    )
                }

                // Dynamic Live Target Quadrant Preview
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = resolvedQuadrant.accentColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, resolvedQuadrant.accentColor.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = resolvedQuadrant.icon,
                            contentDescription = null,
                            tint = resolvedQuadrant.accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Target: ${resolvedQuadrant.title} (${resolvedQuadrant.badgeLabel})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = resolvedQuadrant.accentColor
                            )
                            Text(
                                text = resolvedQuadrant.actionText,
                                fontSize = 10.sp,
                                color = AegisTextSecondary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title.trim(), description.trim(), isUrgent, isImportant, selectedCategory)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = AegisCyanPrimary, contentColor = Color.Black),
                modifier = Modifier.testTag("eisenhower_save_task_btn")
            ) {
                Text("Save to Room DB", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = AegisTextSecondary)
            }
        },
        containerColor = AegisSurfaceDark,
        titleContentColor = AegisTextPrimary,
        textContentColor = AegisTextSecondary
    )
}
