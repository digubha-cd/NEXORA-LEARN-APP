package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.storage.StudentPreferences
import com.example.ui.navigation.NexoraNavHost
import com.example.ui.theme.NexoraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.core.firebase.FirebaseInitHelper.ensureInitialized(applicationContext)
        com.example.core.timer.StudyTimerManager.initialize(applicationContext)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by StudentPreferences.isDarkModeFlow.collectAsStateWithLifecycle(
                initialValue = StudentPreferences.isDarkMode(applicationContext)
            )
            NexoraTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NexoraNavHost()
                }
            }
        }
    }
}

