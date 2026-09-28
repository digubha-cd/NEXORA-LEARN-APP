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

import androidx.compose.runtime.remember
import com.example.ui.screens.StudentIdentitySetupScreen

object NexoraDestinations {
    const val SETUP = "student_identity_setup"
    const val MAIN = "main"
}

/**
 * Main Navigation Host for NEXORA LEARN.
 * First launch: Opens simple Student Identity Setup (Name + Unique ID).
 * Subsequent launches: Opens directly into MainAppScreen (Home) with no gates or logins.
 */
@Composable
fun NexoraNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val observedProfile by StudentPreferences.currentProfile.collectAsState()
    val isSetupDone = remember { StudentPreferences.hasCompletedSetup(context) }
    val initialDestination = if (isSetupDone) NexoraDestinations.MAIN else NexoraDestinations.SETUP

    NavHost(
        navController = navController,
        startDestination = initialDestination,
        modifier = modifier
    ) {
        // One-time First-Launch Student Identity Setup
        composable(
            route = NexoraDestinations.SETUP,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            StudentIdentitySetupScreen(
                onSetupComplete = {
                    navController.navigate(NexoraDestinations.MAIN) {
                        popUpTo(NexoraDestinations.SETUP) { inclusive = true }
                    }
                }
            )
        }

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

