package com.example

import com.example.data.AegisTask
import com.example.ui.components.MatrixQuadrant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying Eisenhower Priority Matrix categorization (Urgent/Important)
 * and Room entity models for task management.
 */
class EisenhowerTaskTest {

    @Test
    fun `test Q1 DO quadrant resolution when urgent and important`() {
        val task = AegisTask(
            id = 1,
            title = "Patch Zero-Day Vulnerability",
            isUrgent = true,
            isImportant = true
        )
        val quadrant = MatrixQuadrant.fromTask(task)
        assertEquals(MatrixQuadrant.DO, quadrant)
        assertEquals("DO FIRST", quadrant.title)
        assertTrue(quadrant.isUrgent)
        assertTrue(quadrant.isImportant)
    }

    @Test
    fun `test Q2 SCHEDULE quadrant resolution when not urgent and important`() {
        val task = AegisTask(
            id = 2,
            title = "Plan Q4 Architecture Roadmap",
            isUrgent = false,
            isImportant = true
        )
        val quadrant = MatrixQuadrant.fromTask(task)
        assertEquals(MatrixQuadrant.SCHEDULE, quadrant)
        assertEquals("SCHEDULE", quadrant.title)
        assertFalse(quadrant.isUrgent)
        assertTrue(quadrant.isImportant)
    }

    @Test
    fun `test Q3 DELEGATE quadrant resolution when urgent and not important`() {
        val task = AegisTask(
            id = 3,
            title = "Order Office Equipment",
            isUrgent = true,
            isImportant = false
        )
        val quadrant = MatrixQuadrant.fromTask(task)
        assertEquals(MatrixQuadrant.DELEGATE, quadrant)
        assertEquals("DELEGATE", quadrant.title)
        assertTrue(quadrant.isUrgent)
        assertFalse(quadrant.isImportant)
    }

    @Test
    fun `test Q4 DELETE quadrant resolution when not urgent and not important`() {
        val task = AegisTask(
            id = 4,
            title = "Clean Obsolete Test Logs",
            isUrgent = false,
            isImportant = false
        )
        val quadrant = MatrixQuadrant.fromTask(task)
        assertEquals(MatrixQuadrant.DELETE, quadrant)
        assertEquals("DELETE / ELIMINATE", quadrant.title)
        assertFalse(quadrant.isUrgent)
        assertFalse(quadrant.isImportant)
    }

    @Test
    fun `test task status and priority mutation`() {
        val initial = AegisTask(
            title = "Initial Task",
            isUrgent = false,
            isImportant = false,
            status = "pending"
        )
        assertEquals(MatrixQuadrant.DELETE, MatrixQuadrant.fromTask(initial))

        // Escalate to DO First
        val escalated = initial.copy(isUrgent = true, isImportant = true, status = "in_progress")
        assertEquals(MatrixQuadrant.DO, MatrixQuadrant.fromTask(escalated))
        assertEquals("in_progress", escalated.status)

        // Mark completed
        val completed = escalated.copy(status = "completed")
        assertEquals("completed", completed.status)
    }
}
