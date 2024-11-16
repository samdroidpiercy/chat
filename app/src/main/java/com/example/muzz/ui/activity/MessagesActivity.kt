package com.example.muzz.ui.activity

import ChatScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.muzz.ui.components.AppTopBar
import com.example.muzz.ui.theme.MuzzTheme
import com.example.muzz.ui.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main activity for the chat feature.
 *
 * This activity serves as the entry point for the chat screen, setting up the theme,
 * top app bar, and chat UI. It uses Jetpack Compose for the UI and integrates with Hilt for
 * dependency injection of the ViewModel.
 */
@AndroidEntryPoint
class MessagesActivity : ComponentActivity() {

    // Injects the ChatViewModel using Hilt and Jetpack's viewModels delegate
    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Apply the app's theme
            MuzzTheme {
                // Scaffold provides the structure for the app's layout
                Scaffold(
                    modifier = Modifier.background(MaterialTheme.colorScheme.background), // Set background color
                    topBar = { AppTopBar() } // Displays the app's top bar
                ) { padding ->
                    // The main chat screen, with padding applied from the Scaffold
                    ChatScreen(chatViewModel, padding)
                }
            }
        }
    }
}