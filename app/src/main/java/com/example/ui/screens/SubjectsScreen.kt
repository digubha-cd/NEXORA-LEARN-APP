package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.model.Subject
import com.example.core.repository.SyllabusRepository
import com.example.ui.components.NexoraCard
import com.example.ui.components.NexoraCreatorCredit
import com.example.ui.components.NexoraLogo
import com.example.ui.theme.NexoraBackground
import com.example.ui.theme.NexoraBorder
import com.example.ui.theme.NexoraCyan
import com.example.ui.theme.NexoraSurface
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
 * Visual metadata for each of the 7 Class 12 Commerce subjects matching reference screenshot.
 */
private data class SubjectStyle(
    val code: String,
    val icon: ImageVector,
    val accentColor: Color,
    val subtitle: String
)

private fun getSubjectStyle(subjectId: String): SubjectStyle {
    return when (subjectId) {
        "gujarati" -> SubjectStyle(
            code = "901",
            icon = Icons.Outlined.MenuBook,
            accentColor = SubjectGujaratiColor,
            subtitle = "ગુજરાતી • First Language"
        )
        "english" -> SubjectStyle(
            code = "023",
            icon = Icons.Outlined.Language,
            accentColor = SubjectEnglishColor,
            subtitle = "English • Second Language"
        )
        "sp_cc" -> SubjectStyle(
            code = "337",
            icon = Icons.Outlined.Description,
            accentColor = SubjectSpCcColor,
            subtitle = "Secretarial Practice & Commercial"
        )
        "ba" -> SubjectStyle(
            code = "144",
            icon = Icons.Outlined.BusinessCenter,
            accentColor = SubjectBaColor,
            subtitle = "Business Administration & Management"
        )
        "stat" -> SubjectStyle(
            code = "135",
            icon = Icons.Outlined.BarChart,
            accentColor = SubjectStatsColor,
            subtitle = "Statistics • આંકડાશાસ્ત્ર"
        )
        "accounts" -> SubjectStyle(
            code = "151",
            icon = Icons.Outlined.AccountBalance,
            accentColor = SubjectAccountsColor,
            subtitle = "Elements of Accounts • નામાનાં મૂળતત્વો"
        )
        "economics" -> SubjectStyle(
            code = "022",
            icon = Icons.Outlined.TrendingUp,
            accentColor = SubjectEconColor,
            subtitle = "Economics • અર્થશાસ્ત્ર"
        )
        else -> SubjectStyle(
            code = "000",
            icon = Icons.Outlined.MenuBook,
            accentColor = NexoraCyan,
            subtitle = "Class 12 Commerce"
        )
    }
}

/**
 * Screen displaying the official 7 Class 12 Commerce subjects:
 * 1. Gujarati (901)
 * 2. English (023)
 * 3. SP & CC (337)
 * 4. B.A. (144)
 * 5. Statistics (135)
 * 6. Elements of Accounts (151)
 * 7. Economics (022)
 */
@Composable
fun SubjectsScreen(
    onSubjectClick: (Subject) -> Unit,
    modifier: Modifier = Modifier
) {
    val completedChapterIds by SyllabusRepository.completedChapterIds.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NexoraBackground)
            .statusBarsPadding()
            .testTag("subjects_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Top Header: "Subjects" & Subtitle & Mini NEXORA logo emblem
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Subjects",
                            color = NexoraTextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.3.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Class 12 Commerce • Gujarati Medium (7 Subjects)",
                            color = NexoraTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    NexoraLogo(
                        emblemSize = 30.dp,
                        showWordmark = false,
                        showSubtitle = false
                    )
                }
            }

            // 2. Top Banner Card: "Gujarat Secondary & Higher Secondary Board"
            item {
                NexoraCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("subjects_banner_card"),
                    containerColor = NexoraCyan.copy(alpha = 0.08f),
                    borderColor = NexoraCyan.copy(alpha = 0.25f),
                    cornerRadius = 14.dp,
                    contentPadding = 14.dp
                ) {
                    Column {
                        Text(
                            text = "Gujarat Secondary & Higher Secondary Board",
                            color = NexoraTextPrimary,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Tap any subject to view study chapters & planner",
                            color = NexoraCyan,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // 3. Exactly 7 Official Subjects Cards
            items(Subject.OFFICIAL_SUBJECTS, key = { it.id }) { subject ->
                val chapters = SyllabusRepository.getChapters(subject.id)
                val completedCount = chapters.count { completedChapterIds.contains(it.id) }
                val totalCount = chapters.size
                val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
                val percentInt = (progress * 100).toInt()
                val style = getSubjectStyle(subject.id)

                NexoraCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSubjectClick(subject) }
                        .testTag("subject_card_${subject.id}"),
                    cornerRadius = 16.dp,
                    contentPadding = 16.dp,
                    elevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Subject Icon Box
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(style.accentColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                    .border(1.dp, style.accentColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = style.icon,
                                    contentDescription = subject.name,
                                    tint = style.accentColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = subject.name,
                                        color = NexoraTextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    // Subject Code Pill (e.g. 901, 023, etc.)
                                    Box(
                                        modifier = Modifier
                                            .background(style.accentColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = style.code,
                                            color = style.accentColor,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(3.dp))

                                Text(
                                    text = style.subtitle,
                                    color = NexoraTextSecondary,
                                    fontSize = 11.5.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Progress Bar and Chapter Ratio
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth(0.9f)
                                ) {
                                    LinearProgressIndicator(
                                        progress = { progress },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(5.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = style.accentColor,
                                        trackColor = NexoraSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "$completedCount/$totalCount ch",
                                        color = NexoraTextMuted,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Right Chevron
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "View Subject",
                            tint = NexoraTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // 4. Global Creator Credit Footer
            item {
                NexoraCreatorCredit()
            }
        }
    }
}
