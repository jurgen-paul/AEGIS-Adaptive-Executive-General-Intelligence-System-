package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegisTask
import com.example.data.EisenhowerTask
import com.example.ui.theme.QuadrantDelegateDark
import com.example.ui.theme.QuadrantDelegateLight
import com.example.ui.theme.QuadrantDoDark
import com.example.ui.theme.QuadrantDoLight
import com.example.ui.theme.QuadrantEliminateDark
import com.example.ui.theme.QuadrantEliminateLight
import com.example.ui.theme.QuadrantScheduleDark
import com.example.ui.theme.QuadrantScheduleLight

/**
 * Resolves high-contrast quadrant accent colors respecting the active MaterialTheme brightness.
 */
@Composable
fun rememberQuadrantAccentColor(quadrant: EisenhowerQuadrant): Color {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    return when (quadrant) {
        EisenhowerQuadrant.DO -> if (isDark) QuadrantDoDark else QuadrantDoLight
        EisenhowerQuadrant.SCHEDULE -> if (isDark) QuadrantScheduleDark else QuadrantScheduleLight
        EisenhowerQuadrant.DELEGATE -> if (isDark) QuadrantDelegateDark else QuadrantDelegateLight
        EisenhowerQuadrant.ELIMINATE -> if (isDark) QuadrantEliminateDark else QuadrantEliminateLight
    }
}

/**
 * Overload of [ExecutivePriorityGridComponent] accepting Room [EisenhowerTask] entities directly.
 */
@JvmName("ExecutivePriorityGridComponentForEisenhowerTask")
@Composable
fun ExecutivePriorityGridComponent(
    eisenhowerTasks: List<EisenhowerTask>,
    onMoveTask: (task: EisenhowerTask, targetQuadrant: EisenhowerQuadrant) -> Unit,
    onToggleComplete: (task: EisenhowerTask, completed: Boolean) -> Unit,
    onQuickAddToQuadrant: (quadrant: EisenhowerQuadrant) -> Unit,
    onDeleteTask: ((taskId: Long) -> Unit)? = null,
    selectedQuadrantFilter: EisenhowerQuadrant? = null,
    onSelectQuadrant: ((EisenhowerQuadrant?) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val mappedTasks = remember(eisenhowerTasks) { eisenhowerTasks.map { it.toAegisTask() } }
    ExecutivePriorityGridComponent(
        tasks = mappedTasks,
        onMoveTask = { aegisTask, targetQuadrant ->
            eisenhowerTasks.find { it.id == aegisTask.id }?.let { match ->
                onMoveTask(match, targetQuadrant)
            }
        },
        onToggleComplete = { aegisTask, completed ->
            eisenhowerTasks.find { it.id == aegisTask.id }?.let { match ->
                onToggleComplete(match, completed)
            }
        },
        onQuickAddToQuadrant = onQuickAddToQuadrant,
        onDeleteTask = onDeleteTask,
        selectedQuadrantFilter = selectedQuadrantFilter,
        onSelectQuadrant = onSelectQuadrant,
        modifier = modifier
    )
}

/**
 * Interactive 2x2 Eisenhower Priority Matrix Grid UI Component for Executive Task Management.
 *
 * Visualizes tasks across two perpendicular axes:
 * - Horizontal axis: URGENT vs NOT URGENT
 * - Vertical axis: IMPORTANT vs NOT IMPORTANT
 *
 * Supports two seamless workflows to move tasks between quadrants:
 * 1. Relocate Mode: Tap the swap icon on any task to highlight target quadrants, then tap a destination quadrant.
 * 2. Quick-Move Sheet Dialog: Tap any task card to inspect it and reassign it via a visual 2x2 picker.
 */
@Composable
fun ExecutivePriorityGridComponent(
    tasks: List<AegisTask>,
    onMoveTask: (task: AegisTask, targetQuadrant: EisenhowerQuadrant) -> Unit,
    onToggleComplete: (task: AegisTask, completed: Boolean) -> Unit,
    onQuickAddToQuadrant: (quadrant: EisenhowerQuadrant) -> Unit,
    onDeleteTask: ((taskId: Long) -> Unit)? = null,
    selectedQuadrantFilter: EisenhowerQuadrant? = null,
    onSelectQuadrant: ((EisenhowerQuadrant?) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val doTasks = remember(tasks) { tasks.filter { it.isUrgent && it.isImportant } }
    val scheduleTasks = remember(tasks) { tasks.filter { !it.isUrgent && it.isImportant } }
    val delegateTasks = remember(tasks) { tasks.filter { it.isUrgent && !it.isImportant } }
    val eliminateTasks = remember(tasks) { tasks.filter { !it.isUrgent && !it.isImportant } }

    var relocatingTask by remember { mutableStateOf<AegisTask?>(null) }
    var inspectedTask by remember { mutableStateOf<AegisTask?>(null) }
    val expandedQuadrants = remember {
        mutableStateMapOf(
            EisenhowerQuadrant.DO to false,
            EisenhowerQuadrant.SCHEDULE to false,
            EisenhowerQuadrant.DELEGATE to false,
            EisenhowerQuadrant.ELIMINATE to false
        )
    }

    // Clear stale relocatingTask if it gets deleted externally
    val activeRelocatingTask = relocatingTask?.let { target ->
        tasks.find { it.id == target.id } ?: target
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
            .testTag("executive_priority_grid"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Relocate Mode Action Banner
        AnimatedVisibility(
            visible = activeRelocatingTask != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            activeRelocatingTask?.let { moving ->
                val currentQuad = EisenhowerQuadrant.fromTask(moving)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("relocate_mode_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "Moving \"${moving.title}\"",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "From ${currentQuad.title} • Tap any destination quadrant below",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                )
                            }
                        }

                        IconButton(
                            onClick = { relocatingTask = null },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("cancel_relocate_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel Move",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }

        // Top Horizontal Axis Header: URGENT | NOT URGENT
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("grid_axis_urgency_header"),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AxisBadgeHeader(
                label = "URGENT",
                icon = Icons.Default.Bolt,
                accentColor = rememberQuadrantAccentColor(EisenhowerQuadrant.DO),
                modifier = Modifier.weight(1f)
            )
            AxisBadgeHeader(
                label = "NOT URGENT",
                icon = Icons.Default.Schedule,
                accentColor = rememberQuadrantAccentColor(EisenhowerQuadrant.SCHEDULE),
                modifier = Modifier.weight(1f)
            )
        }

        // Vertical Row 1 Section Header: IMPORTANT
        ImportanceAxisStrip(
            label = "IMPORTANT • High Strategic Impact",
            isImportant = true,
            modifier = Modifier.testTag("grid_axis_important_header")
        )

        // Row 1 of 2x2 Matrix: DO (Q1) | SCHEDULE (Q2)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExecutiveQuadrantCell(
                quadrant = EisenhowerQuadrant.DO,
                quadrantCode = "Q1",
                tasks = doTasks,
                isExpanded = expandedQuadrants[EisenhowerQuadrant.DO] == true,
                onToggleExpand = {
                    expandedQuadrants[EisenhowerQuadrant.DO] =
                        !(expandedQuadrants[EisenhowerQuadrant.DO] ?: false)
                },
                isSelectedFilter = selectedQuadrantFilter == EisenhowerQuadrant.DO,
                relocatingTask = activeRelocatingTask,
                onHeaderClick = {
                    if (activeRelocatingTask != null) {
                        if (EisenhowerQuadrant.fromTask(activeRelocatingTask) != EisenhowerQuadrant.DO) {
                            onMoveTask(activeRelocatingTask, EisenhowerQuadrant.DO)
                        }
                        relocatingTask = null
                    } else {
                        onSelectQuadrant?.invoke(
                            if (selectedQuadrantFilter == EisenhowerQuadrant.DO) null else EisenhowerQuadrant.DO
                        )
                    }
                },
                onQuickAdd = { onQuickAddToQuadrant(EisenhowerQuadrant.DO) },
                onStartRelocate = { task ->
                    relocatingTask = if (relocatingTask?.id == task.id) null else task
                },
                onInspectTask = { task -> inspectedTask = task },
                onToggleComplete = onToggleComplete,
                onDropHere = {
                    activeRelocatingTask?.let { moving ->
                        onMoveTask(moving, EisenhowerQuadrant.DO)
                        relocatingTask = null
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )

            ExecutiveQuadrantCell(
                quadrant = EisenhowerQuadrant.SCHEDULE,
                quadrantCode = "Q2",
                tasks = scheduleTasks,
                isExpanded = expandedQuadrants[EisenhowerQuadrant.SCHEDULE] == true,
                onToggleExpand = {
                    expandedQuadrants[EisenhowerQuadrant.SCHEDULE] =
                        !(expandedQuadrants[EisenhowerQuadrant.SCHEDULE] ?: false)
                },
                isSelectedFilter = selectedQuadrantFilter == EisenhowerQuadrant.SCHEDULE,
                relocatingTask = activeRelocatingTask,
                onHeaderClick = {
                    if (activeRelocatingTask != null) {
                        if (EisenhowerQuadrant.fromTask(activeRelocatingTask) != EisenhowerQuadrant.SCHEDULE) {
                            onMoveTask(activeRelocatingTask, EisenhowerQuadrant.SCHEDULE)
                        }
                        relocatingTask = null
                    } else {
                        onSelectQuadrant?.invoke(
                            if (selectedQuadrantFilter == EisenhowerQuadrant.SCHEDULE) null else EisenhowerQuadrant.SCHEDULE
                        )
                    }
                },
                onQuickAdd = { onQuickAddToQuadrant(EisenhowerQuadrant.SCHEDULE) },
                onStartRelocate = { task ->
                    relocatingTask = if (relocatingTask?.id == task.id) null else task
                },
                onInspectTask = { task -> inspectedTask = task },
                onToggleComplete = onToggleComplete,
                onDropHere = {
                    activeRelocatingTask?.let { moving ->
                        onMoveTask(moving, EisenhowerQuadrant.SCHEDULE)
                        relocatingTask = null
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }

        // Vertical Row 2 Section Header: NOT IMPORTANT
        ImportanceAxisStrip(
            label = "NOT IMPORTANT • Operational / Low Leverage",
            isImportant = false,
            modifier = Modifier.testTag("grid_axis_not_important_header")
        )

        // Row 2 of 2x2 Matrix: DELEGATE (Q3) | ELIMINATE (Q4)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExecutiveQuadrantCell(
                quadrant = EisenhowerQuadrant.DELEGATE,
                quadrantCode = "Q3",
                tasks = delegateTasks,
                isExpanded = expandedQuadrants[EisenhowerQuadrant.DELEGATE] == true,
                onToggleExpand = {
                    expandedQuadrants[EisenhowerQuadrant.DELEGATE] =
                        !(expandedQuadrants[EisenhowerQuadrant.DELEGATE] ?: false)
                },
                isSelectedFilter = selectedQuadrantFilter == EisenhowerQuadrant.DELEGATE,
                relocatingTask = activeRelocatingTask,
                onHeaderClick = {
                    if (activeRelocatingTask != null) {
                        if (EisenhowerQuadrant.fromTask(activeRelocatingTask) != EisenhowerQuadrant.DELEGATE) {
                            onMoveTask(activeRelocatingTask, EisenhowerQuadrant.DELEGATE)
                        }
                        relocatingTask = null
                    } else {
                        onSelectQuadrant?.invoke(
                            if (selectedQuadrantFilter == EisenhowerQuadrant.DELEGATE) null else EisenhowerQuadrant.DELEGATE
                        )
                    }
                },
                onQuickAdd = { onQuickAddToQuadrant(EisenhowerQuadrant.DELEGATE) },
                onStartRelocate = { task ->
                    relocatingTask = if (relocatingTask?.id == task.id) null else task
                },
                onInspectTask = { task -> inspectedTask = task },
                onToggleComplete = onToggleComplete,
                onDropHere = {
                    activeRelocatingTask?.let { moving ->
                        onMoveTask(moving, EisenhowerQuadrant.DELEGATE)
                        relocatingTask = null
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )

            ExecutiveQuadrantCell(
                quadrant = EisenhowerQuadrant.ELIMINATE,
                quadrantCode = "Q4",
                tasks = eliminateTasks,
                isExpanded = expandedQuadrants[EisenhowerQuadrant.ELIMINATE] == true,
                onToggleExpand = {
                    expandedQuadrants[EisenhowerQuadrant.ELIMINATE] =
                        !(expandedQuadrants[EisenhowerQuadrant.ELIMINATE] ?: false)
                },
                isSelectedFilter = selectedQuadrantFilter == EisenhowerQuadrant.ELIMINATE,
                relocatingTask = activeRelocatingTask,
                onHeaderClick = {
                    if (activeRelocatingTask != null) {
                        if (EisenhowerQuadrant.fromTask(activeRelocatingTask) != EisenhowerQuadrant.ELIMINATE) {
                            onMoveTask(activeRelocatingTask, EisenhowerQuadrant.ELIMINATE)
                        }
                        relocatingTask = null
                    } else {
                        onSelectQuadrant?.invoke(
                            if (selectedQuadrantFilter == EisenhowerQuadrant.ELIMINATE) null else EisenhowerQuadrant.ELIMINATE
                        )
                    }
                },
                onQuickAdd = { onQuickAddToQuadrant(EisenhowerQuadrant.ELIMINATE) },
                onStartRelocate = { task ->
                    relocatingTask = if (relocatingTask?.id == task.id) null else task
                },
                onInspectTask = { task -> inspectedTask = task },
                onToggleComplete = onToggleComplete,
                onDropHere = {
                    activeRelocatingTask?.let { moving ->
                        onMoveTask(moving, EisenhowerQuadrant.ELIMINATE)
                        relocatingTask = null
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }

    // Visual 2x2 Task Inspection & Quadrant Move Dialog
    val activeInspectedTask = inspectedTask?.let { t -> tasks.find { it.id == t.id } ?: t }
    if (activeInspectedTask != null) {
        MoveTaskQuadrantDialog(
            task = activeInspectedTask,
            onDismiss = { inspectedTask = null },
            onMoveToQuadrant = { targetQuad ->
                onMoveTask(activeInspectedTask, targetQuad)
                inspectedTask = null
            },
            onToggleComplete = { completed ->
                onToggleComplete(activeInspectedTask, completed)
                inspectedTask = null
            },
            onDelete = onDeleteTask?.let { deleteFn ->
                {
                    deleteFn(activeInspectedTask.id)
                    inspectedTask = null
                }
            }
        )
    }
}

@Composable
private fun AxisBadgeHeader(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = accentColor.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.8.sp,
                color = accentColor
            )
        }
    }
}

@Composable
private fun ImportanceAxisStrip(
    label: String,
    isImportant: Boolean,
    modifier: Modifier = Modifier
) {
    val accent = if (isImportant) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = if (isImportant) Icons.Default.Star else Icons.Default.StarOutline,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.7.sp,
            color = accent
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = accent.copy(alpha = 0.25f)
        )
    }
}

@Composable
private fun ExecutiveQuadrantCell(
    quadrant: EisenhowerQuadrant,
    quadrantCode: String,
    tasks: List<AegisTask>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    isSelectedFilter: Boolean,
    relocatingTask: AegisTask?,
    onHeaderClick: () -> Unit,
    onQuickAdd: () -> Unit,
    onStartRelocate: (AegisTask) -> Unit,
    onInspectTask: (AegisTask) -> Unit,
    onToggleComplete: (AegisTask, Boolean) -> Unit,
    onDropHere: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = rememberQuadrantAccentColor(quadrant)
    val isRelocating = relocatingTask != null
    val sourceQuadrant = relocatingTask?.let { EisenhowerQuadrant.fromTask(it) }
    val isDropTarget = isRelocating && sourceQuadrant != quadrant

    val completedCount = tasks.count { it.status == "completed" }
    val activeCount = tasks.size - completedCount
    val progress = if (tasks.isNotEmpty()) completedCount.toFloat() / tasks.size else 0f

    val containerColor by animateColorAsState(
        targetValue = when {
            isDropTarget -> accentColor.copy(alpha = 0.14f)
            isSelectedFilter -> accentColor.copy(alpha = 0.10f)
            else -> MaterialTheme.colorScheme.surface
        },
        label = "quadrant_bg_${quadrant.tag}"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isDropTarget -> accentColor
            isSelectedFilter -> accentColor
            else -> accentColor.copy(alpha = 0.35f)
        },
        label = "quadrant_border_${quadrant.tag}"
    )

    Card(
        modifier = modifier
            .defaultMinSize(minHeight = 165.dp)
            .testTag("grid_quadrant_${quadrant.tag}")
            .clickable(enabled = isDropTarget || true) {
                if (isDropTarget) onDropHere() else onHeaderClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(
            width = if (isDropTarget || isSelectedFilter) 2.dp else 1.dp,
            color = borderColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDropTarget) 3.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Cell Header: Q-Badge + Title + Count + Quick-Add
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = accentColor.copy(alpha = 0.18f)
                    ) {
                        Text(
                            text = quadrantCode,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = accentColor,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = quadrant.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = accentColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "$activeCount active / ${tasks.size}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                // Quick-add icon with accessible minimum touch target
                IconButton(
                    onClick = onQuickAdd,
                    modifier = Modifier
                        .size(36.dp)
                        .minimumInteractiveComponentSize()
                        .testTag("grid_quick_add_${quadrant.tag}")
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add task to ${quadrant.title}",
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Progress bar reflecting quadrant completion
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = accentColor,
                trackColor = accentColor.copy(alpha = 0.15f)
            )

            // Prominent Drop Action CTA when user is moving a task from another quadrant
            if (isDropTarget) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = accentColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onDropHere() }
                        .testTag("grid_move_target_${quadrant.tag}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Move to ${quadrant.title}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }

            // Task list or Empty placeholder
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isDropTarget) "Tap to place here" else "${quadrant.actionLabel}\nTap + to add",
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                val visibleLimit = if (isExpanded) tasks.size else 3
                val visibleTasks = tasks.take(visibleLimit)

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    visibleTasks.forEach { task ->
                        val isBeingMoved = relocatingTask?.id == task.id
                        ExecutiveGridTaskTile(
                            task = task,
                            accentColor = accentColor,
                            isBeingMoved = isBeingMoved,
                            onInspect = { onInspectTask(task) },
                            onStartRelocate = { onStartRelocate(task) },
                            onToggleComplete = { completed -> onToggleComplete(task, completed) }
                        )
                    }

                    if (tasks.size > 3) {
                        TextButton(
                            onClick = onToggleExpand,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .testTag("grid_expand_${quadrant.tag}")
                        ) {
                            Text(
                                text = if (isExpanded) "Show fewer" else "+ ${tasks.size - 3} more tasks",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExecutiveGridTaskTile(
    task: AegisTask,
    accentColor: Color,
    isBeingMoved: Boolean,
    onInspect: () -> Unit,
    onStartRelocate: () -> Unit,
    onToggleComplete: (Boolean) -> Unit
) {
    val isCompleted = task.status == "completed"
    val tileContainerColor = when {
        isBeingMoved -> MaterialTheme.colorScheme.primaryContainer
        isCompleted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = tileContainerColor,
        border = BorderStroke(
            width = if (isBeingMoved) 1.5.dp else 1.dp,
            color = if (isBeingMoved) MaterialTheme.colorScheme.primary else accentColor.copy(alpha = 0.25f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onInspect() }
            .testTag("grid_task_card_${task.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Completion checkbox icon with clear semantics
            IconButton(
                onClick = { onToggleComplete(!isCompleted) },
                modifier = Modifier
                    .size(28.dp)
                    .semantics(mergeDescendants = true) {}
                    .testTag("grid_task_toggle_${task.id}")
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (isCompleted) "Mark ${task.title} pending" else "Mark ${task.title} completed",
                    tint = if (isCompleted) MaterialTheme.colorScheme.secondary else accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 14.sp
                )
                if (task.category.isNotBlank()) {
                    Text(
                        text = task.category.uppercase(),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            // Move-to-another-quadrant button
            IconButton(
                onClick = onStartRelocate,
                modifier = Modifier
                    .size(28.dp)
                    .testTag("grid_move_task_btn_${task.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Move ${task.title} to another quadrant",
                    tint = if (isBeingMoved) MaterialTheme.colorScheme.primary else accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Interactive Dialog presenting full task details and a visual 2x2 Quadrant Picker
 * for one-tap movement between quadrants.
 */
@Composable
fun MoveTaskQuadrantDialog(
    task: AegisTask,
    onDismiss: () -> Unit,
    onMoveToQuadrant: (EisenhowerQuadrant) -> Unit,
    onToggleComplete: (Boolean) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val currentQuadrant = EisenhowerQuadrant.fromTask(task)
    val isCompleted = task.status == "completed"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Executive Task Routing",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = task.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }

                // Current status row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onToggleComplete(!isCompleted) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("move_dialog_toggle_complete")
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isCompleted) "Reopen" else "Complete",
                            fontSize = 12.sp
                        )
                    }

                    if (onDelete != null) {
                        OutlinedButton(
                            onClick = onDelete,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.testTag("move_dialog_delete_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Task",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "Move to Quadrant:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Embedded 2x2 Quadrant Selector
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuadrantMoveOptionButton(
                            quadrant = EisenhowerQuadrant.DO,
                            isCurrent = currentQuadrant == EisenhowerQuadrant.DO,
                            onClick = { onMoveToQuadrant(EisenhowerQuadrant.DO) },
                            modifier = Modifier.weight(1f)
                        )
                        QuadrantMoveOptionButton(
                            quadrant = EisenhowerQuadrant.SCHEDULE,
                            isCurrent = currentQuadrant == EisenhowerQuadrant.SCHEDULE,
                            onClick = { onMoveToQuadrant(EisenhowerQuadrant.SCHEDULE) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuadrantMoveOptionButton(
                            quadrant = EisenhowerQuadrant.DELEGATE,
                            isCurrent = currentQuadrant == EisenhowerQuadrant.DELEGATE,
                            onClick = { onMoveToQuadrant(EisenhowerQuadrant.DELEGATE) },
                            modifier = Modifier.weight(1f)
                        )
                        QuadrantMoveOptionButton(
                            quadrant = EisenhowerQuadrant.ELIMINATE,
                            isCurrent = currentQuadrant == EisenhowerQuadrant.ELIMINATE,
                            onClick = { onMoveToQuadrant(EisenhowerQuadrant.ELIMINATE) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun QuadrantMoveOptionButton(
    quadrant: EisenhowerQuadrant,
    isCurrent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = rememberQuadrantAccentColor(quadrant)
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isCurrent) accent.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        border = BorderStroke(
            width = if (isCurrent) 2.dp else 1.dp,
            color = if (isCurrent) accent else accent.copy(alpha = 0.35f)
        ),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("move_dialog_quad_${quadrant.tag}")
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = quadrant.icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(16.dp)
                )
                if (isCurrent) {
                    Text(
                        text = "CURRENT",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accent
                    )
                }
            }
            Text(
                text = quadrant.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accent
            )
            Text(
                text = quadrant.subtitle,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 12.sp
            )
        }
    }
}
