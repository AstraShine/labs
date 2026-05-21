package com.example.lab10

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lab10.database.AppDatabase
import com.example.lab10.ui.theme.MainViewModel

class MainViewModelFactory(
    private val database: AppDatabase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}