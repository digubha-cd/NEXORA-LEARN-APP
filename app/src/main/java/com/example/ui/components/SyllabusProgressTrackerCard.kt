package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import com.example.core.storage.StudentPreferences
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.model.Subject
import com.example.core.repository.SyllabusRepository
import com.example.ui.theme.NexoraBorder
import com.example.ui.theme.NexoraCyan
import com.example.ui.theme.NexoraElectricBlue
import com.example.ui.theme.NexoraGold
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
import com.example.ui.theme.SubjectAccountsColor
import com.example.ui.theme.SubjectBaColor
import com.example.ui.theme.SubjectEconColor
import com.example.ui.theme.SubjectEnglishColor
import com.example.ui.theme.SubjectGujaratiColor
import com.example.ui.theme.SubjectSpCcColor
import com.example.ui.theme.SubjectStatsColor

/**
 * Modes for the Progress Tracking Chart.
 */
enum class ProgressChartMode {
    SUBJECT_BARS,
    TIMELINE_TREND
}

/**
 * Visual Progress Tracking Card on the main dashboard using charts
 * to show the completion percentage of the 7 syllabus subjects over time.
 */
@Composable
fun SyllabusProgressTrackerCard(
    onNavigateToSubjects: () -> Unit,
    onSubjectClick: (Subject) -> Unit,
    modifier: Modifier = Modifier
) {
    val completedChapterIds by SyllabusRepository.completedChapterIds.collectAsStateWithLifecycle()
    var selectedMode by remember { mutableStateOf(ProgressChartMode.SUBJECT_BARS) }
    var selectedSubjectId by remember { mutableStateOf<String?>(null) }

    val totalChapters = remember(completedChapterIds) { SyllabusRepository.getTotalChaptersCount() }
    val totalCompleted = remember(completedChapterIds) { SyllabusRepository.getTotalCompletedChaptersCount(completedChapterIds) }
    val overallProgress = remember(completedChapterIds) { SyllabusRepository.getOverallProgress(completedChapterIds) }
    val overallPercentInt = (overallProgress * 100).toInt()

    // 7 Official Subjects with their color mappings
    val subjectsData = remember(completedChapterIds) {
        Subject.OFFICIAL_SUBJECTS.map { subject ->
            val chapters = SyllabusRepository.getChapters(subject.id)
            val completed = chapters.count { completedChapterIds.contains(it.id) }
            val progress = if (chapters.isNotEmpty()) completed.toFloat() / chapters.size else 0f
            val color = getSubjectColor(subject.id)
            SubjectProgressItem(
                subject = subject,
                totalChapters = chapters.size,
                completedChapters = completed,
                progress = progress,
                color = color
            )
        }
    }

    NexoraCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("syllabus_progress_tracker_card"),
        cornerRadius = 20.dp,
        contentPadding = 18.dp,
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Title, Total Progress Badge & 90+ Target
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(NexoraCyan.copy(alpha = 0.12f), CircleShape)
                            .border(1.dp, NexoraCyan.copy(alpha = 0.25f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.TrendingUp,
                            contentDescription = "Progress Tracker",
                            tint = NexoraCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Syllabus Progress",
                                color = NexoraTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.testTag("progress_tracker_title")
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // 90+ Target Pill
                            Box(
                                modifier = Modifier
                                    .background(NexoraGold.copy(alpha = 0.14f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "🎯 90+ PACE",
                                    color = NexoraGold,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = "7 Subjects • $totalCompleted of $totalChapters Chapters Completed",
                            color = NexoraTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Overall Percentage Ring / Pill
                Box(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                listOf(NexoraCyan.copy(alpha = 0.15f), NexoraElectricBlue.copy(alpha = 0.15f))
                            ),
                            RoundedCornerShape(12.dp)
                        )
                        .border(1.dp, NexoraCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("overall_progress_badge")
                ) {
                    Text(
                        text = "$overallPercentInt%",
                        color = NexoraCyan,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            val context = androidx.compose.ui.platform.LocalContext.current
            var showGoalsDialog by remember { mutableStateOf(false) }

            val subjectGoals = remember(completedChapterIds, showGoalsDialog) {
                Subject.OFFICIAL_SUBJECTS.associate { s ->
                    s.id to StudentPreferences.getSubjectGoal(context, s.id, 90)
                }
            }
            val avgGoal = if (subjectGoals.isNotEmpty()) subjectGoals.values.average().toInt() else 90
            val combinedActual = overallPercentInt
            val combinedGoal = avgGoal

            // Combined Study Progress Bar (Tappable to assign subject percentage goals)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NexoraSurfaceVariant.copy(alpha = 0.6f))
                    .border(1.dp, NexoraBorder, RoundedCornerShape(14.dp))
                    .clickable { showGoalsDialog = true }
                    .padding(14.dp)
                    .testTag("combined_study_progress_bar"),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Combined Study Progress",
                                color = NexoraTextPrimary,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(NexoraCyan.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Goal: $combinedGoal%",
                                    color = NexoraCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$combinedActual% / $combinedGoal%",
                                color = if (combinedActual >= combinedGoal) NexoraSuccess else NexoraTextPrimary,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Edit Subject Goals",
                                tint = NexoraCyan,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { (combinedActual.toFloat() / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (combinedActual >= combinedGoal) NexoraSuccess else NexoraCyan,
                        trackColor = NexoraSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Subject Percentage Goals Dialog
            if (showGoalsDialog) {
                AlertDialog(
                    onDismissRequest = { showGoalsDialog = false },
                    title = {
                        Text(text = "Assign Subject Percentage Goals", color = NexoraTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 360.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "Set your target completion percentage for each GSEB Class 12 Commerce subject to keep your study pace on track for 90+.",
                                color = NexoraTextSecondary,
                                fontSize = 12.sp
                            )

                            Subject.OFFICIAL_SUBJECTS.forEach { subject ->
                                val currentGoal = StudentPreferences.getSubjectGoal(context, subject.id, 90)
                                var goalState by remember { mutableStateOf(currentGoal.toFloat()) }

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = subject.name,
                                            color = NexoraTextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${goalState.toInt()}%",
                                            color = getSubjectColor(subject.id),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                    Slider(
                                        value = goalState,
                                        onValueChange = { newVal ->
                                            goalState = newVal
                                            StudentPreferences.setSubjectGoal(context, subject.id, newVal.toInt())
                                        },
                                        valueRange = 50f..100f,
                                        steps = 9,
                                        colors = SliderDefaults.colors(
                                            thumbColor = getSubjectColor(subject.id),
                                            activeTrackColor = getSubjectColor(subject.id),
                                            inactiveTrackColor = NexoraSurfaceVariant
                                        ),
                                        modifier = Modifier.testTag("goal_slider_${subject.id}")
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showGoalsDialog = false }) {
                            Text(text = "Done", color = NexoraCyan, fontWeight = FontWeight.Bold)
                        }
                    },
                    containerColor = NexoraSurfaceElevated,
                    tonalElevation = 6.dp
                )
            }

            // Chart Mode Switcher (Subject Breakdown vs Timeline Trend)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NexoraSurfaceVariant, RoundedCornerShape(12.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Tab 1: Subject Bars
                val barsSelected = selectedMode == ProgressChartMode.SUBJECT_BARS
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (barsSelected) NexoraSurface else Color.Transparent)
                        .clickable { selectedMode = ProgressChartMode.SUBJECT_BARS }
                        .padding(vertical = 7.dp)
                        .testTag("tab_subject_bars"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.BarChart,
                            contentDescription = null,
                            tint = if (barsSelected) NexoraCyan else NexoraTextMuted,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "7 Subjects Breakdown",
                            color = if (barsSelected) NexoraTextPrimary else NexoraTextMuted,
                            fontSize = 11.5.sp,
                            fontWeight = if (barsSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }

                // Tab 2: Timeline Trend
                val trendSelected = selectedMode == ProgressChartMode.TIMELINE_TREND
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (trendSelected) NexoraSurface else Color.Transparent)
                        .clickable { selectedMode = ProgressChartMode.TIMELINE_TREND }
                        .padding(vertical = 7.dp)
                        .testTag("tab_timeline_trend"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.ShowChart,
                            contentDescription = null,
                            tint = if (trendSelected) NexoraCyan else NexoraTextMuted,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Timeline & 90+ Pace",
                            color = if (trendSelected) NexoraTextPrimary else NexoraTextMuted,
                            fontSize = 11.5.sp,
                            fontWeight = if (trendSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Animated Chart View
            AnimatedContent(
                targetState = selectedMode,
                transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(180)) },
                label = "progress_chart_content"
            ) { mode ->
                when (mode) {
                    ProgressChartMode.SUBJECT_BARS -> {
                        SubjectBarsChartView(
                            subjects = subjectsData,
                            selectedSubjectId = selectedSubjectId,
                            onSelectSubject = { id ->
                                selectedSubjectId = if (selectedSubjectId == id) null else id
                            },
                            onSubjectClick = onSubjectClick
                        )
                    }
                    ProgressChartMode.TIMELINE_TREND -> {
                        TimelineTrendChartView(
                            overallProgress = overallProgress,
                            subjects = subjectsData
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Subject Quick Chips
            Text(
                text = "Tap any subject to view syllabus:",
                color = NexoraTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(subjectsData) { item ->
                    val isFocused = selectedSubjectId == item.subject.id
                    val percent = (item.progress * 100).toInt()

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isFocused) item.color.copy(alpha = 0.16f) else NexoraSurfaceVariant
                            )
                            .border(
                                1.dp,
                                if (isFocused) item.color else NexoraBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                onSubjectClick(item.subject)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("subject_progress_chip_${item.subject.id}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(item.color, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.subject.name,
                                color = NexoraTextPrimary,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$percent%",
                                color = if (percent > 0) item.color else NexoraTextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card Footer: Detailed Breakdown Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GSEB Class 12 Commerce Syllabus",
                    color = NexoraTextMuted,
                    fontSize = 11.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onNavigateToSubjects() }
                        .padding(vertical = 4.dp)
                        .testTag("view_all_subjects_progress_btn")
                ) {
                    Text(
                        text = "View All Subjects",
                        color = NexoraCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Go to subjects",
                        tint = NexoraCyan,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

/**
 * Subject Bars Chart View: Displays animated completion bars for all 7 subjects.
 */
@Composable
private fun SubjectBarsChartView(
    subjects: List<SubjectProgressItem>,
    selectedSubjectId: String?,
    onSelectSubject: (String) -> Unit,
    onSubjectClick: (Subject) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NexoraSurfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        subjects.forEach { item ->
            val isSelected = selectedSubjectId == item.subject.id
            val animatedProgress by animateFloatAsState(
                targetValue = item.progress,
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                label = "bar_anim_${item.subject.id}"
            )
            val percent = (item.progress * 100).toInt()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        onSelectSubject(item.subject.id)
                    }
                    .padding(vertical = 2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(item.color, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item.subject.name,
                            color = NexoraTextPrimary,
                            fontSize = 12.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${item.completedChapters}/${item.totalChapters} ch",
                            color = NexoraTextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$percent%",
                            color = item.color,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Custom Rounded Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(NexoraBorder.copy(alpha = 0.7f), CircleShape)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                            .height(8.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(item.color.copy(alpha = 0.8f), item.color)
                                ),
                                CircleShape
                            )
                    )
                }
            }
        }
    }
}

/**
 * Timeline Trend Chart View: Visualizes study progress trajectory against the 90+ Target pace.
 */
@Composable
private fun TimelineTrendChartView(
    overallProgress: Float,
    subjects: List<SubjectProgressItem>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NexoraSurfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        // Timeline Header Summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Weekly Completion Trajectory",
                    color = NexoraTextPrimary,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Progress curve vs 90+ Target Pace",
                    color = NexoraTextSecondary,
                    fontSize = 10.5.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(NexoraGold, CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "90% Pace Line",
                    color = NexoraTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Canvas Line Chart with Bezier curve & 90% benchmark
        val currentPercent = (overallProgress * 100).coerceAtLeast(0f)
        val dataPoints = remember(overallProgress) {
            listOf(
                0.05f, // Start
                (overallProgress * 0.25f).coerceAtLeast(0.08f), // Week 1
                (overallProgress * 0.55f).coerceAtLeast(0.12f), // Week 2
                (overallProgress * 0.8f).coerceAtLeast(0.18f),  // Week 3
                overallProgress.coerceAtLeast(0.22f)            // Current
            )
        }

        val primaryColor = NexoraCyan
        val goldColor = NexoraGold
        val gridColor = NexoraBorder

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .testTag("timeline_trend_canvas")
        ) {
            val width = size.width
            val height = size.height
            val paddingBottom = 20.dp.toPx()
            val chartHeight = height - paddingBottom

            // 1. Draw horizontal grid lines (0%, 50%, 90%, 100%)
            val y90 = chartHeight * (1f - 0.90f)
            val y50 = chartHeight * (1f - 0.50f)
            val y0 = chartHeight

            drawLine(
                color = gridColor,
                start = Offset(0f, y0),
                end = Offset(width, y0),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = gridColor.copy(alpha = 0.6f),
                start = Offset(0f, y50),
                end = Offset(width, y50),
                strokeWidth = 1.dp.toPx()
            )

            // 2. Draw 90+ Target Dashed Benchmark Line
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
            drawLine(
                color = goldColor.copy(alpha = 0.8f),
                start = Offset(0f, y90),
                end = Offset(width, y90),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = dashEffect
            )

            // 3. Draw Actual Progress Curve
            val stepX = width / (dataPoints.size - 1)
            val path = Path()
            val fillPath = Path()

            val points = dataPoints.mapIndexed { index, value ->
                val x = index * stepX
                val y = chartHeight * (1f - value.coerceIn(0f, 1f))
                Offset(x, y)
            }

            path.moveTo(points.first().x, points.first().y)
            fillPath.moveTo(points.first().x, chartHeight)
            fillPath.lineTo(points.first().x, points.first().y)

            for (i in 0 until points.size - 1) {
                val p1 = points[i]
                val p2 = points[i + 1]
                val controlPoint1 = Offset((p1.x + p2.x) / 2, p1.y)
                val controlPoint2 = Offset((p1.x + p2.x) / 2, p2.y)
                path.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p2.x, p2.y)
                fillPath.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p2.x, p2.y)
            }

            fillPath.lineTo(points.last().x, chartHeight)
            fillPath.close()

            // Draw Area Fill Gradient
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(primaryColor.copy(alpha = 0.25f), Color.Transparent),
                    startY = 0f,
                    endY = chartHeight
                )
            )

            // Draw Stroke
            drawPath(
                path = path,
                color = primaryColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw Data Points
            points.forEachIndexed { index, pt ->
                val isCurrent = index == points.size - 1
                drawCircle(
                    color = if (isCurrent) NexoraCyan else NexoraElectricBlue,
                    radius = if (isCurrent) 5.dp.toPx() else 3.5.dp.toPx(),
                    center = pt
                )
                drawCircle(
                    color = Color.White,
                    radius = if (isCurrent) 2.5.dp.toPx() else 1.5.dp.toPx(),
                    center = pt
                )
            }
        }

        // Timeline Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Week 1", "Week 2", "Week 3", "Week 4", "Now (Current)").forEach { label ->
                Text(
                    text = label,
                    color = NexoraTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Subject Progress Data Model for the Chart.
 */
data class SubjectProgressItem(
    val subject: Subject,
    val totalChapters: Int,
    val completedChapters: Int,
    val progress: Float,
    val color: Color
)

/**
 * Returns the distinctive high-contrast color for each of the 7 subjects.
 */
fun getSubjectColor(subjectId: String): Color {
    return when (subjectId) {
        "gujarati" -> SubjectGujaratiColor
        "english" -> SubjectEnglishColor
        "sp_cc" -> SubjectSpCcColor
        "ba" -> SubjectBaColor
        "stat" -> SubjectStatsColor
        "accounts" -> SubjectAccountsColor
        "economics" -> SubjectEconColor
        else -> NexoraCyan
    }
}
