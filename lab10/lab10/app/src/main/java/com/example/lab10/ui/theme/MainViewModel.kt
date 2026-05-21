package com.example.lab10.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab10.database.AppDatabase
import com.example.lab10.database.TaskEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val database: AppDatabase
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList())
    private val taskDao = database.taskDao()

    // Получаем Flow из DAO и преобразуем в StateFlow для Compose/UI
    val tasks: StateFlow<List<TaskEntity>> = taskDao.getAllTasksSortedByStatusAndDate()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTask(title: String) {
        viewModelScope.launch {
            val task = TaskEntity(title = title)
            taskDao.insertTask(task)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            taskDao.deleteTask(task)
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            taskDao.updateTask(task)
        }
    }

    fun toggleTaskCompletion(task: TaskEntity, isCompleted: Boolean) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = isCompleted)
            taskDao.updateTask(updatedTask)
        }
    }

    fun deleteAllTasks() {
        viewModelScope.launch {
            taskDao.deleteAll()
        }
    }

    fun updateTask(oldTitle: String, newTitle: String) {
        viewModelScope.launch {
            taskDao.updateTaskByTitle(oldTitle, newTitle)
        }
    }

    fun deleteTaskByText(title: String) {
        viewModelScope.launch {
            taskDao.deleteTaskByTitle(title)
        }
    }
}