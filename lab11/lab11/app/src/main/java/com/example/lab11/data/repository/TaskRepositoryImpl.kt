package com.example.lab11.data.repository

import com.example.lab11.database.TaskDao
import com.example.lab11.database.TaskEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepositoryImpl(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()

    override fun getAllSortedTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasksSortedByStatusAndDate()

    override suspend fun addTask(title: String) {
        val task = TaskEntity(title = title)
        taskDao.insertTask(task)
    }

    override suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }

    override suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    override suspend fun toggleTaskCompletion(task: TaskEntity, isCompleted: Boolean) {
        val updatedTask = task.copy(isCompleted = isCompleted)
        taskDao.updateTask(updatedTask)
    }

    override suspend fun deleteAllTasks() {
        taskDao.deleteAll()
    }

    override suspend fun updateTaskByTitle(oldTitle: String, newTitle: String) {
        taskDao.updateTaskByTitle(oldTitle, newTitle)
    }

    override suspend fun deleteTaskByTitle(title: String) {
        taskDao.deleteTaskByTitle(title)
    }
}