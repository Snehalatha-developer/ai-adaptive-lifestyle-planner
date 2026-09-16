package com.example.aiplanner.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aiplanner.domain.model.ScheduleTask
import com.example.aiplanner.domain.model.TaskDisplayStatus
import com.example.aiplanner.ui.components.EmptyState
import com.example.aiplanner.ui.components.LoadingScreen
import com.example.aiplanner.ui.components.color
import com.example.aiplanner.ui.components.icon
import com.example.aiplanner.utils.toDisplayString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { message -> snackbarHostState.showSnackbar(message) }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Today's Schedule") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingScreen(modifier = Modifier.padding(innerPadding))
            uiState.tasks.isEmpty() -> EmptyState(
                title = "No schedule yet",
                subtitle = "Finish onboarding or tap Regenerate Schedule on Home to see today's plan.",
                modifier = Modifier.padding(innerPadding)
            )
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp)
            ) {
                itemsIndexed(uiState.tasks, key = { _, task -> task.id }) { index, task ->
                    TimelineRow(
                        task = task,
                        displayStatus = task.displayStatus(uiState.now),
                        isLast = index == uiState.tasks.lastIndex,
                        onClick = { viewModel.selectTask(task.id) }
                    )
                }
            }
        }
    }

    val selected = uiState.selectedTask
    if (selected != null) {
        TaskDetailBottomSheet(
            task = selected,
            onDismiss = { viewModel.selectTask(null) },
            onDone = { viewModel.completeSelectedTask() },
            onSkip = { viewModel.skipSelectedTask() },
            onRemind = { viewModel.remindLater() },
            onReschedule = { viewModel.reschedule() }
        )
    }
}

@Composable
private fun TimelineRow(
    task: ScheduleTask,
    displayStatus: TaskDisplayStatus,
    isLast: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(24.dp)) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(displayStatus.color())
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier
            .weight(1f)
            .padding(bottom = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(
                    imageVector = task.category.icon(),
                    contentDescription = null,
                    tint = task.category.color(),
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = task.startTime.toDisplayString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = displayStatus.label(),
                    style = MaterialTheme.typography.labelSmall,
                    color = displayStatus.color()
                )
            }
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                textDecoration = if (displayStatus == TaskDisplayStatus.SKIPPED) {
                    TextDecoration.LineThrough
                } else {
                    TextDecoration.None
                }
            )
            if (task.durationMinutes > 0) {
                Text(
                    text = "${task.durationMinutes} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun TaskDisplayStatus.label(): String = when (this) {
    TaskDisplayStatus.COMPLETED -> "Done"
    TaskDisplayStatus.SKIPPED -> "Skipped"
    TaskDisplayStatus.CURRENT -> "Now"
    TaskDisplayStatus.UPCOMING -> "Upcoming"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDetailBottomSheet(
    task: ScheduleTask,
    onDismiss: () -> Unit,
    onDone: () -> Unit,
    onSkip: () -> Unit,
    onRemind: () -> Unit,
    onReschedule: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(task.category.icon(), contentDescription = null, tint = task.category.color())
                Column {
                    Text(task.title, style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = "${task.startTime.toDisplayString()} – ${task.endTime.toDisplayString()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            task.description?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onDone, modifier = Modifier.weight(1f)) { Text("Done") }
                OutlinedButton(onClick = onSkip, modifier = Modifier.weight(1f)) { Text("Skip") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onRemind, modifier = Modifier.weight(1f)) { Text("Remind in 10 min") }
                OutlinedButton(onClick = onReschedule, modifier = Modifier.weight(1f)) { Text("Reschedule") }
            }
        }
    }
}
