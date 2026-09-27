package com.example.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.core.storage.StudentPreferences
import com.example.ui.screens.MainAppScreen

object NexoraDestinations {
    const val MAIN = "main"
}

/**
 * Main Navigation Host opening directly into the main NEXORA LEARN experience.
 * No login screen, no authentication gate, no billing/payment gates.
 */
@Composable
fun NexoraNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val observedProfile by StudentPreferences.currentProfile.collectAsState()

    NavHost(
        navController = navController,
        startDestination = NexoraDestinations.MAIN,
        modifier = modifier
    ) {
        // Main App Experience (Home, Subjects, Planner, Exam, Profile)
        composable(
            route = NexoraDestinations.MAIN,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            val activeProfile = observedProfile ?: StudentPreferences.getStudentProfile(context)
            MainAppScreen(
                studentProfile = activeProfile
            )
        }
    }
}

