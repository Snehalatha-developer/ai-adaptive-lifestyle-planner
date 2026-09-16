package com.example.aiplanner.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.aiplanner.domain.model.TaskCategory
import com.example.aiplanner.domain.model.TaskDisplayStatus
import com.example.aiplanner.ui.theme.CategoryExercise
import com.example.aiplanner.ui.theme.CategoryHealth
import com.example.aiplanner.ui.theme.CategoryMeal
import com.example.aiplanner.ui.theme.CategoryPersonal
import com.example.aiplanner.ui.theme.CategoryStudy
import com.example.aiplanner.ui.theme.CategoryWork
import com.example.aiplanner.ui.theme.StatusCompleted
import com.example.aiplanner.ui.theme.StatusCurrent
import com.example.aiplanner.ui.theme.StatusSkipped
import com.example.aiplanner.ui.theme.StatusUpcoming

/** Icon shown for each task category on the timeline and in task detail. */
fun TaskCategory.icon(): ImageVector = when (this) {
    TaskCategory.HEALTH -> Icons.Filled.Favorite
    TaskCategory.EXERCISE -> Icons.Filled.FitnessCenter
    TaskCategory.MEAL -> Icons.Filled.Restaurant
    TaskCategory.STUDY -> Icons.AutoMirrored.Filled.MenuBook
    TaskCategory.WORK -> Icons.Filled.Work
    TaskCategory.PERSONAL -> Icons.Filled.SelfImprovement
}

/** Accent color for each task category. */
fun TaskCategory.color(): Color = when (this) {
    TaskCategory.HEALTH -> CategoryHealth
    TaskCategory.EXERCISE -> CategoryExercise
    TaskCategory.MEAL -> CategoryMeal
    TaskCategory.STUDY -> CategoryStudy
    TaskCategory.WORK -> CategoryWork
    TaskCategory.PERSONAL -> CategoryPersonal
}

/** Color used for the timeline marker/status label of a task. */
fun TaskDisplayStatus.color(): Color = when (this) {
    TaskDisplayStatus.COMPLETED -> StatusCompleted
    TaskDisplayStatus.SKIPPED -> StatusSkipped
    TaskDisplayStatus.CURRENT -> StatusCurrent
    TaskDisplayStatus.UPCOMING -> StatusUpcoming
}
