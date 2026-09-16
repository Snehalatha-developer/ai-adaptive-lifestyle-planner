package com.example.aiplanner.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aiplanner.R
import com.example.aiplanner.domain.model.LifestyleGoals
import com.example.aiplanner.domain.model.WeekendPreference
import com.example.aiplanner.ui.components.LabeledTimeField
import com.example.aiplanner.ui.components.MultiChoiceChipGroup
import com.example.aiplanner.ui.components.NumberStepperField
import com.example.aiplanner.ui.components.SectionCard
import com.example.aiplanner.ui.components.SingleChoiceChipGroup

/**
 * Collects/edits the user's lifestyle profile. Used both as the first-run onboarding flow
 * ([isEditMode] = false, no back button, completing navigates into the app) and as the
 * "Edit Profile" screen reached from Settings ([isEditMode] = true, completing pops back).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    isEditMode: Boolean,
    onComplete: () -> Unit,
    onBack: (() -> Unit)? = null,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { viewModel.loadExistingProfileIfNeeded() }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        topBar = {
            if (isEditMode) {
                TopAppBar(
                    title = { Text("Edit Profile") },
                    navigationIcon = {
                        if (onBack != null) {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        val form = uiState.form
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isEditMode) {
                item {
                    Column {
                        Text(stringResource(R.string.onboarding_title), style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.onboarding_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                SectionCard(title = "About you") {
                    OutlinedTextField(
                        value = form.name,
                        onValueChange = { value -> viewModel.updateForm { it.copy(name = value) } },
                        label = { Text("Name") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                SectionCard(title = "Sleep & Wake") {
                    LabeledTimeField(
                        label = "Wake-up time",
                        time = form.wakeUpTime,
                        onTimeSelected = { value -> viewModel.updateForm { it.copy(wakeUpTime = value) } }
                    )
                    LabeledTimeField(
                        label = "Preferred sleep time",
                        time = form.sleepTime,
                        onTimeSelected = { value -> viewModel.updateForm { it.copy(sleepTime = value) } }
                    )
                }
            }

            item {
                SectionCard(title = "Work") {
                    LabeledTimeField(
                        label = "Office start time",
                        time = form.officeStartTime,
                        onTimeSelected = { value -> viewModel.updateForm { it.copy(officeStartTime = value) } }
                    )
                    LabeledTimeField(
                        label = "Office end time",
                        time = form.officeEndTime,
                        onTimeSelected = { value -> viewModel.updateForm { it.copy(officeEndTime = value) } }
                    )
                    NumberStepperField(
                        label = "Commute duration",
                        value = form.commuteMinutes,
                        onValueChange = { value -> viewModel.updateForm { it.copy(commuteMinutes = value) } },
                        maxValue = 180,
                        step = 5
                    )
                }
            }

            item {
                SectionCard(title = "Activities") {
                    NumberStepperField(
                        label = "Exercise duration",
                        value = form.exerciseMinutes,
                        onValueChange = { value -> viewModel.updateForm { it.copy(exerciseMinutes = value) } },
                        maxValue = 180,
                        step = 5
                    )
                    NumberStepperField(
                        label = "Study target per day",
                        value = form.studyTargetMinutes,
                        onValueChange = { value -> viewModel.updateForm { it.copy(studyTargetMinutes = value) } },
                        maxValue = 480,
                        step = 15
                    )
                }
            }

            item {
                SectionCard(title = "Meals") {
                    LabeledTimeField(
                        label = "Breakfast",
                        time = form.breakfastTime,
                        onTimeSelected = { value -> viewModel.updateForm { it.copy(breakfastTime = value) } }
                    )
                    LabeledTimeField(
                        label = "Lunch",
                        time = form.lunchTime,
                        onTimeSelected = { value -> viewModel.updateForm { it.copy(lunchTime = value) } }
                    )
                    LabeledTimeField(
                        label = "Dinner",
                        time = form.dinnerTime,
                        onTimeSelected = { value -> viewModel.updateForm { it.copy(dinnerTime = value) } }
                    )
                }
            }

            item {
                SectionCard(title = "Weekend preference") {
                    SingleChoiceChipGroup(
                        options = WeekendPreference.entries,
                        selected = form.weekendPreference,
                        label = { it.label },
                        onSelect = { value -> viewModel.updateForm { it.copy(weekendPreference = value) } }
                    )
                }
            }

            item {
                SectionCard(title = "Lifestyle goals") {
                    MultiChoiceChipGroup(
                        options = LifestyleGoals.PREDEFINED,
                        selected = form.goals,
                        onToggle = { goal ->
                            viewModel.updateForm {
                                val newGoals = if (goal in it.goals) it.goals - goal else it.goals + goal
                                it.copy(goals = newGoals)
                            }
                        }
                    )
                }
            }

            item {
                Button(
                    onClick = { viewModel.save(onComplete) },
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isEditMode) stringResource(R.string.onboarding_update_cta)
                        else stringResource(R.string.onboarding_save_cta)
                    )
                }
            }
        }
    }
}
