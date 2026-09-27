package com.example.core.util

import com.example.core.model.StudyBadge
import com.example.core.timer.FocusSessionRecord
import java.util.Calendar

object StudyBadgeProvider {
    fun calculateBadges(
        sessionHistory: List<FocusSessionRecord>,
        streakDays: Int,
        completedChaptersCount: Int,
        totalCompletedTasks: Int
    ): List<StudyBadge> {
        val totalMinutes = sessionHistory.sumOf { it.durationMinutes }
        val totalHours = totalMinutes / 60

        val hasEarlyBird = sessionHistory.any { session ->
            val calendar = Calendar.getInstance().apply { timeInMillis = session.timestampEpochMillis }
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            hour < 9
        }

        val hasNightOwl = sessionHistory.any { session ->
            val calendar = Calendar.getInstance().apply { timeInMillis = session.timestampEpochMillis }
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            hour >= 21
        }

        return listOf(
            StudyBadge(
                id = "badge_10_hours",
                title = "10 Hours Studied",
                description = "Accumulate 10+ hours of focused study time",
                iconEmoji = "⏳",
                targetValue = 10,
                currentProgress = minOf(10, totalHours),
                isUnlocked = totalHours >= 10
            ),
            StudyBadge(
                id = "badge_streak_5",
                title = "Streak Master",
                description = "Maintain a 5-day continuous study streak",
                iconEmoji = "🔥",
                targetValue = 5,
                currentProgress = minOf(5, streakDays),
                isUnlocked = streakDays >= 5
            ),
            StudyBadge(
                id = "badge_chapters_5",
                title = "Chapter Champion",
                description = "Complete 5 or more GSEB textbook chapters",
                iconEmoji = "📚",
                targetValue = 5,
                currentProgress = minOf(5, completedChaptersCount),
                isUnlocked = completedChaptersCount >= 5
            ),
            StudyBadge(
                id = "badge_early_bird",
                title = "Early Bird Learner",
                description = "Complete a focus session before 9:00 AM",
                iconEmoji = "🌅",
                targetValue = 1,
                currentProgress = if (hasEarlyBird) 1 else 0,
                isUnlocked = hasEarlyBird
            ),
            StudyBadge(
                id = "badge_night_owl",
                title = "Night Owl Scholar",
                description = "Complete a focus session after 9:00 PM",
                iconEmoji = "🌙",
                targetValue = 1,
                currentProgress = if (hasNightOwl) 1 else 0,
                isUnlocked = hasNightOwl
            ),
            StudyBadge(
                id = "badge_tasks_10",
                title = "Planner Pro",
                description = "Complete 10 custom planner tasks",
                iconEmoji = "🎯",
                targetValue = 10,
                currentProgress = minOf(10, totalCompletedTasks),
                isUnlocked = totalCompletedTasks >= 10
            )
        )
    }
}
