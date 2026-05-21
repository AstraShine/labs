package com.example.lab12.data.repository

import com.example.lab12.database.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<TaskEntity>>
    fun getAllSortedTasks(): Flow<List<TaskEntity>>
    suspend fun addTask(title: String)
    suspend fun deleteTask(task: TaskEntity)
    suspend fun updateTask(task: TaskEntity)
    suspend fun toggleTaskCompletion(task: TaskEntity, isCompleted: Boolean)
    suspend fun deleteAllTasks()
    suspend fun updateTaskByTitle(oldTitle: String, newTitle: String)
    suspend fun deleteTaskByTitle(title: String)

    suspend fun getTasksOnce(): List<TaskEntity>
}