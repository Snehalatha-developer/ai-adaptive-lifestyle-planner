package com.example.aiplanner.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aiplanner.ui.components.LoadingScreen
import com.example.aiplanner.ui.components.SectionCard
import com.example.aiplanner.utils.toDisplayString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onEditProfile: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("Settings") }) }) { innerPadding ->
        if (uiState.isLoading) {
            LoadingScreen(modifier = Modifier.padding(innerPadding))
            return@Scaffold
        }
        val profile = uiState.profile

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionCard(title = "Profile") {
                InfoRow("Name", profile.name.ifBlank { "—" })
                InfoRow("Wake-up", profile.wakeUpTime.toDisplayString())
                InfoRow("Sleep", profile.sleepTime.toDisplayString())
                InfoRow(
                    label = "Office hours",
                    value = "${profile.officeStartTime.toDisplayString()} – ${profile.officeEndTime.toDisplayString()}"
                )
                InfoRow("Commute", "${profile.commuteMinutes} min")
                InfoRow("Exercise", "${profile.exerciseMinutes} min")
                InfoRow("Study target", "${profile.studyTargetMinutes} min")
                InfoRow("Breakfast", profile.breakfastTime.toDisplayString())
                InfoRow("Lunch", profile.lunchTime.toDisplayString())
                InfoRow("Dinner", profile.dinnerTime.toDisplayString())
                InfoRow("Weekend", profile.weekendPreference.label)
            }

            if (profile.goals.isNotEmpty()) {
                SectionCard(title = "Goals") {
                    Text(profile.goals.joinToString(", "))
                }
            }

            Button(onClick = onEditProfile, modifier = Modifier.fillMaxWidth()) {
                Text("Edit Profile")
            }

            SectionCard(title = "About") {
                Text("AI Adaptive Lifestyle Planner", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Version 1.0 (Phase 1)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Notification preferences arrive in Phase 2.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
