package com.example.lab12.data.repository

import com.example.lab12.database.TaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


class InMemoryTaskRepository : TaskRepository {
    private var nextId: Long = 1L
    private val _tasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    private val tasks = _tasks.asStateFlow()

    override fun getAllTasks(): Flow<List<TaskEntity>> = tasks

    override fun getAllSortedTasks(): Flow<List<TaskEntity>> = tasks.map { taskList ->
        taskList.sortedWith(
            compareBy<TaskEntity> { it.isCompleted }
                .thenByDescending { it.createdTime }
        )
    }

    override suspend fun addTask(title: String) {
        val newTask = TaskEntity(
            id = nextId++,
            title = title,
            isCompleted = false,
            createdTime = System.currentTimeMillis()
        )
        val currentTasks = _tasks.value
        _tasks.value = currentTasks + newTask
    }

    override suspend fun deleteTask(task: TaskEntity) {
        val currentTasks = _tasks.value
        _tasks.value = currentTasks.filter { it.id != task.id }
    }

    override suspend fun updateTask(task: TaskEntity) {
        val currentTasks = _tasks.value
        _tasks.value = currentTasks.map { existingTask ->
            if (existingTask.id == task.id) task else existingTask
        }
    }

    override suspend fun toggleTaskCompletion(task: TaskEntity, isCompleted: Boolean) {
        val updatedTask = task.copy(isCompleted = isCompleted)
        updateTask(updatedTask)
    }

    override suspend fun deleteAllTasks() {
        _tasks.value = emptyList()
    }

    override suspend fun updateTaskByTitle(oldTitle: String, newTitle: String) {
        val currentTasks = _tasks.value
        val taskToUpdate = currentTasks.find { it.title == oldTitle }
        if (taskToUpdate != null) {
            val updatedTask = taskToUpdate.copy(title = newTitle)
            updateTask(updatedTask)
        }
    }

    override suspend fun deleteTaskByTitle(title: String) {
        val currentTasks = _tasks.value
        val taskToDelete = currentTasks.find { it.title == title }
        if (taskToDelete != null) {
            deleteTask(taskToDelete)
        }
    }

    override suspend fun getTasksOnce(): List<TaskEntity> {
        return tasks.first() // first() приостановится до первого элемента
    }
}
