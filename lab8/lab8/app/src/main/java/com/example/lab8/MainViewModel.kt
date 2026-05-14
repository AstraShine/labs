package com.example.lab8

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _tasks = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList())
    val tasks: StateFlow<List<Pair<String, Boolean>>> = _tasks.asStateFlow()

    fun addTask(task: String) {
        val currentList = _tasks.value.toMutableList()
        currentList.add(task to false)
        _tasks.value = currentList
    }

    fun deleteTask(index: Int) {
        val currentList = _tasks.value.toMutableList()
        if (index in currentList.indices) {
            currentList.removeAt(index)
            _tasks.value = currentList
        }
    }

    fun updateTaskCompletion(index: Int, isCompleted: Boolean) {
        val currentList = _tasks.value.toMutableList()
        if (index in currentList.indices) {
            currentList[index] = currentList[index].first to isCompleted
            _tasks.value = currentList
        }
    }

    fun loadTestData() {
        _tasks.value = listOf(
            "Купить продукты" to false,
            "Сделать ДЗ по Android" to false,
            "Позвонить маме" to false,
            "Записаться к врачу" to false
        )
    }

    fun updateTaskText(oldText: String, newText: String) {
        val currentList = _tasks.value.toMutableList()
        val index = currentList.indexOfFirst { it.first == oldText }
        if (index != -1) {
            currentList[index] = newText to currentList[index].second
            _tasks.value = currentList
        }
    }

    fun deleteTaskByText(taskText: String) {
        val currentList = _tasks.value.toMutableList()
        val index = currentList.indexOfFirst { it.first == taskText }
        if (index != -1) {
            currentList.removeAt(index)
            _tasks.value = currentList
        }
    }

    fun insertTaskAtPosition(position: Int, taskText: String) {
        val currentList = _tasks.value.toMutableList()
        // Вставляем задачу на прежнюю позицию с состоянием "не выполнена"
        currentList.add(position, taskText to false)
        _tasks.value = currentList
    }
}
