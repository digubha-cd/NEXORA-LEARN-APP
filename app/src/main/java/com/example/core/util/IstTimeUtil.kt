package com.example.core.util

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * Data class representing precise remaining time for exam countdowns (Days, Hours, Minutes, Seconds).
 */
data class ExamCountdownTime(
    val days: Long,
    val hours: Long,
    val minutes: Long,
    val seconds: Long,
    val isExamDay: Boolean
)

/**
 * Utility for India Standard Time (IST - UTC+05:30) calculations and formatting.
 * Timezone: "Asia/Kolkata".
 */
object IstTimeUtil {
    val IST_ZONE_ID: ZoneId = ZoneId.of("Asia/Kolkata")

    private val TIME_FORMATTER_12H: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.ENGLISH)
    private val TIME_FORMATTER_24H: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ENGLISH)
    private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)

    /**
     * Returns the current ZonedDateTime in India Standard Time (Asia/Kolkata).
     */
    fun getCurrentZonedDateTime(): ZonedDateTime {
        return ZonedDateTime.now(IST_ZONE_ID)
    }

    /**
     * Returns the current LocalDate in India Standard Time (Asia/Kolkata).
     */
    fun getCurrentLocalDate(): LocalDate {
        return getCurrentZonedDateTime().toLocalDate()
    }

    /**
     * Returns formatted IST time string in 12-hour format with seconds (e.g., "04:32:18 PM").
     */
    fun getFormattedTime12h(zdt: ZonedDateTime = getCurrentZonedDateTime()): String {
        return zdt.format(TIME_FORMATTER_12H)
    }

    /**
     * Returns formatted IST time string in 24-hour format with seconds (e.g., "16:32:18").
     */
    fun getFormattedTime24h(zdt: ZonedDateTime = getCurrentZonedDateTime()): String {
        return zdt.format(TIME_FORMATTER_24H)
    }

    /**
     * Returns formatted IST date string (e.g., "27 Sep 2026").
     */
    fun getFormattedDate(zdt: ZonedDateTime = getCurrentZonedDateTime()): String {
        return zdt.format(DATE_FORMATTER)
    }

    /**
     * Calculates precise days, hours, minutes, and seconds remaining until the target exam date in IST.
     */
    fun calculateExamCountdown(targetDate: LocalDate, currentZdt: ZonedDateTime = getCurrentZonedDateTime()): ExamCountdownTime {
        val targetZdt = targetDate.atStartOfDay(IST_ZONE_ID)
        val totalSeconds = ChronoUnit.SECONDS.between(currentZdt, targetZdt)
        if (totalSeconds <= 0) {
            return ExamCountdownTime(0, 0, 0, 0, true)
        }
        val days = totalSeconds / (24 * 3600)
        val hours = (totalSeconds % (24 * 3600)) / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return ExamCountdownTime(days, hours, minutes, seconds, false)
    }
}
