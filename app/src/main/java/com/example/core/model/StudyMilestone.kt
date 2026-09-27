package com.example.core.model

/**
 * Actionable sub-task / study milestone for a specific subject.
 */
data class StudyMilestone(
    val id: String,
    val subjectId: String,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val createdAtEpochMillis: Long = System.currentTimeMillis()
)
