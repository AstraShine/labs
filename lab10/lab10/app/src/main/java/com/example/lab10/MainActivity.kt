package com.example.lab10

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab10.ui.theme.MainViewModel
import com.example.lab10.database.AppDatabase
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    // Получаем базу данных
    private val database by lazy { AppDatabase.getInstance(this) }
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(database)
    }
    private lateinit var adapter: TaskAdapter


    companion object {
        private const val EDIT_TASK_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTextTask = findViewById<EditText>(R.id.editTextTask)
        val buttonAddTask = findViewById<Button>(R.id.buttonAddTask)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewTasks)

        // Настройка RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TaskAdapter(
            tasks = emptyList(),
            onItemClick = { task ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("task_text", task.title)
                startActivityForResult(intent, EDIT_TASK_REQUEST_CODE)
            },
            onItemLongClick = { task ->
                viewModel.deleteTask(task)
                Toast.makeText(this, "Задача удалена", Toast.LENGTH_SHORT).show()
            },
            onCheckChange = { task, isChecked ->
                viewModel.toggleTaskCompletion(task, isChecked)
            }
        )
        recyclerView.adapter = adapter

        // Подписка на изменения списка задач
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasks.collect { tasks ->
                    adapter.updateData(tasks)
                }
            }
        }

        // Добавление задачи
        buttonAddTask.setOnClickListener {
            val task = editTextTask.text.toString()
            if (task.isNotBlank()) {
                viewModel.addTask(task)
                editTextTask.text.clear()
            } else {
                Toast.makeText(this, "Введите задачу", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == EDIT_TASK_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val action = data.getStringExtra("action")
            val dataString = data.getStringExtra("data")

            when (action) {
                "edit" -> {
                    val parts = dataString?.split("|")
                    if (parts != null && parts.size == 2) {
                        val oldText = parts[0]
                        val newText = parts[1]
                        if (oldText.isNotBlank() && newText.isNotBlank()) {
                            viewModel.updateTask(oldText, newText)
                        } else {
                            Toast.makeText(this, "Некорректные данные для редактирования", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Ошибка при разборе данных", Toast.LENGTH_SHORT).show()
                    }
                }
                "delete" -> {
                    if (!dataString.isNullOrEmpty()) {
                        viewModel.deleteTaskByText(dataString)
                    } else {
                        Toast.makeText(this, "Не удалось получить текст задачи для удаления", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}