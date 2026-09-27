package com.example.core.model

/**
 * Virtual trophy / badge earned by students for achieving study milestones.
 */
data class StudyBadge(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val targetValue: Int,
    val currentProgress: Int,
    val isUnlocked: Boolean
)
