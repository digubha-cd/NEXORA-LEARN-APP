package com.example.core.model

import java.time.LocalDate

/**
 * Subject-wise completed tasks model for student-created To-Do items.
 */
data class SubjectProgressItem(
    val subjectId: String,
    val subjectName: String,
    val completedCount: Int,
    val pendingCount: Int,
    val totalCount: Int,
    val progressPercentage: Int
)

/**
 * Study Streak statistics.
 * Based on completing at least one student-created To-Do on a day.
 */
data class StudyStreak(
    val currentStreak: Int,
    val bestStreak: Int,
    val isCompletedToday: Boolean, // true if at least 1 task completed today
    val todayStatusText: String, // "Completed today" or "Pending today"
    val streakDays: List<LocalDate> = emptyList()
)

/**
 * Daily study activity for a specific day in the streak/weekly timeline.
 */
data class DayStudyActivity(
    val date: LocalDate,
    val dateStr: String,
    val dayLabel: String,
    val dayNumberFormatted: String,
    val completedCount: Int,
    val totalCount: Int,
    val durationMinutes: Int,
    val isStreakAchieved: Boolean,
    val isToday: Boolean
)

/**
 * Complete Progress summary based exclusively on the student's own To-Do activity.
 */
data class StudyProgressSummary(
    val completedCount: Int,
    val pendingCount: Int,
    val missedCount: Int,
    val totalCount: Int,
    val dailyPercentage: Int,
    val weeklyPercentage: Int,
    val overallPercentage: Int,
    val subjectProgressList: List<SubjectProgressItem>,
    val streak: StudyStreak
)
