package com.example.lab12.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab12.data.repository.TaskRepository
import com.example.lab12.database.TaskEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TasksUiState>(TasksUiState.Loading)
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            _uiState.value = TasksUiState.Loading
            try {
                delay(2000) // симуляция задержки
                val tasks = repository.getTasksOnce()
                _uiState.value = TasksUiState.Success(tasks)
            } catch (e: Exception) {
                _uiState.value = TasksUiState.Error(
                    message = e.message ?: "Ошибка загрузки",
                    canRetry = true
                )
            }
        }
    }

    fun addTask(title: String) {
        // Сохраняем текущее состояние для использования в catch
        val currentState = _uiState.value

        // Оптимистично добавляем задачу в текущий список
        _uiState.update { state ->
            when (state) {
                is TasksUiState.Success -> {
                    val newTasks = state.tasks + TaskEntity(
                        title = title,
                        isCompleted = false,
                        createdTime = System.currentTimeMillis()
                    )
                    TasksUiState.Success(tasks = newTasks)
                }
                is TasksUiState.Error -> {
                    // Если были ошибки, но есть сохранённый список — используем его
                    val newTasks = state.tasks?.plus(TaskEntity(
                        title = title,
                        isCompleted = false,
                        createdTime = System.currentTimeMillis()
                    )) ?: emptyList()
                    TasksUiState.Success(tasks = newTasks)
                }
                else -> state // Обязательно: возвращаем исходное состояние, если тип не распознан
            }
        }

// Запускаем фоновую операцию
        viewModelScope.launch {
            try {
                repository.addTask(title)
                // Успех: ничего не делаем — список уже обновлён оптимистично
            } catch (e: Exception) {
                // Откат при ошибке
                _uiState.update { state ->
                    when (state) {
                        is TasksUiState.Success -> {
                            // Удаляем оптимистично добавленную задачу
                            val newTasks = state.tasks.filter { it.title != title }
                            TasksUiState.Success(tasks = newTasks)
                        }
                        else -> state // Возвращаем исходное состояние
                    }
                }
// Показываем ошибку пользователю
                _uiState.value = TasksUiState.Error(
                    message = "Не удалось добавить задачу: ${e.message}",
                    canRetry = true,
                    tasks = (currentState as? TasksUiState.Success)?.tasks
                )
            }
        }
    }

    fun deleteTask(task: TaskEntity) {
        // Сохраняем текущее состояние
        val currentState = _uiState.value

        // Оптимистично удаляем задачу из списка
        _uiState.update { state ->
            when (state) {
                is TasksUiState.Success -> {
                    val newTasks = state.tasks.filter { it.id != task.id }
                    TasksUiState.Success(tasks = newTasks)
                }
                else -> state
            }
        }

        // Запускаем фоновую операцию удаления
        viewModelScope.launch {
            try {
                repository.deleteTask(task)
                // Успех: список уже обновлён оптимистично, ничего делать не нужно
            } catch (e: Exception) {
                // Откат: возвращаем задачу обратно в список
                _uiState.update { state ->
                    when (state) {
                        is TasksUiState.Success -> {
                            val newTasks = state.tasks + task
                            TasksUiState.Success(tasks = newTasks)
                        }
                        else -> state
                    }
                }
// Показываем ошибку
                _uiState.value = TasksUiState.Error(
                    message = "Не удалось удалить задачу: ${e.message}",
                    canRetry = true,
                    tasks = (currentState as? TasksUiState.Success)?.tasks
                )
            }
        }
    }

    fun toggleTaskCompletion(task: TaskEntity, isCompleted: Boolean) {
        // Сохраняем текущее состояние
        val currentState = _uiState.value

        // Оптимистично обновляем статус задачи
        _uiState.update { state ->
            when (state) {
                is TasksUiState.Success -> {
                    val newTasks = state.tasks.map { currentTask ->
                        if (currentTask.id == task.id) {
                            currentTask.copy(isCompleted = isCompleted)
                        } else {
                            currentTask
                        }
                    }
                    TasksUiState.Success(tasks = newTasks)
                }
                else -> state
            }
        }

// Запускаем фоновую операцию обновления статуса
        viewModelScope.launch {
            try {
                repository.toggleTaskCompletion(task, isCompleted)
                // Успех: список уже обновлён оптимистично, ничего делать не нужно
            } catch (e: Exception) {
                // Откат: восстанавливаем предыдущий статус
                _uiState.update { state ->
                    when (state) {
                        is TasksUiState.Success -> {
                            val newTasks = state.tasks.map { currentTask ->
                                if (currentTask.id == task.id) {
                                    currentTask.copy(isCompleted = !isCompleted)
                                } else {
                                    currentTask
                                }
                            }
                            TasksUiState.Success(tasks = newTasks)
                        }
                        else -> state
                    }
                }
// Показываем ошибку
                _uiState.value = TasksUiState.Error(
                    message = "Не удалось обновить статус задачи: ${e.message}",
                    canRetry = true,
                    tasks = (currentState as? TasksUiState.Success)?.tasks
                )
            }
        }
    }

    fun refresh() {
        loadTasks()
    }

    fun updateTask(oldTitle: String, newTitle: String) {
        viewModelScope.launch {
            repository.updateTaskByTitle(oldTitle, newTitle)
        }
    }

    fun deleteTaskByText(title: String) {
        viewModelScope.launch {
            repository.deleteTaskByTitle(title)
        }
    }
}
