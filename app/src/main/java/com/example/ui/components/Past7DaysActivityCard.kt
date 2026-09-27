package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.DayStudyActivity
import com.example.ui.theme.NexoraBorder
import com.example.ui.theme.NexoraCyan
import com.example.ui.theme.NexoraElectricBlue
import com.example.ui.theme.NexoraGold
import com.example.ui.theme.NexoraMagenta
import com.example.ui.theme.NexoraPurple
import com.example.ui.theme.NexoraSuccess
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraSurfaceVariant
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary

/**
 * Secondary card displayed under the 'Day Streak' card on the Home screen.
 * Displays a visual horizontal bar chart of the past 7 days, showing study activity for each day.
 */
@Composable
fun Past7DaysActivityCard(
    activityList: List<DayStudyActivity>,
    modifier: Modifier = Modifier,
    onNavigateToPlanner: () -> Unit = {}
) {
    val maxCompleted = maxOf(1, activityList.maxOfOrNull { it.completedCount } ?: 1)
    // Scale reference: max completed tasks or minimum of 3 for nice proportional visual bars
    val maxScale = maxOf(maxCompleted, 3).toFloat()

    val totalCompleted7Days = activityList.sumOf { it.completedCount }
    val activeDaysCount = activityList.count { it.isStreakAchieved }
    val consistencyPercentage = if (activityList.isNotEmpty()) {
        (activeDaysCount * 100) / activityList.size
    } else 0

    NexoraCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("home_past_7_days_activity_card"),
        cornerRadius = 16.dp,
        contentPadding = 16.dp,
        elevation = 1.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Header: Icon + Title + Active Days Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(NexoraPurple.copy(alpha = 0.12f), CircleShape)
                            .border(1.dp, NexoraPurple.copy(alpha = 0.28f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.BarChart,
                            contentDescription = "7-Day Activity",
                            tint = NexoraPurple,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "7-Day Study Activity",
                                color = NexoraTextPrimary,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.testTag("activity_chart_title")
                            )
                        }
                        Text(
                            text = "Daily task completion & streak consistency",
                            color = NexoraTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Active Days Pill
                Box(
                    modifier = Modifier
                        .background(
                            if (activeDaysCount > 0) NexoraSuccess.copy(alpha = 0.12f) else NexoraSurfaceVariant,
                            RoundedCornerShape(10.dp)
                        )
                        .border(
                            1.dp,
                            if (activeDaysCount > 0) NexoraSuccess.copy(alpha = 0.35f) else NexoraBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                        .testTag("active_days_pill")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (activeDaysCount >= 5) "🔥" else "📊",
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$activeDaysCount/7 Days Active",
                            color = if (activeDaysCount > 0) NexoraSuccess else NexoraTextSecondary,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Horizontal Bar Chart for the past 7 days
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("activity_bars_container"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                activityList.forEach { dayActivity ->
                    val progressRatio = (dayActivity.completedCount / maxScale).coerceIn(0f, 1f)
                    val animatedProgress by animateFloatAsState(
                        targetValue = progressRatio,
                        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
                        label = "bar_anim_${dayActivity.dateStr}"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("activity_bar_day_${dayActivity.dayLabel.lowercase()}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Day Label & Date
                        Column(
                            modifier = Modifier.width(62.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (dayActivity.isToday) "Today" else dayActivity.dayLabel,
                                    color = if (dayActivity.isToday) NexoraCyan else NexoraTextPrimary,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (dayActivity.isToday || dayActivity.isStreakAchieved) FontWeight.Bold else FontWeight.SemiBold
                                )
                                if (dayActivity.isToday) {
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .background(NexoraCyan, CircleShape)
                                    )
                                }
                            }
                            Text(
                                text = dayActivity.dayNumberFormatted,
                                color = NexoraTextMuted,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Horizontal Bar Track
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(18.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .background(NexoraSurfaceVariant)
                                .border(
                                    0.5.dp,
                                    if (dayActivity.isToday) NexoraCyan.copy(alpha = 0.3f) else NexoraBorder.copy(alpha = 0.5f),
                                    RoundedCornerShape(9.dp)
                                )
                        ) {
                            if (animatedProgress > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(animatedProgress)
                                        .clip(RoundedCornerShape(9.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = when {
                                                    dayActivity.isToday -> listOf(
                                                        NexoraCyan,
                                                        NexoraElectricBlue
                                                    )
                                                    dayActivity.isStreakAchieved -> listOf(
                                                        NexoraPurple,
                                                        NexoraCyan
                                                    )
                                                    else -> listOf(
                                                        NexoraSurfaceVariant,
                                                        NexoraBorder
                                                    )
                                                }
                                            )
                                        )
                                )
                            } else {
                                // Subtle empty placeholder dot
                                Box(
                                    modifier = Modifier
                                        .padding(start = 6.dp)
                                        .size(6.dp)
                                        .align(Alignment.CenterStart)
                                        .background(NexoraTextMuted.copy(alpha = 0.3f), CircleShape)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Right Status / Count Badge
                        Box(
                            modifier = Modifier
                                .width(68.dp)
                                .align(Alignment.CenterVertically),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            if (dayActivity.completedCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (dayActivity.isToday) NexoraCyan.copy(alpha = 0.12f)
                                            else NexoraPurple.copy(alpha = 0.1f),
                                            RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = if (dayActivity.isToday) NexoraCyan else NexoraPurple,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "${dayActivity.completedCount} done",
                                            color = if (dayActivity.isToday) NexoraCyan else NexoraPurple,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = if (dayActivity.isToday) "Pending" else "0 done",
                                    color = if (dayActivity.isToday) NexoraGold else NexoraTextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = if (dayActivity.isToday) FontWeight.SemiBold else FontWeight.Normal,
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Mini Summary Stats Footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(NexoraSurfaceElevated)
                    .border(1.dp, NexoraBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stat 1: Total Tasks
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$totalCompleted7Days",
                            color = NexoraCyan,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tasks Done (7d)",
                            color = NexoraTextSecondary,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Divider dot
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .background(NexoraBorder, CircleShape)
                    )

                    // Stat 2: Active Days
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$activeDaysCount / 7",
                            color = NexoraPurple,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Streak Days",
                            color = NexoraTextSecondary,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Divider dot
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .background(NexoraBorder, CircleShape)
                    )

                    // Stat 3: Consistency
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$consistencyPercentage%",
                            color = if (consistencyPercentage >= 70) NexoraSuccess else NexoraGold,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Consistency",
                            color = NexoraTextSecondary,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4. Quick Planner shortcut
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToPlanner() }
                    .padding(vertical = 4.dp)
                    .testTag("activity_card_planner_shortcut"),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Manage Study To-Do & Schedule",
                    color = NexoraCyan,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = NexoraCyan,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
