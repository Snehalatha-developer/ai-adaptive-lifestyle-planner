package com.example.aiplanner.ui.aiplanner

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.aiplanner.ui.components.EmptyState

/** Placeholder for the conversational "Ask AI" experience, implemented in Phase 5. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiPlannerScreen() {
    Scaffold(topBar = { TopAppBar(title = { Text("AI Planner") }) }) { innerPadding ->
        EmptyState(
            title = "Ask AI is coming soon",
            subtitle = "In Phase 5 you'll be able to type things like \"move my workout to evening\" " +
                "or \"I came home late\" and have your schedule adjust automatically.",
            icon = Icons.Filled.AutoAwesome,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
