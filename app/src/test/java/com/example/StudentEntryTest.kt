package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.storage.StudentPreferences
import com.example.core.util.StudentIdGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class StudentEntryTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("nexora_student_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun testStudentIdValidation() {
        assertTrue(StudentIdGenerator.isValidStudentId("NX-7K4P92"))
        assertTrue(StudentIdGenerator.isValidStudentId("nx-7k4p92"))
        assertTrue(StudentIdGenerator.isValidStudentId("7K4P92"))
        assertFalse(StudentIdGenerator.isValidStudentId("NX-123"))
        assertFalse(StudentIdGenerator.isValidStudentId(""))
        assertFalse(StudentIdGenerator.isValidStudentId(null))

        assertEquals("NX-7K4P92", StudentIdGenerator.normalizeStudentId("NX-7K4P92"))
        assertEquals("NX-7K4P92", StudentIdGenerator.normalizeStudentId("nx-7k4p92"))
        assertEquals("NX-7K4P92", StudentIdGenerator.normalizeStudentId("7k4p92"))
    }

    @Test
    fun testSaveStudentProfileWithId() {
        val name = "Aarav Patel"
        val id = "NX-8M3X9K"

        val profile = StudentPreferences.saveStudentProfileWithId(context, name, id)

        assertTrue(StudentPreferences.hasSavedName(context))
        assertEquals(name, profile.studentName)
        assertEquals(id, profile.studentId)
        assertEquals(name, StudentPreferences.getSavedStudentName(context))

        val loaded = StudentPreferences.getStudentProfile(context)
        assertEquals(name, loaded.studentName)
        assertEquals(id, loaded.studentId)
    }

    @Test
    fun testCandidateIdGeneration() {
        val candidate = StudentIdGenerator.generateCandidateId()
        assertNotNull(candidate)
        assertTrue(candidate.startsWith("NX-"))
        assertTrue(StudentIdGenerator.isValidStudentId(candidate))
    }
}
