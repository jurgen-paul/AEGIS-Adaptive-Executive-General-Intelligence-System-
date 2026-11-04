package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Database Entity to persist task data in the Eisenhower Priority Matrix.
 * Guarantees that task items, their quadrant allocations, completion status,
 * and metadata persist safely across application restarts in local SQLite storage.
 *
 * Quadrants:
 * - "DO": Urgent & Important (Quadrant I)
 * - "SCHEDULE": Not Urgent & Important (Quadrant II)
 * - "DELEGATE": Urgent & Not Important (Quadrant III)
 * - "ELIMINATE": Not Urgent & Not Important (Quadrant IV)
 */
@Entity(tableName = "eisenhower_tasks")
data class EisenhowerTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val quadrant: String = QUADRANT_DO, // "DO", "SCHEDULE", "DELEGATE", "ELIMINATE"
    val isUrgent: Boolean = true,
    val isImportant: Boolean = true,
    val status: String = STATUS_PENDING, // "pending", "in_progress", "completed"
    val category: String = "general", // executive, security, engineering, health, general
    val dueDate: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val QUADRANT_DO = "DO"
        const val QUADRANT_SCHEDULE = "SCHEDULE"
        const val QUADRANT_DELEGATE = "DELEGATE"
        const val QUADRANT_ELIMINATE = "ELIMINATE"

        const val STATUS_PENDING = "pending"
        const val STATUS_IN_PROGRESS = "in_progress"
        const val STATUS_COMPLETED = "completed"

        /**
         * Resolves the quadrant from urgency and importance flags.
         */
        fun resolveQuadrant(isUrgent: Boolean, isImportant: Boolean): String {
            return when {
                isUrgent && isImportant -> QUADRANT_DO
                !isUrgent && isImportant -> QUADRANT_SCHEDULE
                isUrgent && !isImportant -> QUADRANT_DELEGATE
                else -> QUADRANT_ELIMINATE
            }
        }

        /**
         * Converts from an AegisTask to an EisenhowerTask.
         */
        fun fromAegisTask(aegisTask: AegisTask): EisenhowerTask {
            return EisenhowerTask(
                id = aegisTask.id,
                title = aegisTask.title,
                description = aegisTask.description,
                quadrant = resolveQuadrant(aegisTask.isUrgent, aegisTask.isImportant),
                isUrgent = aegisTask.isUrgent,
                isImportant = aegisTask.isImportant,
                status = aegisTask.status,
                category = aegisTask.category,
                dueDate = aegisTask.dueDate,
                createdAt = aegisTask.createdAt
            )
        }
    }

    /**
     * Converts to an AegisTask for backward compatibility.
     */
    fun toAegisTask(): AegisTask {
        return AegisTask(
            id = id,
            title = title,
            description = description,
            category = category,
            isUrgent = isUrgent,
            isImportant = isImportant,
            status = status,
            dueDate = dueDate,
            createdAt = createdAt
        )
    }
}
