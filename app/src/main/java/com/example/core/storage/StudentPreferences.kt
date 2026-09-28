package com.example.core.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.core.model.StudentProfile
import com.example.core.util.StudentIdGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Persistent local storage manager for student profile and preferences in NEXORA LEARN.
 * Stores the student's name and local identity without requiring any login or authentication.
 */
object StudentPreferences {

    private const val PREFS_NAME = "nexora_student_prefs"
    private const val KEY_USER_ID = "key_user_id"
    private const val KEY_STUDENT_ID = "key_student_id"
    private const val KEY_STUDENT_NAME = "key_student_name"
    private const val KEY_MEDIUM = "key_medium"
    private const val KEY_CLASS_LEVEL = "key_class_level"
    private const val KEY_TARGET_MARKS = "key_target_marks"
    private const val KEY_AVATAR_COLOR = "key_avatar_color"
    private const val KEY_IS_DARK_MODE = "key_is_dark_mode"
    private const val KEY_IDENTITY_SETUP_DONE = "key_identity_setup_done"
    private const val KEY_STUDENT_ID_LOCKED = "key_student_id_locked"

    private val _currentProfile = MutableStateFlow<StudentProfile?>(null)
    val currentProfile: StateFlow<StudentProfile?> = _currentProfile.asStateFlow()

    private val _isDarkModeFlow = MutableStateFlow(false)
    val isDarkModeFlow: StateFlow<Boolean> = _isDarkModeFlow.asStateFlow()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Check if Deep Navy (Night) study mode is enabled.
     */
    fun isDarkMode(context: Context): Boolean {
        val enabled = getPrefs(context).getBoolean(KEY_IS_DARK_MODE, false)
        _isDarkModeFlow.value = enabled
        return enabled
    }

    /**
     * Set Deep Navy (Night) study mode.
     */
    fun setDarkMode(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_IS_DARK_MODE, enabled).apply()
        _isDarkModeFlow.value = enabled
    }

    /**
     * Check if the one-time Student Identity Setup has been completed.
     */
    fun hasCompletedSetup(context: Context): Boolean {
        val prefs = getPrefs(context)
        val isExplicitlyCompleted = prefs.getBoolean(KEY_IDENTITY_SETUP_DONE, false)
        if (isExplicitlyCompleted) return true
        // Legacy fallback: if name and studentId were previously saved
        val name = prefs.getString(KEY_STUDENT_NAME, "")?.trim().orEmpty()
        val id = prefs.getString(KEY_STUDENT_ID, "")?.trim().orEmpty()
        return name.isNotBlank() && id.isNotBlank() && prefs.contains(KEY_STUDENT_ID)
    }

    /**
     * Check if Student ID is immutable and permanently locked.
     */
    fun isStudentIdImmutable(context: Context): Boolean {
        val prefs = getPrefs(context)
        return prefs.getBoolean(KEY_STUDENT_ID_LOCKED, false) || hasCompletedSetup(context)
    }

    /**
     * Complete the first-launch Student Identity Setup.
     * Atomically saves the student's name and permanent Student ID,
     * locks the Student ID so it becomes immutable, and marks setup as completed.
     */
    fun completeIdentitySetup(context: Context, name: String, studentId: String): StudentProfile {
        val trimmedName = name.trim()
        val normalizedId = StudentIdGenerator.normalizeStudentId(studentId) ?: studentId.trim().uppercase()
        val prefs = getPrefs(context)
        prefs.edit()
            .putString(KEY_STUDENT_NAME, trimmedName)
            .putString(KEY_STUDENT_ID, normalizedId)
            .putBoolean(KEY_IDENTITY_SETUP_DONE, true)
            .putBoolean(KEY_STUDENT_ID_LOCKED, true)
            .apply()

        val existing = getStudentProfile(context)
        val updatedProfile = existing.copy(
            studentName = if (trimmedName.isNotBlank()) trimmedName else "Class 12 Student",
            studentId = normalizedId,
            updatedAt = System.currentTimeMillis()
        )
        _currentProfile.value = updatedProfile
        return updatedProfile
    }

    /**
     * Check if a student name has already been entered and saved locally.
     */
    fun hasSavedName(context: Context): Boolean {
        val name = getPrefs(context).getString(KEY_STUDENT_NAME, "")?.trim().orEmpty()
        return name.isNotBlank()
    }

    /**
     * Get the student's locally saved name.
     */
    fun getSavedStudentName(context: Context): String {
        return getPrefs(context).getString(KEY_STUDENT_NAME, "")?.trim().orEmpty()
    }

    /**
     * Load or initialize the persistent local StudentProfile.
     */
    fun getStudentProfile(context: Context): StudentProfile {
        val prefs = getPrefs(context)
        var userId = prefs.getString(KEY_USER_ID, null)
        if (userId.isNullOrBlank()) {
            userId = "student_${UUID.randomUUID().toString().take(12)}"
            prefs.edit().putString(KEY_USER_ID, userId).apply()
        }

        var studentId = prefs.getString(KEY_STUDENT_ID, null)
        if (studentId.isNullOrBlank()) {
            studentId = StudentIdGenerator.generateCandidateId()
            prefs.edit().putString(KEY_STUDENT_ID, studentId).apply()
        }

        val name = prefs.getString(KEY_STUDENT_NAME, "")?.trim().orEmpty()
        val medium = prefs.getString(KEY_MEDIUM, "Gujarati Medium") ?: "Gujarati Medium"
        val classLevel = prefs.getString(KEY_CLASS_LEVEL, "Class 12 Commerce") ?: "Class 12 Commerce"
        val targetMarks = prefs.getString(KEY_TARGET_MARKS, "90+ Marks") ?: "90+ Marks"
        val avatarColor = prefs.getLong(KEY_AVATAR_COLOR, 0xFF00E5FF)

        val profile = StudentProfile(
            userId = userId,
            studentId = studentId,
            studentName = if (name.isNotBlank()) name else "Class 12 Student",
            medium = medium,
            classLevel = classLevel,
            targetMarks = targetMarks,
            avatarColorHex = avatarColor
        )

        _currentProfile.value = profile
        return profile
    }

    /**
     * Save the student's name on first setup or update.
     */
    fun saveStudentName(context: Context, name: String): StudentProfile {
        val trimmedName = name.trim()
        val prefs = getPrefs(context)
        prefs.edit().putString(KEY_STUDENT_NAME, trimmedName).apply()

        val updatedProfile = getStudentProfile(context).copy(
            studentName = if (trimmedName.isNotBlank()) trimmedName else "Class 12 Student",
            updatedAt = System.currentTimeMillis()
        )
        _currentProfile.value = updatedProfile
        return updatedProfile
    }

    /**
     * Save student with entered name and student ID.
     * If an existing profile is supplied, preserves user progress and updates credentials.
     */
    fun saveStudentProfileWithId(context: Context, name: String, studentId: String): StudentProfile {
        val trimmedName = name.trim()
        val normalizedId = StudentIdGenerator.normalizeStudentId(studentId) ?: studentId.trim().uppercase()
        val prefs = getPrefs(context)
        prefs.edit()
            .putString(KEY_STUDENT_NAME, trimmedName)
            .putString(KEY_STUDENT_ID, normalizedId)
            .putBoolean(KEY_IDENTITY_SETUP_DONE, true)
            .putBoolean(KEY_STUDENT_ID_LOCKED, true)
            .apply()

        val existing = getStudentProfile(context)
        val updatedProfile = existing.copy(
            studentName = if (trimmedName.isNotBlank()) trimmedName else "Class 12 Student",
            studentId = normalizedId,
            updatedAt = System.currentTimeMillis()
        )
        _currentProfile.value = updatedProfile
        return updatedProfile
    }

    /**
     * Update full student profile locally.
     */
    fun updateStudentProfile(context: Context, profile: StudentProfile) {
        val prefs = getPrefs(context)
        prefs.edit()
            .putString(KEY_USER_ID, profile.userId)
            .putString(KEY_STUDENT_ID, profile.studentId)
            .putString(KEY_STUDENT_NAME, profile.studentName)
            .putString(KEY_MEDIUM, profile.medium)
            .putString(KEY_CLASS_LEVEL, profile.classLevel)
            .putString(KEY_TARGET_MARKS, profile.targetMarks)
            .putLong(KEY_AVATAR_COLOR, profile.avatarColorHex)
            .apply()

        _currentProfile.value = profile
    }

    private const val KEY_SUBJECT_GOAL_PREFIX = "key_subject_goal_"

    fun getSubjectGoal(context: Context, subjectId: String, defaultGoal: Int = 90): Int {
        return getPrefs(context).getInt("$KEY_SUBJECT_GOAL_PREFIX$subjectId", defaultGoal)
    }

    fun setSubjectGoal(context: Context, subjectId: String, goal: Int) {
        getPrefs(context).edit().putInt("$KEY_SUBJECT_GOAL_PREFIX$subjectId", goal.coerceIn(10, 100)).apply()
    }
}
