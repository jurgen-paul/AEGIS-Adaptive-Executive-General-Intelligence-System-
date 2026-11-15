package com.example

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.data.AegisTask
import com.example.ui.components.EisenhowerQuadrant
import com.example.ui.components.ExecutivePriorityGridComponent
import com.example.ui.theme.AegisTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Robolectric Compose UI tests verifying the 2x2 Executive Priority Grid component:
 * - Urgency & Importance axis rendering
 * - Task categorization across DO, SCHEDULE, DELEGATE, and ELIMINATE quadrants
 * - Moving tasks between quadrants via Relocate Mode and the 2x2 Move Dialog
 * - Quick-add & completion toggles
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExecutivePriorityGridTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `renders 2x2 grid axes and all four quadrants with categorized tasks`() {
        val sampleTasks = listOf(
            AegisTask(id = 1L, title = "Rotate Production Keys", isUrgent = true, isImportant = true),
            AegisTask(id = 2L, title = "Q4 Security Architecture", isUrgent = false, isImportant = true),
            AegisTask(id = 3L, title = "Vendor Renewal Ping", isUrgent = true, isImportant = false),
            AegisTask(id = 4L, title = "Archive Old Logs", isUrgent = false, isImportant = false)
        )

        composeTestRule.setContent {
            AegisTheme {
                ExecutivePriorityGridComponent(
                    tasks = sampleTasks,
                    onMoveTask = { _, _ -> },
                    onToggleComplete = { _, _ -> },
                    onQuickAddToQuadrant = {}
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("executive_priority_grid").assertExists()
        composeTestRule.onNodeWithTag("grid_axis_urgency_header").assertExists()
        composeTestRule.onNodeWithTag("grid_axis_important_header").assertExists()
        composeTestRule.onNodeWithTag("grid_axis_not_important_header").assertExists()

        composeTestRule.onNodeWithTag("grid_quadrant_do").assertExists()
        composeTestRule.onNodeWithTag("grid_quadrant_schedule").assertExists()
        composeTestRule.onNodeWithTag("grid_quadrant_delegate").assertExists()
        composeTestRule.onNodeWithTag("grid_quadrant_eliminate").assertExists()

        composeTestRule.onNodeWithText("Rotate Production Keys").assertExists()
        composeTestRule.onNodeWithText("Q4 Security Architecture").assertExists()
        composeTestRule.onNodeWithText("Vendor Renewal Ping").assertExists()
        composeTestRule.onNodeWithText("Archive Old Logs").assertExists()
    }

    @Test
    fun `relocate mode moves task from ELIMINATE to DO quadrant`() {
        var movedToQuadrant: EisenhowerQuadrant? = null

        composeTestRule.setContent {
            val tasksState = remember {
                mutableStateListOf(
                    AegisTask(
                        id = 10L,
                        title = "Review Incident Runbook",
                        isUrgent = false,
                        isImportant = false,
                        status = "pending"
                    )
                )
            }

            AegisTheme {
                ExecutivePriorityGridComponent(
                    tasks = tasksState.toList(),
                    onMoveTask = { task, targetQuad ->
                        movedToQuadrant = targetQuad
                        val idx = tasksState.indexOfFirst { it.id == task.id }
                        if (idx >= 0) {
                            tasksState[idx] = task.copy(
                                isUrgent = targetQuad.isUrgent,
                                isImportant = targetQuad.isImportant
                            )
                        }
                    },
                    onToggleComplete = { _, _ -> },
                    onQuickAddToQuadrant = {}
                )
            }
        }
        composeTestRule.waitForIdle()

        // Enter Relocate Mode for task #10
        composeTestRule.onNodeWithTag("grid_move_task_btn_10").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("relocate_mode_banner").assertIsDisplayed()
        composeTestRule.onNodeWithTag("grid_move_target_do").assertExists()

        // Tap target DO quadrant action
        composeTestRule.onNodeWithTag("grid_move_target_do").performClick()
        composeTestRule.waitForIdle()

        assertEquals(EisenhowerQuadrant.DO, movedToQuadrant)
        assertTrue(movedToQuadrant!!.isUrgent)
        assertTrue(movedToQuadrant!!.isImportant)
        composeTestRule.onNodeWithTag("relocate_mode_banner").assertDoesNotExist()
        composeTestRule.onNodeWithText("Review Incident Runbook").assertExists()
    }

    @Test
    fun `task dialog moves task from DO to SCHEDULE quadrant and supports completion toggle`() {
        var latestTask = AegisTask(
            id = 20L,
            title = "Executive Board Brief",
            description = "Prepare security posture metrics",
            isUrgent = true,
            isImportant = true,
            status = "pending"
        )

        composeTestRule.setContent {
            val tasksState = remember { mutableStateListOf(latestTask) }
            AegisTheme {
                ExecutivePriorityGridComponent(
                    tasks = tasksState.toList(),
                    onMoveTask = { task, targetQuad ->
                        val updated = task.copy(
                            isUrgent = targetQuad.isUrgent,
                            isImportant = targetQuad.isImportant
                        )
                        latestTask = updated
                        val idx = tasksState.indexOfFirst { it.id == task.id }
                        if (idx >= 0) tasksState[idx] = updated
                    },
                    onToggleComplete = { task, completed ->
                        val updated = task.copy(status = if (completed) "completed" else "pending")
                        latestTask = updated
                        val idx = tasksState.indexOfFirst { it.id == task.id }
                        if (idx >= 0) tasksState[idx] = updated
                    },
                    onQuickAddToQuadrant = {}
                )
            }
        }
        composeTestRule.waitForIdle()

        // Tap task card to open MoveTaskQuadrantDialog
        composeTestRule.onNodeWithTag("grid_task_card_20").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("move_dialog_quad_schedule").performClick()
        composeTestRule.waitForIdle()

        assertFalse(latestTask.isUrgent)
        assertTrue(latestTask.isImportant)

        // Toggle completion directly via card button
        composeTestRule.onNodeWithTag("grid_task_toggle_20").performClick()
        composeTestRule.waitForIdle()

        assertEquals("completed", latestTask.status)
    }

    @Test
    fun `quick add button triggers callback for respective quadrant`() {
        var requestedQuadrant: EisenhowerQuadrant? = null

        composeTestRule.setContent {
            AegisTheme {
                ExecutivePriorityGridComponent(
                    tasks = emptyList(),
                    onMoveTask = { _, _ -> },
                    onToggleComplete = { _, _ -> },
                    onQuickAddToQuadrant = { requestedQuadrant = it }
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("grid_quick_add_delegate").performClick()
        composeTestRule.waitForIdle()

        assertEquals(EisenhowerQuadrant.DELEGATE, requestedQuadrant)
    }
}
