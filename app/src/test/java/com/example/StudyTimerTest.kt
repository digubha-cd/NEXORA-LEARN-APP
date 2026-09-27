package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.model.Subject
import com.example.core.repository.StudyPlannerRepository
import com.example.core.timer.StudyTimerManager
import com.example.core.timer.StudyTimerPreset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit and Robolectric tests for the persistent Study Focus Timer component.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class StudyTimerTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        StudyPlannerRepository.resetForTesting()
        StudyTimerManager.initialize(context)
        StudyTimerManager.resetForTesting()
    }

    @Test
    fun testInitialTimerStateDefaults() {
        val state = StudyTimerManager.timerState.value
        assertEquals("accounts", state.subjectId)
        assertEquals("Elements of Accounts", state.subjectName)
        assertEquals(25 * 60L, state.totalDurationSeconds)
        assertEquals(25 * 60L, state.remainingSeconds)
        assertEquals(0L, state.elapsedSeconds)
        assertEquals(0f, state.progressFraction, 0.001f)
        assertFalse(state.isRunning)
        assertFalse(state.isPaused)
        assertFalse(state.isCompleted)
        assertEquals("25:00", state.formattedRemainingTime)
        assertEquals("00:00", state.formattedElapsedTime)
    }

    @Test
    fun testSelectOfficialSubjectUpdatesTimer() {
        val statSubject = Subject.OFFICIAL_SUBJECTS.find { it.id == "stat" }!!
        StudyTimerManager.selectSubject(statSubject)

        val state = StudyTimerManager.timerState.value
        assertEquals("stat", state.subjectId)
        assertEquals("Statistics", state.subjectName)

        val ecoSubject = Subject.OFFICIAL_SUBJECTS.find { it.id == "economics" }!!
        StudyTimerManager.selectSubject(ecoSubject)
        assertEquals("economics", StudyTimerManager.timerState.value.subjectId)
    }

    @Test
    fun testSetDurationMinutesUpdatesTimer() {
        StudyTimerManager.setDurationMinutes(45)
        val state = StudyTimerManager.timerState.value
        assertEquals(45 * 60L, state.totalDurationSeconds)
        assertEquals(45 * 60L, state.remainingSeconds)
        assertEquals("45:00", state.formattedRemainingTime)
        assertEquals("45m", state.formattedTotalDuration)

        StudyTimerManager.setDurationMinutes(StudyTimerPreset.EXAM_DRILL.minutes)
        assertEquals(60 * 60L, StudyTimerManager.timerState.value.totalDurationSeconds)
    }

    @Test
    fun testStartPauseResumeAndResetFlow() {
        // 1. Start timer
        StudyTimerManager.setDurationMinutes(25)
        StudyTimerManager.startTimer()

        assertTrue(StudyTimerManager.timerState.value.isRunning)
        assertFalse(StudyTimerManager.timerState.value.isPaused)

        // 2. Pause timer
        StudyTimerManager.pauseTimer()
        assertFalse(StudyTimerManager.timerState.value.isRunning)
        assertTrue(StudyTimerManager.timerState.value.isPaused)

        // 3. Resume timer
        StudyTimerManager.resumeTimer()
        assertTrue(StudyTimerManager.timerState.value.isRunning)
        assertFalse(StudyTimerManager.timerState.value.isPaused)

        // 4. Reset timer
        StudyTimerManager.resetTimer()
        assertFalse(StudyTimerManager.timerState.value.isRunning)
        assertFalse(StudyTimerManager.timerState.value.isPaused)
        assertEquals(25 * 60L, StudyTimerManager.timerState.value.remainingSeconds)
    }

    @Test
    fun testCompleteSessionEarlyLogsTaskAndIncrementsStats() {
        assertEquals(0, StudyPlannerRepository.tasks.value.size)
        assertEquals(0, StudyTimerManager.timerState.value.completedSessionsCount)

        StudyTimerManager.selectSubjectById("ba")
        StudyTimerManager.setDurationMinutes(30)
        StudyTimerManager.startTimer()

        // Complete session early
        StudyTimerManager.completeSessionEarly()

        val state = StudyTimerManager.timerState.value
        assertFalse(state.isRunning)
        assertTrue(state.isCompleted)
        assertEquals(1, state.completedSessionsCount)
        assertTrue("Total focused minutes should be >= 1", state.totalFocusedMinutes >= 1)

        // Verify task was added to StudyPlannerRepository
        assertEquals(1, StudyPlannerRepository.tasks.value.size)
        val task = StudyPlannerRepository.tasks.value.first()
        assertTrue(task.title.contains("Focus Session"))
        assertEquals("ba", task.subjectId)
    }
}
