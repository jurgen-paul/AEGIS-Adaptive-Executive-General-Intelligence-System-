package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AegisDatabase
import com.example.data.EisenhowerTask
import com.example.data.EisenhowerTaskDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Robolectric unit tests validating Room database entity and DAO persistence
 * for the Eisenhower Priority Matrix ('Do', 'Schedule', 'Delegate', 'Eliminate').
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class EisenhowerRoomRobolectricTest {

    private lateinit var db: AegisDatabase
    private lateinit var dao: EisenhowerTaskDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AegisDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.eisenhowerTaskDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `test inserting and querying tasks across all 4 quadrants in Room`() = runBlocking {
        val tasks = listOf(
            EisenhowerTask(
                title = "Critical Security Hotfix",
                description = "Immediate key rotation",
                quadrant = EisenhowerTask.QUADRANT_DO,
                isUrgent = true,
                isImportant = true
            ),
            EisenhowerTask(
                title = "Executive Q4 Architecture Review",
                description = "Long-term scalability roadmap",
                quadrant = EisenhowerTask.QUADRANT_SCHEDULE,
                isUrgent = false,
                isImportant = true
            ),
            EisenhowerTask(
                title = "Order Team Workstation Upgrades",
                description = "Procurement approvals",
                quadrant = EisenhowerTask.QUADRANT_DELEGATE,
                isUrgent = true,
                isImportant = false
            ),
            EisenhowerTask(
                title = "Purge Stale Build Caches",
                description = "Clean up temporary logs",
                quadrant = EisenhowerTask.QUADRANT_ELIMINATE,
                isUrgent = false,
                isImportant = false
            )
        )

        dao.insertTasks(tasks)

        val all = dao.getAllTasks().first()
        assertEquals(4, all.size)

        val doTasks = dao.getTasksByQuadrant(EisenhowerTask.QUADRANT_DO).first()
        assertEquals(1, doTasks.size)
        assertEquals("Critical Security Hotfix", doTasks[0].title)

        val scheduleTasks = dao.getTasksByQuadrant(EisenhowerTask.QUADRANT_SCHEDULE).first()
        assertEquals(1, scheduleTasks.size)
        assertEquals("Executive Q4 Architecture Review", scheduleTasks[0].title)

        val delegateTasks = dao.getTasksByQuadrant(EisenhowerTask.QUADRANT_DELEGATE).first()
        assertEquals(1, delegateTasks.size)
        assertEquals("Order Team Workstation Upgrades", delegateTasks[0].title)

        val eliminateTasks = dao.getTasksByQuadrant(EisenhowerTask.QUADRANT_ELIMINATE).first()
        assertEquals(1, eliminateTasks.size)
        assertEquals("Purge Stale Build Caches", eliminateTasks[0].title)
    }

    @Test
    fun `test updating task status and moving between quadrants in Room`() = runBlocking {
        val task = EisenhowerTask(
            title = "Routine Task",
            quadrant = EisenhowerTask.QUADRANT_ELIMINATE,
            isUrgent = false,
            isImportant = false,
            status = "pending"
        )
        val id = dao.insertTask(task)
        assertTrue(id > 0)

        // Escalate task to DO quadrant
        val inserted = dao.getTaskById(id).first()
        assertNotNull(inserted)
        val escalated = inserted!!.copy(
            quadrant = EisenhowerTask.QUADRANT_DO,
            isUrgent = true,
            isImportant = true,
            status = "in_progress"
        )
        dao.updateTask(escalated)

        val updated = dao.getTaskById(id).first()
        assertNotNull(updated)
        assertEquals(EisenhowerTask.QUADRANT_DO, updated!!.quadrant)
        assertEquals("in_progress", updated.status)
        assertTrue(updated.isUrgent)
        assertTrue(updated.isImportant)

        // Delete task
        dao.deleteTaskById(id)
        val remaining = dao.getAllTasks().first()
        assertEquals(0, remaining.size)
    }
}
