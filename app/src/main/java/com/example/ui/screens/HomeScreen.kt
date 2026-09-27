package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.constants.AppConstants
import com.example.core.model.StudentProfile
import com.example.core.model.Subject
import com.example.core.repository.FriendsRepository
import com.example.core.repository.StudyPlannerRepository
import com.example.core.repository.SyllabusRepository
import com.example.ui.components.ExamCountdownWidget
import com.example.ui.components.NexoraCard
import com.example.ui.components.NexoraCreatorCredit
import com.example.ui.components.NexoraLogo
import com.example.ui.components.Past7DaysActivityCard
import com.example.ui.components.SyllabusProgressTrackerCard
import com.example.ui.components.WeeklyInsightsCard
import com.example.ui.theme.NexoraBackground
import com.example.ui.theme.NexoraBorder
import com.example.ui.theme.NexoraCyan
import com.example.ui.theme.NexoraGold
import com.example.ui.theme.NexoraLavender
import com.example.ui.theme.NexoraMagenta
import com.example.ui.theme.NexoraPink
import com.example.ui.theme.NexoraPurple
import com.example.ui.theme.NexoraSuccess
import com.example.ui.theme.NexoraSurface
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraSurfaceVariant
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Premium Home Dashboard for NEXORA LEARN adhering exactly to the visual reference screenshot.
 *
 * Layout Sections:
 * 1. Top Header: Mini NEXORA logo + "NEXORA LEARN", "Welcome, [Name]", and Right Logo Emblem
 * 2. 90+ MARKS TARGET Card (Gold accent, "TARGET" badge, "GSEB Class 12 Commerce · Gujarati Medium")
 * 3. Student Identity Bar: Person icon in cyan circle, Name, "Enrolled: Gujarati Medium", "✓ Active" badge
 * 4. Exam Milestones: "Exam Dashboard →", School Exam (22 Oct 2026) & Board Exam (25 Feb 2027) cards
 * 5. Day Streak Card: Flame icon, "X Days Streak", "Complete 1 task today to keep streak", "Best: Xd"
 * 6. Study Friends & Competition Card: "Study Buddies", chevron navigation
 * 7. Today's Study To-Do: "Open Planner →", "Today - [Date]", "X / Y Tasks Done", Task items
 * 8. Visual Syllabus Progress Tracking Card (7 Subjects breakdown & 90+ pace line)
 * 9. Quick Actions (Planner, Exam, Friends, Leaderboard)
 * 10. Creator Credit: "This App is Made by DIGVIJAYSINH CHAUHAN"
 */
@Composable
fun HomeScreen(
    studentProfile: StudentProfile,
    onSubjectClick: (Subject) -> Unit = {},
    onNavigateToSubjects: () -> Unit = {},
    onNavigateToPlanner: () -> Unit = {},
    onNavigateToExam: () -> Unit = {},
    onOpenFriendsPage: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val tasks by StudyPlannerRepository.tasks.collectAsStateWithLifecycle()
    val streak = remember(tasks) { StudyPlannerRepository.getStudyStreak() }
    val past7DaysActivity = remember(tasks) { StudyPlannerRepository.getPast7DaysActivity() }
    val todayTasks = remember(tasks) { StudyPlannerRepository.getTodayTasks() }
    val todayCompletedCount = remember(todayTasks) { todayTasks.count { it.isCompleted } }
    val todayTotalCount = todayTasks.size

    val completedChapterIds by SyllabusRepository.completedChapterIds.collectAsStateWithLifecycle()

    // Friends & Social State
    val friendsList by FriendsRepository.friends.collectAsStateWithLifecycle()
    val totalNotificationCount = remember(friendsList) { FriendsRepository.getTotalNotificationCount() }

    // Dynamic current date formatted (e.g., "22 Sep 2026")
    val todayDateFormatted = remember {
        try {
            val date = StudyPlannerRepository.TODAY
            date.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH))
        } catch (e: Exception) {
            "22 Sep 2026"
        }
    }

    val displayName = studentProfile.studentName.ifBlank { "Digvijay" }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NexoraBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("home_screen_root")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Top Header: Mini NEXORA Logo + Greeting + Right Logo Emblem
            item {
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_top_bar"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        NexoraLogo(
                            emblemSize = 32.dp,
                            showWordmark = false,
                            showSubtitle = false
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = AppConstants.APP_NAME,
                                color = NexoraTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Welcome, $displayName",
                                color = NexoraCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Right Logo Emblem matching reference screenshot
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NexoraSurfaceElevated)
                            .border(1.dp, NexoraBorder, CircleShape)
                            .clickable { onOpenFriendsPage() },
                        contentAlignment = Alignment.Center
                    ) {
                        NexoraLogo(
                            emblemSize = 22.dp,
                            showWordmark = false,
                            showSubtitle = false
                        )
                    }
                }
            }

            // 2. 90+ MARKS TARGET Card (Gold Background matching reference)
            item {
                NexoraCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToExam() }
                        .testTag("home_target_hero_card"),
                    containerColor = Color(0xFFFFFBEB),
                    borderColor = NexoraGold.copy(alpha = 0.4f),
                    cornerRadius = 16.dp,
                    contentPadding = 14.dp,
                    elevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(NexoraGold.copy(alpha = 0.18f), CircleShape)
                                    .border(1.dp, NexoraGold.copy(alpha = 0.35f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🎯", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "90+ MARKS TARGET",
                                    color = Color(0xFF92400E), // Deep amber
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "GSEB Class 12 Commerce • Gujarati Medium",
                                    color = Color(0xFFB45309), // Medium amber
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .background(NexoraGold.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .border(1.dp, NexoraGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "TARGET",
                                color = Color(0xFFB45309),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }

            // 3. Student Identity Bar Card (Matches reference screenshot)
            item {
                NexoraCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_student_identity_bar"),
                    cornerRadius = 16.dp,
                    contentPadding = 14.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(NexoraCyan.copy(alpha = 0.12f), CircleShape)
                                    .border(1.dp, NexoraCyan.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = "Student",
                                    tint = NexoraCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = displayName,
                                    color = NexoraTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Enrolled: Gujarati Medium",
                                    color = NexoraTextSecondary,
                                    fontSize = 11.5.sp
                                )
                            }
                        }

                        // "✓ Active" Badge
                        Box(
                            modifier = Modifier
                                .background(NexoraSuccess.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                                .border(1.dp, NexoraSuccess.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = NexoraSuccess,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Active",
                                    color = NexoraSuccess,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Motivation of the Day Section (Curated Daily Study Quotes)
            item {
                val quotes = remember {
                    listOf(
                        QuoteItem("Success is the sum of small efforts, repeated day in and day out.", "Robert Collier"),
                        QuoteItem("The secret of getting ahead is getting started. Conquer your syllabus today!", "Mark Twain"),
                        QuoteItem("Excellence is not an act, but a habit. Keep revising every day!", "Aristotle"),
                        QuoteItem("Your limitation—it's only your imagination. Aim for 90+ Marks!", "Nexora Learn"),
                        QuoteItem("Hard work beats talent when talent doesn't work hard.", "Tim Notke"),
                        QuoteItem("Believe you can and you're halfway there.", "Theodore Roosevelt"),
                        QuoteItem("Education is the passport to the future, for tomorrow belongs to those who prepare for it today.", "Malcolm X"),
                        QuoteItem("Focus on your goals, block out the distractions, and conquer your Board Exams.", "Nexora Study"),
                        QuoteItem("An investment in knowledge pays the best interest.", "Benjamin Franklin"),
                        QuoteItem("Push yourself, because no one else is going to do it for you.", "Study Motivation")
                    )
                }
                val dailyQuote = remember(quotes) {
                    val dayOfYear = LocalDate.now().dayOfYear
                    quotes[dayOfYear % quotes.size]
                }

                NexoraCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("motivation_of_the_day_card"),
                    containerColor = NexoraSurfaceElevated,
                    borderColor = NexoraPurple.copy(alpha = 0.3f),
                    cornerRadius = 16.dp,
                    contentPadding = 16.dp,
                    elevation = 1.dp
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(NexoraPurple.copy(alpha = 0.15f), CircleShape)
                                        .border(1.dp, NexoraPurple.copy(alpha = 0.3f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.AutoAwesome,
                                        contentDescription = null,
                                        tint = NexoraPurple,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Motivation of the Day",
                                    color = NexoraTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(NexoraPurple.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "DAILY",
                                    color = NexoraPurple,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        Text(
                            text = "“${dailyQuote.text}”",
                            color = NexoraTextPrimary,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 19.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "— ${dailyQuote.author}",
                                color = NexoraTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // 4. Prominent Real-Time Exam Countdown Section (Live IST Clock + Dual Countdown)
            item {
                ExamCountdownWidget(
                    onNavigateToExam = onNavigateToExam
                )
            }

            // 5. Day Streak Card (with subtle pulse animation when daily study goal is achieved)
            item {
                val infiniteTransition = rememberInfiniteTransition(label = "streak_pulse_transition")
                val flameScale by if (streak.isCompletedToday) {
                    infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.14f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "flame_pulse_scale"
                    )
                } else {
                    remember { mutableStateOf(1f) }
                }

                val pulseGlowAlpha by if (streak.isCompletedToday) {
                    infiniteTransition.animateFloat(
                        initialValue = 0.25f,
                        targetValue = 0.65f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "streak_glow_alpha"
                    )
                } else {
                    remember { mutableStateOf(0.3f) }
                }

                NexoraCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_streak_stat_card"),
                    borderColor = if (streak.isCompletedToday) NexoraGold.copy(alpha = pulseGlowAlpha) else NexoraBorder,
                    cornerRadius = 16.dp,
                    contentPadding = 14.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .graphicsLayer {
                                        scaleX = flameScale
                                        scaleY = flameScale
                                    }
                                    .background(
                                        NexoraGold.copy(alpha = if (streak.isCompletedToday) pulseGlowAlpha * 0.35f else 0.12f),
                                        CircleShape
                                    )
                                    .border(
                                        1.dp,
                                        NexoraGold.copy(alpha = if (streak.isCompletedToday) pulseGlowAlpha else 0.3f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Whatshot,
                                    contentDescription = "Streak",
                                    tint = NexoraGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${streak.currentStreak} Days Streak",
                                    color = NexoraTextPrimary,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (streak.isCompletedToday) "Daily goal achieved! Streak active 🔥" else "Complete 1 task today to keep streak",
                                    color = if (streak.isCompletedToday) NexoraSuccess else NexoraTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (streak.isCompletedToday) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Best: ${streak.bestStreak}d",
                                color = NexoraTextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (streak.isCompletedToday) NexoraSuccess.copy(alpha = 0.12f) else NexoraGold.copy(alpha = 0.1f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (streak.isCompletedToday) NexoraSuccess.copy(alpha = pulseGlowAlpha) else NexoraGold.copy(alpha = 0.3f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (streak.isCompletedToday) "Today Done ✓" else "Today Pending",
                                    color = if (streak.isCompletedToday) NexoraSuccess else NexoraGold,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 5b. Secondary Card: Past 7 Days Study Activity Horizontal Bar Chart
            item {
                Past7DaysActivityCard(
                    activityList = past7DaysActivity,
                    onNavigateToPlanner = onNavigateToPlanner
                )
            }

            // 5c. Weekly Insights Dashboard: Study Hours per Subject over Last 7 Days
            item {
                WeeklyInsightsCard()
            }

            // 6. Study Friends & Competition Card (Matches reference screenshot)
            item {
                NexoraCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenFriendsPage() }
                        .testTag("home_friends_competition_card"),
                    cornerRadius = 16.dp,
                    contentPadding = 14.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(NexoraCyan.copy(alpha = 0.12f), CircleShape)
                                    .border(1.dp, NexoraCyan.copy(alpha = 0.25f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Group,
                                    contentDescription = "Friends",
                                    tint = NexoraCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Study Friends & Competition",
                                    color = NexoraTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Study Buddies",
                                    color = NexoraCyan,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = "Open Friends",
                            tint = NexoraTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 7. Today's Study To-Do Section (Matches reference screenshot)
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Today's Study To-Do",
                            color = NexoraTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("today_todo_title")
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { onNavigateToPlanner() }
                                .padding(vertical = 4.dp)
                                .testTag("open_planner_btn")
                        ) {
                            Text(
                                text = "Open Planner",
                                color = NexoraCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = NexoraCyan,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Today • $todayDateFormatted",
                            color = NexoraTextSecondary,
                            fontSize = 11.5.sp
                        )
                        Text(
                            text = "$todayCompletedCount / $todayTotalCount Tasks Done",
                            color = if (todayCompletedCount == todayTotalCount && todayTotalCount > 0) NexoraSuccess else NexoraCyan,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Today's Tasks List or Empty State Card
            if (todayTasks.isEmpty()) {
                item {
                    NexoraCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToPlanner() }
                            .testTag("today_empty_tasks_card"),
                        cornerRadius = 16.dp,
                        contentPadding = 18.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "No study tasks scheduled for today",
                                    color = NexoraTextPrimary,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Tap to add your study goals in Study Planner.",
                                    color = NexoraTextSecondary,
                                    fontSize = 11.5.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(NexoraCyan.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "+ Add Task",
                                    color = NexoraCyan,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                items(todayTasks.take(3), key = { it.taskId }) { task ->
                    NexoraCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_task_${task.taskId}"),
                        cornerRadius = 14.dp,
                        contentPadding = 12.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { _ ->
                                    StudyPlannerRepository.toggleTaskCompletion(task.taskId)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = NexoraCyan,
                                    uncheckedColor = NexoraBorder,
                                    checkmarkColor = NexoraSurface
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.title,
                                    color = if (task.isCompleted) NexoraTextMuted else NexoraTextPrimary,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (task.chapterTitle.isNotBlank()) "${task.subjectName} • ${task.chapterTitle}" else task.subjectName,
                                    color = NexoraTextMuted,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // 8. Visual Progress Tracking Card (7 Syllabus Subjects completion chart)
            item {
                SyllabusProgressTrackerCard(
                    onNavigateToSubjects = onNavigateToSubjects,
                    onSubjectClick = onSubjectClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 9. Quick Actions Section (4 round/square cards: Planner, Exam, Friends, Leaderboard)
            item {
                Column {
                    Text(
                        text = "Quick Actions",
                        color = NexoraTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("quick_actions_title")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // 1. Planner
                        HomeQuickActionItem(
                            icon = Icons.Filled.CalendarMonth,
                            iconTint = NexoraCyan,
                            bgTint = NexoraCyan.copy(alpha = 0.12f),
                            label = "Planner",
                            onClick = onNavigateToPlanner,
                            testTag = "quick_action_planner"
                        )
                        // 2. Exam
                        HomeQuickActionItem(
                            icon = Icons.Filled.EmojiEvents,
                            iconTint = NexoraPink,
                            bgTint = NexoraPink.copy(alpha = 0.12f),
                            label = "Exam",
                            onClick = onNavigateToExam,
                            testTag = "quick_action_exam"
                        )
                        // 3. Friends
                        HomeQuickActionItem(
                            icon = Icons.Filled.Group,
                            iconTint = NexoraLavender,
                            bgTint = NexoraLavender.copy(alpha = 0.14f),
                            label = "Friends",
                            onClick = onOpenFriendsPage,
                            testTag = "quick_action_friends"
                        )
                        // 4. Leaderboard
                        HomeQuickActionItem(
                            icon = Icons.Filled.Leaderboard,
                            iconTint = NexoraGold,
                            bgTint = NexoraGold.copy(alpha = 0.14f),
                            label = "Leaderboard",
                            onClick = onOpenFriendsPage,
                            testTag = "quick_action_leaderboard"
                        )
                    }
                }
            }

            // 10. Global Creator Credit
            item {
                NexoraCreatorCredit()
            }
        }
    }
}

/**
 * Reusable Quick Action Item.
 */
@Composable
private fun HomeQuickActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    bgTint: Color,
    label: String,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(bgTint, RoundedCornerShape(16.dp))
                .border(1.dp, iconTint.copy(alpha = 0.25f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = NexoraTextPrimary,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private data class QuoteItem(val text: String, val author: String)
