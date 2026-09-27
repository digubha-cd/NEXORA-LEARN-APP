package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Background and Surface Palette (Clean Light Canvas with Deep Navy Contrasts)
val NexoraBackground = Color(0xFFF8FAFC)        // Clean, bright off-white / light slate canvas
val NexoraSurface = Color(0xFFFFFFFF)           // Pure crisp white card surface
val NexoraSurfaceVariant = Color(0xFFF1F5F9)    // Soft subtle light grey/blue container
val NexoraSurfaceElevated = Color(0xFFFFFFFF)   // Pure white elevated card
val NexoraBorder = Color(0xFFE2E8F0)            // Subtle, elegant light border
val NexoraBorderGlow = Color(0x330284C7)        // Soft azure highlight border

// Deep Navy Hero Colors (for top contrast banner & header)
val NexoraNavyDark = Color(0xFF0B132B)          // Deep midnight navy
val NexoraNavyCard = Color(0xFF1C2541)          // Rich indigo-navy container
val NexoraNavySurface = Color(0xFF1E293B)       // Dark slate surface

// Brand Core Accents (Adjusted for high contrast and vibrance)
val NexoraCyan = Color(0xFF0284C7)              // Premium deep cyan / sky blue
val NexoraElectricBlue = Color(0xFF2563EB)      // Vibrant royal blue
val NexoraPurple = Color(0xFF7C3AED)            // Vivid violet purple
val NexoraLavender = Color(0xFF8B5CF6)          // Soft lavender
val NexoraMagenta = Color(0xFFC026D3)           // Deep rich magenta
val NexoraPink = Color(0xFFEC4899)              // Elegant deep rose pink
val NexoraGold = Color(0xFFF59E0B)              // Rich amber gold (contrast-safe on white)
val NexoraAmber = Color(0xFFD97706)

// Subject-Specific Accent Colors
val SubjectGujaratiColor = Color(0xFF2563EB)     // Royal Blue
val SubjectEnglishColor = Color(0xFF9333EA)      // Vibrant Violet
val SubjectSpCcColor = Color(0xFFEA580C)         // Warm Coral Orange
val SubjectBaColor = Color(0xFF0284C7)           // Tech Blue / Cyan
val SubjectStatsColor = Color(0xFF0284C7)        // Ocean Blue
val SubjectAccountsColor = Color(0xFFD97706)     // Gold / Amber
val SubjectEconColor = Color(0xFFDB2777)         // Rose Pink

// Text Colors (High-contrast dark navy / slate palette)
val NexoraTextPrimary = Color(0xFF0F172A)        // Deep navy/slate for crystal-clear readability
val NexoraTextSecondary = Color(0xFF475569)      // Balanced medium slate for secondary text
val NexoraTextMuted = Color(0xFF64748B)          // Refined muted slate for timestamps/captions
val NexoraTextLight = Color(0xFFF8FAFC)          // Light text for dark containers
val NexoraTextLightMuted = Color(0xFF94A3B8)     // Light muted text for dark containers

// Status & Semantic Colors
val NexoraSuccess = Color(0xFF10B981)           // Crisp emerald green
val NexoraWarning = Color(0xFFF59E0B)           // Warm amber
val NexoraError = Color(0xFFEF4444)             // Clean ruby red

// Brand Gradients
val NexoraLogoGradient = Brush.linearGradient(
    colors = listOf(NexoraCyan, NexoraElectricBlue, NexoraPurple, NexoraMagenta)
)

val NexoraHeroGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF1E3A8A))
)

val NexoraPointsCardGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6), Color(0xFF6366F1))
)

val NexoraButtonGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF0284C7), Color(0xFF2563EB), Color(0xFF7C3AED))
)

val NexoraCardGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFFFFFFF), Color(0xFFF8FAFC))
)

val NexoraUserRankGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFEDE9FE), Color(0xFFE0E7FF), Color(0xFFF0FDF4))
)

val NexoraGlobeAtmosphere = Brush.verticalGradient(
    colors = listOf(Color(0x000284C7), Color(0x180284C7), Color(0x302563EB))
)


