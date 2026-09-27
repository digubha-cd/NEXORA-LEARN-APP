package com.example.core.timer

import androidx.compose.ui.graphics.Color
import com.example.core.model.Subject

/**
 * Focus Timer Preset Modes.
 */
enum class StudyTimerPreset(
    val title: String,
    val minutes: Int,
    val subtitle: String
) {
    POMODORO("25m Pomodoro", 25, "Standard Pomodoro focus block"),
    SHORT_BREAK("5m Break", 5, "Quick rest and recharge"),
    DEEP_FOCUS("45m Deep Focus", 45, "In-depth chapter mastery"),
    EXAM_DRILL("60m Exam Drill", 60, "Full board exam paper practice")
}

/**
 * State representation for the persistent study focus timer.
 */
data class StudyTimerState(
    val subjectId: String = "accounts",
    val subjectName: String = "Elements of Accounts",
    val totalDurationSeconds: Long = 25 * 60L,
    val remainingSeconds: Long = 25 * 60L,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isCompleted: Boolean = false,
    val startEpochMillis: Long? = null,
    val pauseEpochMillis: Long? = null,
    val completedSessionsCount: Int = 0,
    val totalFocusedMinutes: Int = 0
) {
    val elapsedSeconds: Long
        get() = (totalDurationSeconds - remainingSeconds).coerceAtLeast(0L)

    val progressFraction: Float
        get() = if (totalDurationSeconds > 0) {
            (elapsedSeconds.toFloat() / totalDurationSeconds.toFloat()).coerceIn(0f, 1f)
        } else 0f

    val remainingProgressFraction: Float
        get() = if (totalDurationSeconds > 0) {
            (remainingSeconds.toFloat() / totalDurationSeconds.toFloat()).coerceIn(0f, 1f)
        } else 0f

    val formattedRemainingTime: String
        get() {
            val mins = remainingSeconds / 60
            val secs = remainingSeconds % 60
            return String.format("%02d:%02d", mins, secs)
        }

    val formattedElapsedTime: String
        get() {
            val mins = elapsedSeconds / 60
            val secs = elapsedSeconds % 60
            return String.format("%02d:%02d", mins, secs)
        }

    val formattedTotalDuration: String
        get() {
            val mins = totalDurationSeconds / 60
            return "${mins}m"
        }
}

/**
 * Record of a completed focus study session.
 */
data class FocusSessionRecord(
    val id: String,
    val subjectId: String,
    val subjectName: String,
    val durationMinutes: Int,
    val timestampEpochMillis: Long,
    val formattedTimestamp: String
)
