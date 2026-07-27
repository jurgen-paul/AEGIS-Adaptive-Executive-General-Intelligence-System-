package com.example.data

enum class TaskDomain(val domainId: String, val displayName: String) {
    SECURITY("security", "Security Shield"),
    CONVERSATION("conversation", "Human Conversation"),
    MATH("math", "Math & Logic Solver"),
    ART("art", "Art & Visual Design"),
    SALES("sales", "Sales & Advisory"),
    HEALTH("health", "Health & Wellness"),
    ORGANIZER("organizer", "Executive Organizer");

    companion object {
        fun fromId(id: String): TaskDomain {
            return entries.firstOrNull { it.domainId == id } ?: CONVERSATION
        }
    }
}
