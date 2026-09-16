package com.example.aiplanner.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aiplanner.ui.components.LoadingScreen
import com.example.aiplanner.ui.components.SectionCard
import com.example.aiplanner.ui.components.color
import com.example.aiplanner.ui.components.icon
import com.example.aiplanner.utils.toDisplayString

@Composable
fun HomeScreen(
    onViewSchedule: () -> Unit,
    onOpenAiPlanner: () -> Unit,
    onOpenProgress: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { message -> snackbarHostState.showSnackbar(message) }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        if (uiState.isLoading) {
            LoadingScreen(modifier = Modifier.padding(innerPadding))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column {
                Text("Good Morning, ${uiState.userName}", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = uiState.todayDateDisplay,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            SectionCard(title = "Today's completion") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { uiState.completionPercent / 100f },
                            modifier = Modifier.size(64.dp),
                            strokeWidth = 6.dp
                        )
                        Text("${uiState.completionPercent}%", style = MaterialTheme.typography.titleMedium)
                    }
                    Text(
                        text = "${uiState.completedCount} of ${uiState.totalCount} tasks completed",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            SectionCard(title = "Next Activity") {
                val next = uiState.nextTask
                if (next == null) {
                    Text("Nothing left on today's schedule. Nice work!", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(next.category.icon(), contentDescription = null, tint = next.category.color())
                        Column {
                            Text(next.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "${next.startTime.toDisplayString()} • ${next.durationMinutes} minutes",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    Text(
                        text = if (uiState.nextTaskInProgress) {
                            "Happening now"
                        } else {
                            "Starts in ${uiState.nextTaskMinutesUntil} minutes"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onViewSchedule, modifier = Modifier.fillMaxWidth()) {
                    Text("View Full Schedule")
                }
                OutlinedButton(onClick = onOpenAiPlanner, modifier = Modifier.fillMaxWidth()) {
                    Text("Ask AI")
                }
                OutlinedButton(onClick = { viewModel.regenerateSchedule() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Regenerate Schedule")
                }
                OutlinedButton(onClick = onOpenProgress, modifier = Modifier.fillMaxWidth()) {
                    Text("Progress")
                }
            }
        }
    }
}
