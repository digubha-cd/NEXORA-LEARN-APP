package com.example.core.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.core.model.StudyMilestone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Repository for managing student-defined actionable study milestones and sub-tasks.
 */
object StudyMilestoneRepository {
    private const val PREFS_NAME = "nexora_study_milestones_prefs"
    private const val KEY_MILESTONES = "key_study_milestones_serialized"

    private var appContext: Context? = null

    private val _milestones = MutableStateFlow<List<StudyMilestone>>(emptyList())
    val milestones: StateFlow<List<StudyMilestone>> = _milestones.asStateFlow()

    fun initialize(context: Context) {
        appContext = context.applicationContext
        loadMilestones()
    }

    private fun getPrefs(): SharedPreferences? {
        return appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun loadMilestones() {
        val prefs = getPrefs() ?: return
        val serialized = prefs.getString(KEY_MILESTONES, null) ?: return
        if (serialized.isBlank()) return
        try {
            val list = serialized.split(";;;").mapNotNull { chunk ->
                val parts = chunk.split("|")
                if (parts.size >= 6) {
                    StudyMilestone(
                        id = parts[0],
                        subjectId = parts[1],
                        title = parts[2],
                        description = parts[3],
                        isCompleted = parts[4].toBoolean(),
                        createdAtEpochMillis = parts[5].toLongOrNull() ?: System.currentTimeMillis()
                    )
                } else null
            }
            _milestones.value = list
        } catch (_: Exception) {}
    }

    private fun persistMilestones() {
        val prefs = getPrefs() ?: return
        val serialized = _milestones.value.joinToString(";;;") { m ->
            "${m.id}|${m.subjectId}|${m.title}|${m.description}|${m.isCompleted}|${m.createdAtEpochMillis}"
        }
        prefs.edit().putString(KEY_MILESTONES, serialized).apply()
    }

    fun addMilestone(subjectId: String, title: String, description: String = "") {
        if (title.isBlank()) return
        val newMilestone = StudyMilestone(
            id = UUID.randomUUID().toString(),
            subjectId = subjectId,
            title = title.trim(),
            description = description.trim(),
            isCompleted = false,
            createdAtEpochMillis = System.currentTimeMillis()
        )
        _milestones.value = listOf(newMilestone) + _milestones.value
        persistMilestones()
    }

    fun toggleMilestone(id: String) {
        _milestones.value = _milestones.value.map { m ->
            if (m.id == id) m.copy(isCompleted = !m.isCompleted) else m
        }
        persistMilestones()
    }

    fun deleteMilestone(id: String) {
        _milestones.value = _milestones.value.filter { it.id != id }
        persistMilestones()
    }

    fun getMilestonesForSubject(subjectId: String, list: List<StudyMilestone> = _milestones.value): List<StudyMilestone> {
        return list.filter { it.subjectId == subjectId }
    }
}
