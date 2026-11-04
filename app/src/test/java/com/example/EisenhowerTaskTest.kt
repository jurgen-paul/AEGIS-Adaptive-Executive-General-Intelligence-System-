package com.example

import com.example.data.AegisTask
import com.example.data.EisenhowerTask
import com.example.ui.components.EisenhowerQuadrant
import com.example.ui.components.MatrixQuadrant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying Eisenhower Priority Matrix categorization into
 * 'Do', 'Schedule', 'Delegate', and 'Eliminate' quadrants,
 * as well as Room database entity models for persistent task management.
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

        val eisenQuadrant = EisenhowerQuadrant.fromTask(task)
        assertEquals(EisenhowerQuadrant.DO, eisenQuadrant)
        assertEquals("DO", eisenQuadrant.title)
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

        val eisenQuadrant = EisenhowerQuadrant.fromTask(task)
        assertEquals(EisenhowerQuadrant.SCHEDULE, eisenQuadrant)
        assertEquals("SCHEDULE", eisenQuadrant.title)
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

        val eisenQuadrant = EisenhowerQuadrant.fromTask(task)
        assertEquals(EisenhowerQuadrant.DELEGATE, eisenQuadrant)
        assertEquals("DELEGATE", eisenQuadrant.title)
    }

    @Test
    fun `test Q4 ELIMINATE quadrant resolution when not urgent and not important`() {
        val task = AegisTask(
            id = 4,
            title = "Clean Obsolete Test Logs",
            isUrgent = false,
            isImportant = false
        )
        val quadrant = MatrixQuadrant.fromTask(task)
        assertEquals(MatrixQuadrant.ELIMINATE, quadrant)
        assertEquals("ELIMINATE", quadrant.title)
        assertFalse(quadrant.isUrgent)
        assertFalse(quadrant.isImportant)

        val eisenQuadrant = EisenhowerQuadrant.fromTask(task)
        assertEquals(EisenhowerQuadrant.ELIMINATE, eisenQuadrant)
        assertEquals("ELIMINATE", eisenQuadrant.title)
    }

    @Test
    fun `test EisenhowerTask Room entity instantiation and quadrant derivation`() {
        val doTask = EisenhowerTask(
            id = 10,
            title = "Hotfix Keystore StrongBox Leak",
            description = "Rotate root certs immediately",
            quadrant = EisenhowerTask.QUADRANT_DO,
            isUrgent = true,
            isImportant = true,
            status = EisenhowerTask.STATUS_PENDING,
            category = "security"
        )
        assertEquals("DO", doTask.quadrant)
        assertEquals("Hotfix Keystore StrongBox Leak", doTask.title)
        assertEquals("security", doTask.category)
        assertTrue(doTask.isUrgent)
        assertTrue(doTask.isImportant)

        val eliminateTask = EisenhowerTask(
            id = 11,
            title = "Unused Temporary Debug Logs",
            description = "Stale export files",
            quadrant = EisenhowerTask.QUADRANT_ELIMINATE,
            isUrgent = false,
            isImportant = false,
            status = EisenhowerTask.STATUS_COMPLETED,
            category = "general"
        )
        assertEquals("ELIMINATE", eliminateTask.quadrant)
        assertFalse(eliminateTask.isUrgent)
        assertFalse(eliminateTask.isImportant)
        assertEquals(EisenhowerQuadrant.ELIMINATE, EisenhowerQuadrant.fromEisenhowerTask(eliminateTask))
        assertEquals(MatrixQuadrant.ELIMINATE, MatrixQuadrant.fromEisenhowerTask(eliminateTask))
    }

    @Test
    fun `test conversion between EisenhowerTask and AegisTask`() {
        val original = EisenhowerTask(
            id = 42,
            title = "Review Security Policy",
            description = "Zero-trust audit",
            quadrant = EisenhowerTask.QUADRANT_SCHEDULE,
            isUrgent = false,
            isImportant = true,
            status = "in_progress",
            category = "security"
        )
        val converted = original.toAegisTask()
        assertEquals(original.id, converted.id)
        assertEquals(original.title, converted.title)
        assertEquals(original.isUrgent, converted.isUrgent)
        assertEquals(original.isImportant, converted.isImportant)

        val reconverted = EisenhowerTask.fromAegisTask(converted)
        assertEquals(original.title, reconverted.title)
        assertEquals(EisenhowerTask.QUADRANT_SCHEDULE, reconverted.quadrant)
    }

    @Test
    fun `test task status and priority mutation`() {
        val initial = AegisTask(
            title = "Initial Task",
            isUrgent = false,
            isImportant = false,
            status = "pending"
        )
        assertEquals(MatrixQuadrant.ELIMINATE, MatrixQuadrant.fromTask(initial))

        // Escalate to DO First
        val escalated = initial.copy(isUrgent = true, isImportant = true, status = "in_progress")
        assertEquals(MatrixQuadrant.DO, MatrixQuadrant.fromTask(escalated))
        assertEquals("in_progress", escalated.status)

        // Mark completed
        val completed = escalated.copy(status = "completed")
        assertEquals("completed", completed.status)
    }
}
