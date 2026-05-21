package com.example.lab12.ui.theme

import com.example.lab12.database.TaskEntity

sealed class TasksUiState {
    object Loading : TasksUiState()
    data class Success(val tasks: List<TaskEntity>) : TasksUiState()
    data class Error(
        val message: String,
        val canRetry: Boolean = true,
        val tasks: List<TaskEntity>? = null // Сохраняем текущий список
    ) : TasksUiState()
}
