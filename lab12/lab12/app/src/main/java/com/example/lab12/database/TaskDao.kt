package com.example.lab12.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY createdTime DESC")
    fun getAllTasks(): Flow<List<TaskEntity>> // Возвращаем Flow для реактивного обновления

    @Insert(onConflict = OnConflictStrategy.REPLACE) // При конфликте заменять
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks")
    suspend fun deleteAll()

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Query("UPDATE tasks SET title = :newTitle WHERE title = :oldTitle")
    suspend fun updateTaskByTitle(oldTitle: String, newTitle: String)

    @Query("DELETE FROM tasks WHERE title = :title")
    suspend fun deleteTaskByTitle(title: String)

    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, createdTime DESC")
    fun getAllTasksSortedByStatusAndDate(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE title LIKE :query ORDER BY isCompleted ASC, createdTime DESC")
    fun searchTasksByTitle(query: String): Flow<List<TaskEntity>>

}