package com.example.aiplanner.ui.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aiplanner.ui.components.LoadingScreen
import com.example.aiplanner.ui.components.SectionCard
import com.example.aiplanner.ui.theme.StatusCompleted
import com.example.aiplanner.ui.theme.StatusUpcoming

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(viewModel: ProgressViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("Today's Progress") }) }) { innerPadding ->
        if (uiState.isLoading) {
            LoadingScreen(modifier = Modifier.padding(innerPadding))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionCard(title = "Overall") {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { uiState.completionPercent / 100f },
                            modifier = Modifier.size(72.dp),
                            strokeWidth = 7.dp
                        )
                        Text("${uiState.completionPercent}%", style = MaterialTheme.typography.titleMedium)
                    }
                    Column {
                        Text("${uiState.completedCount}/${uiState.totalCount} tasks completed", style = MaterialTheme.typography.bodyMedium)
                        if (uiState.skippedCount > 0) {
                            Text(
                                text = "${uiState.skippedCount} skipped",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            SectionCard(title = "Exercise") {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = if (uiState.exerciseCompleted) Icons.Filled.Check else Icons.Filled.HourglassEmpty,
                        contentDescription = null,
                        tint = if (uiState.exerciseCompleted) StatusCompleted else StatusUpcoming
                    )
                    Text(if (uiState.exerciseCompleted) "Completed" else "Not done yet")
                }
            }

            SectionCard(title = "Study") {
                Text("${uiState.studyMinutesCompleted} / ${uiState.studyMinutesTarget} min")
            }

            SectionCard(title = "This week") {
                Text(
                    text = "Weekly consistency and history arrive in Phase 6, once more than one day of data is available.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
