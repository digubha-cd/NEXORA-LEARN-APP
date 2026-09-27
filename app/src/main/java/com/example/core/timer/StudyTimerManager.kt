package com.example.core.timer

import android.content.Context
import android.content.SharedPreferences
import com.example.core.model.Subject
import com.example.core.model.TaskPriority
import com.example.core.repository.StudyPlannerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Persistent Study Timer Manager.
 * Tracks focus study sessions for specific Class 12 Commerce subjects,
 * displaying time elapsed and remaining with circular progress.
 */
object StudyTimerManager {

    private const val PREFS_NAME = "nexora_study_timer_prefs"
    private const val KEY_SUBJECT_ID = "key_timer_subject_id"
    private const val KEY_SUBJECT_NAME = "key_timer_subject_name"
    private const val KEY_TOTAL_DURATION_SECS = "key_timer_total_duration_secs"
    private const val KEY_REMAINING_SECS = "key_timer_remaining_secs"
    private const val KEY_IS_RUNNING = "key_timer_is_running"
    private const val KEY_IS_PAUSED = "key_timer_is_paused"
    private const val KEY_START_EPOCH = "key_timer_start_epoch"
    private const val KEY_PAUSE_EPOCH = "key_timer_pause_epoch"
    private const val KEY_COMPLETED_SESSIONS = "key_timer_completed_sessions"
    private const val KEY_TOTAL_FOCUSED_MINS = "key_timer_total_focused_mins"
    private const val KEY_SESSION_HISTORY = "key_timer_session_history"

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var tickerJob: Job? = null
    private var appContext: Context? = null

    private val _timerState = MutableStateFlow(StudyTimerState())
    val timerState: StateFlow<StudyTimerState> = _timerState.asStateFlow()

    private val _sessionHistory = MutableStateFlow<List<FocusSessionRecord>>(emptyList())
    val sessionHistory: StateFlow<List<FocusSessionRecord>> = _sessionHistory.asStateFlow()

    fun initialize(context: Context) {
        appContext = context.applicationContext
        loadPersistedState()
        loadSessionHistory()
    }

    private fun recordCompletedSession(subjectId: String, subjectName: String, durationMins: Int) {
        val now = System.currentTimeMillis()
        val formatter = java.time.format.DateTimeFormatter.ofPattern("d MMM, hh:mm a", java.util.Locale.ENGLISH)
        val timeStr = java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(now), java.time.ZoneId.systemDefault()).format(formatter)
        val record = FocusSessionRecord(
            id = java.util.UUID.randomUUID().toString(),
            subjectId = subjectId,
            subjectName = subjectName,
            durationMinutes = durationMins,
            timestampEpochMillis = now,
            formattedTimestamp = timeStr
        )
        val updated = listOf(record) + _sessionHistory.value
        _sessionHistory.value = updated.take(50)
        persistSessionHistory()
    }

    private fun persistSessionHistory() {
        val prefs = getPrefs() ?: return
        val serialized = _sessionHistory.value.joinToString(";;;") { r ->
            "${r.id}|${r.subjectId}|${r.subjectName}|${r.durationMinutes}|${r.timestampEpochMillis}|${r.formattedTimestamp}"
        }
        prefs.edit().putString(KEY_SESSION_HISTORY, serialized).apply()
    }

    private fun loadSessionHistory() {
        val prefs = getPrefs() ?: return
        val serialized = prefs.getString(KEY_SESSION_HISTORY, null) ?: return
        if (serialized.isBlank()) return
        try {
            val list = serialized.split(";;;").mapNotNull { chunk ->
                val parts = chunk.split("|")
                if (parts.size >= 6) {
                    FocusSessionRecord(
                        id = parts[0],
                        subjectId = parts[1],
                        subjectName = parts[2],
                        durationMinutes = parts[3].toIntOrNull() ?: 25,
                        timestampEpochMillis = parts[4].toLongOrNull() ?: System.currentTimeMillis(),
                        formattedTimestamp = parts[5]
                    )
                } else null
            }
            _sessionHistory.value = list
        } catch (_: Exception) {}
    }

    private fun getPrefs(): SharedPreferences? {
        return appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun loadPersistedState() {
        val prefs = getPrefs() ?: return
        val subjectId = prefs.getString(KEY_SUBJECT_ID, "accounts") ?: "accounts"
        val subjectName = prefs.getString(KEY_SUBJECT_NAME, "Elements of Accounts") ?: "Elements of Accounts"
        val totalDuration = prefs.getLong(KEY_TOTAL_DURATION_SECS, 25 * 60L)
        var remaining = prefs.getLong(KEY_REMAINING_SECS, totalDuration)
        val isRunning = prefs.getBoolean(KEY_IS_RUNNING, false)
        val isPaused = prefs.getBoolean(KEY_IS_PAUSED, false)
        val startEpoch = if (prefs.contains(KEY_START_EPOCH)) prefs.getLong(KEY_START_EPOCH, 0L) else null
        val completedSessions = prefs.getInt(KEY_COMPLETED_SESSIONS, 0)
        val totalFocusedMins = prefs.getInt(KEY_TOTAL_FOCUSED_MINS, 0)

        // If it was running when app was closed/backgrounded, compute exact elapsed time
        if (isRunning && startEpoch != null && startEpoch > 0) {
            val now = System.currentTimeMillis()
            val secondsSinceStart = (now - startEpoch) / 1000L
            remaining = (totalDuration - secondsSinceStart).coerceAtLeast(0L)
        }

        val completed = remaining <= 0L && totalDuration > 0L

        _timerState.value = StudyTimerState(
            subjectId = subjectId,
            subjectName = subjectName,
            totalDurationSeconds = totalDuration,
            remainingSeconds = remaining,
            isRunning = isRunning && !completed,
            isPaused = isPaused,
            isCompleted = completed,
            startEpochMillis = startEpoch,
            completedSessionsCount = completedSessions,
            totalFocusedMinutes = totalFocusedMins
        )

        if (isRunning && !completed && !isPaused) {
            startTicker()
        }
    }

    private fun persistState() {
        val prefs = getPrefs() ?: return
        val state = _timerState.value
        prefs.edit()
            .putString(KEY_SUBJECT_ID, state.subjectId)
            .putString(KEY_SUBJECT_NAME, state.subjectName)
            .putLong(KEY_TOTAL_DURATION_SECS, state.totalDurationSeconds)
            .putLong(KEY_REMAINING_SECS, state.remainingSeconds)
            .putBoolean(KEY_IS_RUNNING, state.isRunning)
            .putBoolean(KEY_IS_PAUSED, state.isPaused)
            .apply {
                if (state.startEpochMillis != null) putLong(KEY_START_EPOCH, state.startEpochMillis) else remove(KEY_START_EPOCH)
                if (state.pauseEpochMillis != null) putLong(KEY_PAUSE_EPOCH, state.pauseEpochMillis) else remove(KEY_PAUSE_EPOCH)
            }
            .putInt(KEY_COMPLETED_SESSIONS, state.completedSessionsCount)
            .putInt(KEY_TOTAL_FOCUSED_MINS, state.totalFocusedMinutes)
            .apply()
    }

    fun selectSubject(subject: Subject) {
        if (_timerState.value.isRunning) return
        _timerState.update {
            it.copy(
                subjectId = subject.id,
                subjectName = subject.name
            )
        }
        persistState()
    }

    fun selectSubjectById(subjectId: String) {
        val subject = Subject.OFFICIAL_SUBJECTS.find { it.id == subjectId } ?: return
        selectSubject(subject)
    }

    fun setDurationMinutes(minutes: Int) {
        if (_timerState.value.isRunning) return
        val totalSecs = (minutes * 60L).coerceAtLeast(60L)
        _timerState.update {
            it.copy(
                totalDurationSeconds = totalSecs,
                remainingSeconds = totalSecs,
                isCompleted = false,
                isPaused = false,
                isRunning = false
            )
        }
        persistState()
    }

    fun startTimer() {
        val current = _timerState.value
        if (current.isCompleted) {
            // Reset if previously completed
            _timerState.update {
                it.copy(
                    remainingSeconds = it.totalDurationSeconds,
                    isCompleted = false
                )
            }
        }

        val startEpoch = System.currentTimeMillis() - (current.elapsedSeconds * 1000L)

        _timerState.update {
            it.copy(
                isRunning = true,
                isPaused = false,
                isCompleted = false,
                startEpochMillis = startEpoch,
                pauseEpochMillis = null
            )
        }
        persistState()
        startTicker()
    }

    fun pauseTimer() {
        tickerJob?.cancel()
        tickerJob = null
        _timerState.update {
            it.copy(
                isRunning = false,
                isPaused = true,
                pauseEpochMillis = System.currentTimeMillis()
            )
        }
        persistState()
    }

    fun resumeTimer() {
        startTimer()
    }

    fun resetTimer() {
        tickerJob?.cancel()
        tickerJob = null
        _timerState.update {
            it.copy(
                remainingSeconds = it.totalDurationSeconds,
                isRunning = false,
                isPaused = false,
                isCompleted = false,
                startEpochMillis = null,
                pauseEpochMillis = null
            )
        }
        persistState()
    }

    fun completeSessionEarly() {
        tickerJob?.cancel()
        tickerJob = null
        val current = _timerState.value
        val focusedMins = ((current.elapsedSeconds + 30) / 60).toInt().coerceAtLeast(1)

        // Log task in planner repository so it shows in real progress and streak
        try {
            StudyPlannerRepository.addTask(
                title = "Focus Session: ${current.subjectName}",
                description = "Completed $focusedMins min deep study focus session",
                subjectId = current.subjectId,
                scheduledDate = StudyPlannerRepository.TODAY,
                priority = TaskPriority.HIGH,
                durationMinutes = focusedMins
            )
        } catch (_: Exception) {}

        recordCompletedSession(current.subjectId, current.subjectName, focusedMins)

        _timerState.update {
            it.copy(
                remainingSeconds = 0L,
                isRunning = false,
                isPaused = false,
                isCompleted = true,
                completedSessionsCount = it.completedSessionsCount + 1,
                totalFocusedMinutes = it.totalFocusedMinutes + focusedMins
            )
        }
        persistState()
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (isActive) {
                delay(1000L)
                val current = _timerState.value
                if (!current.isRunning || current.isPaused) break

                val newRemaining = (current.remainingSeconds - 1L).coerceAtLeast(0L)
                if (newRemaining <= 0L) {
                    val focusedMins = (current.totalDurationSeconds / 60L).toInt()
                    // Automatically log completed session task
                    try {
                        StudyPlannerRepository.addTask(
                            title = "Focus Session: ${current.subjectName}",
                            description = "Completed full $focusedMins min focus session",
                            subjectId = current.subjectId,
                            scheduledDate = StudyPlannerRepository.TODAY,
                            priority = TaskPriority.HIGH,
                            durationMinutes = focusedMins
                        )
                    } catch (_: Exception) {}

                    recordCompletedSession(current.subjectId, current.subjectName, focusedMins)

                    _timerState.update {
                        it.copy(
                            remainingSeconds = 0L,
                            isRunning = false,
                            isPaused = false,
                            isCompleted = true,
                            completedSessionsCount = it.completedSessionsCount + 1,
                            totalFocusedMinutes = it.totalFocusedMinutes + focusedMins
                        )
                    }
                    persistState()
                    break
                } else {
                    _timerState.update { it.copy(remainingSeconds = newRemaining) }
                    persistState()
                }
            }
        }
    }

    fun resetForTesting() {
        tickerJob?.cancel()
        tickerJob = null
        _timerState.value = StudyTimerState()
        _sessionHistory.value = emptyList()
    }
}
