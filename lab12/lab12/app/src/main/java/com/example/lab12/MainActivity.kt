package com.example.lab12

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab12.data.repository.InMemoryTaskRepository
import com.example.lab12.ui.theme.MainViewModel
import com.example.lab12.database.AppDatabase
import com.example.lab12.data.repository.TaskRepositoryImpl
import kotlinx.coroutines.launch
import com.example.lab12.data.repository.TaskRepository
import com.example.lab12.ui.theme.TasksUiState



class MainActivity : AppCompatActivity() {
    private var useInMemoryRepository = false
    private lateinit var repository: TaskRepository
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: TaskAdapter

    companion object {
        private const val EDIT_TASK_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTextTask = findViewById<EditText>(R.id.editTextTask)
        val buttonAddTask = findViewById<Button>(R.id.buttonAddTask)
        val buttonToggleRepository = findViewById<Button>(R.id.buttonToggleRepository)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewTasks)

        // Инициализация начального репозитория и ViewModel
        updateRepositoryAndViewModel() // Сначала инициализируем репозиторий
        updateButtonText() // Затем устанавливаем начальный текст кнопки

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
        recyclerView.itemAnimator = DefaultItemAnimator().apply {
            addDuration = 300
            removeDuration = 300
            moveDuration = 300
            changeDuration = 300
        }


        // Подписка на изменения списка задач
        subscribeToTasks()

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

        // Кнопка переключения репозитория
        buttonToggleRepository.setOnClickListener {
            useInMemoryRepository = !useInMemoryRepository
            updateRepositoryAndViewModel()
            subscribeToTasks() // Переподписываемся на новые данные
            Toast.makeText(
                this,
                if (useInMemoryRepository) "Переключено на In‑Memory хранилище"
                else "Переключено на БД хранилище",
                Toast.LENGTH_SHORT
            ).show()
        }
        buttonToggleRepository.setOnClickListener {
            useInMemoryRepository = !useInMemoryRepository
            updateRepositoryAndViewModel()
            subscribeToTasks()
            Toast.makeText(
                this,
                if (useInMemoryRepository) "Переключено на In‑Memory хранилище"
                else "Переключено на БД хранилище",
                Toast.LENGTH_SHORT
            ).show()
            updateButtonText() // Обновляем текст после переключения
        }
        findViewById<Button>(R.id.buttonRefresh).setOnClickListener {
            viewModel.refresh()
        }
        findViewById<Button>(R.id.buttonRetry).setOnClickListener {
            viewModel.loadTasks()
        }

    }

    private fun updateRepositoryAndViewModel() {
        repository = if (useInMemoryRepository) {
            InMemoryTaskRepository()
        } else {
            TaskRepositoryImpl(AppDatabase.getInstance(this).taskDao())
        }
        viewModel = MainViewModel(repository)
        updateButtonText() // Обновляем текст кнопки
    }

    private fun updateButtonText() {
        val buttonToggleRepository = findViewById<Button>(R.id.buttonToggleRepository)
        buttonToggleRepository.text = if (useInMemoryRepository) {
            "Переключиться на БД"
        } else {
            "Переключиться на In‑Memory"
        }
    }


    private fun subscribeToTasks() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is TasksUiState.Loading -> {
                            findViewById<RecyclerView>(R.id.recyclerViewTasks).visibility =
                                View.GONE
                            findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
                            findViewById<TextView>(R.id.textError).visibility = View.GONE
                            findViewById<Button>(R.id.buttonRetry).visibility = View.GONE
                        }

                        is TasksUiState.Success -> {
                            findViewById<RecyclerView>(R.id.recyclerViewTasks).visibility =
                                View.VISIBLE
                            findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                            findViewById<TextView>(R.id.textError).visibility = View.GONE
                            findViewById<Button>(R.id.buttonRetry).visibility = View.GONE
                            adapter.updateData(state.tasks)
                        }

                        is TasksUiState.Error -> {
                            findViewById<RecyclerView>(R.id.recyclerViewTasks).visibility =
                                View.GONE
                            findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                            findViewById<TextView>(R.id.textError).visibility = View.VISIBLE
                            findViewById<TextView>(R.id.textError).text = state.message
                            findViewById<Button>(R.id.buttonRetry).visibility =
                                if (state.canRetry) View.VISIBLE else View.GONE
                            // Обновляем список, если есть сохранённые задачи
                            state.tasks?.let { adapter.updateData(it) }
                        }
                    }
                }
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