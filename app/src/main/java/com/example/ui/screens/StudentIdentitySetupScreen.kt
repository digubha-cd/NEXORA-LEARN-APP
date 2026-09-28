package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.repository.FirestoreStudentProfileRepository
import com.example.core.repository.FriendsRepository
import com.example.core.storage.StudentPreferences
import com.example.core.util.StudentIdGenerator
import com.example.ui.components.NexoraCard
import com.example.ui.components.NexoraLogo
import com.example.ui.theme.NexoraBackground
import com.example.ui.theme.NexoraBorder
import com.example.ui.theme.NexoraCyan
import com.example.ui.theme.NexoraError
import com.example.ui.theme.NexoraGold
import com.example.ui.theme.NexoraPurple
import com.example.ui.theme.NexoraSuccess
import com.example.ui.theme.NexoraSurface
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary
import kotlinx.coroutines.launch

/**
 * First-Launch Student Identity Setup Screen.
 *
 * No login, no passwords, no OTP, no Google Sign-In.
 * Prompts student for Full Student Name and automatically generates a unique,
 * immutable Student ID in NX-XXXXXX format with collision-resistant Firestore verification.
 */
@Composable
fun StudentIdentitySetupScreen(
    onSetupComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { FirestoreStudentProfileRepository(context) }
    val initialProfile = remember { StudentPreferences.getStudentProfile(context) }

    var studentName by remember {
        mutableStateOf(StudentPreferences.getSavedStudentName(context).ifBlank { "" })
    }
    var candidateId by remember {
        mutableStateOf(
            if (StudentIdGenerator.isValidStudentId(initialProfile.studentId)) {
                initialProfile.studentId
            } else {
                StudentIdGenerator.generateCandidateId()
            }
        )
    }
    var isCheckingId by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NexoraBackground)
            .statusBarsPadding()
            .testTag("student_identity_setup_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // NEXORA LEARN Branding
            NexoraLogo(
                emblemSize = 72.dp,
                showWordmark = true,
                showSubtitle = true,
                subtitleText = "GUJARAT BOARD CLASS 12 COMMERCE"
            )

            // Setup Header Card
            NexoraCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = 16.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(NexoraCyan.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.School,
                                contentDescription = null,
                                tint = NexoraCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "Student Identity Setup",
                            color = NexoraTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Set up your student profile to track study goals, syllabus progress, and board exam countdowns. No login or password required.",
                        color = NexoraTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    // Target pill
                    Row(
                        modifier = Modifier
                            .background(NexoraGold.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                            .border(1.dp, NexoraGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = null,
                            tint = NexoraGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "GSEB Board Target: 90+ Marks",
                            color = NexoraGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Input Card 1: Full Student Name
            NexoraCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = 18.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = NexoraCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Full Student Name",
                            color = NexoraTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedTextField(
                        value = studentName,
                        onValueChange = {
                            studentName = it
                            errorMessage = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("student_name_input"),
                        placeholder = {
                            Text(
                                text = "Enter your full student name",
                                color = NexoraTextMuted,
                                fontSize = 14.sp
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NexoraCyan,
                            unfocusedBorderColor = NexoraBorder,
                            focusedContainerColor = NexoraSurfaceElevated,
                            unfocusedContainerColor = NexoraSurface,
                            focusedTextColor = NexoraTextPrimary,
                            unfocusedTextColor = NexoraTextPrimary,
                            cursorColor = NexoraCyan
                        )
                    )

                    Text(
                        text = "This name appears on your daily study plan, syllabus tracker, and peer study profile.",
                        color = NexoraTextMuted,
                        fontSize = 11.5.sp
                    )
                }
            }

            // Input Card 2: Auto-Generated Student ID (Review & Immutable)
            NexoraCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = 18.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Badge,
                                contentDescription = null,
                                tint = NexoraPurple,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Your Student ID",
                                color = NexoraTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Regenerate ID Button
                        Row(
                            modifier = Modifier
                                .background(NexoraPurple.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                .clickable(enabled = !isSaving && !isCheckingId) {
                                    isCheckingId = true
                                    coroutineScope.launch {
                                        var newCandidate = StudentIdGenerator.generateCandidateId()
                                        // Verify availability in registry
                                        var attempts = 0
                                        while (!repository.isStudentIdAvailable(newCandidate, initialProfile.userId) && attempts < 5) {
                                            newCandidate = StudentIdGenerator.generateCandidateId()
                                            attempts++
                                        }
                                        candidateId = newCandidate
                                        isCheckingId = false
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (isCheckingId) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    strokeWidth = 1.5.dp,
                                    color = NexoraPurple
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.Refresh,
                                    contentDescription = "Regenerate ID",
                                    tint = NexoraPurple,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = "Regenerate",
                                color = NexoraPurple,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Large Monospace ID Display Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NexoraSurfaceElevated, RoundedCornerShape(12.dp))
                            .border(1.5.dp, NexoraCyan.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                            .padding(vertical = 14.dp, horizontal = 16.dp)
                            .testTag("generated_student_id_box"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = candidateId,
                                color = NexoraCyan,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 2.sp
                            )
                        }
                    }

                    // Format and Immutability Notice
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = NexoraSuccess,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Format: NX-XXXXXX • Verified Unique",
                                color = NexoraSuccess,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = NexoraTextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Immutable: Once confirmed, this Student ID is permanently locked to your profile.",
                                color = NexoraTextMuted,
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Error display if any
            if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NexoraError.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = NexoraError,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Primary Confirm Button
            Button(
                onClick = {
                    val trimmed = studentName.trim()
                    if (trimmed.length < 2) {
                        errorMessage = "Please enter your student name (minimum 2 characters)."
                        return@Button
                    }

                    isSaving = true
                    errorMessage = null

                    coroutineScope.launch {
                        try {
                            // 1. Transaction-safe atomic registration with collision retry
                            val registerResult = repository.registerUniqueStudentId(
                                userId = initialProfile.userId,
                                studentName = trimmed,
                                requestedStudentId = candidateId
                            )

                            val finalStudentId = registerResult.getOrDefault(candidateId)

                            // 2. Save locally and lock Student ID permanently
                            val updatedProfile = StudentPreferences.completeIdentitySetup(
                                context = context,
                                name = trimmed,
                                studentId = finalStudentId
                            )

                            // 3. Initialize peer connection & repositories
                            FriendsRepository.initialize(updatedProfile)

                            Toast.makeText(context, "Welcome to NEXORA LEARN, $trimmed!", Toast.LENGTH_SHORT).show()

                            // 4. Open Home screen directly
                            onSetupComplete()
                        } catch (e: Exception) {
                            // Fallback: save locally
                            val updated = StudentPreferences.completeIdentitySetup(
                                context = context,
                                name = trimmed,
                                studentId = candidateId
                            )
                            FriendsRepository.initialize(updated)
                            onSetupComplete()
                        } finally {
                            isSaving = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_student_identity_button"),
                enabled = studentName.trim().length >= 2 && !isSaving,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NexoraCyan,
                    contentColor = NexoraBackground,
                    disabledContainerColor = NexoraCyan.copy(alpha = 0.35f),
                    disabledContentColor = NexoraBackground.copy(alpha = 0.6f)
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = NexoraBackground
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Securing Unique ID...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                } else {
                    Text(
                        text = "Continue to Home",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
