package com.example.todoapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.collections.mutableListOf

class MainActivity : AppCompatActivity() {
    private var counter = 0
    private val tasks = mutableListOf<String>() // Выносим за onCreate для доступа во всех методах

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textCounter = findViewById<TextView>(R.id.textCounter)
        val buttonIncrement = findViewById<Button>(R.id.buttonIncrement)
        val buttonReset = findViewById<Button>(R.id.buttonReset)
        val textTaskCount = findViewById<TextView>(R.id.textTaskCount)

        // Восстанавливаем данные, если есть сохранённое состояние
        if (savedInstanceState != null) {
            counter = savedInstanceState.getInt("counter", 0)
            val savedTasks = savedInstanceState.getStringArrayList("tasks")
            if (savedTasks != null) {
                tasks.clear()
                tasks.addAll(savedTasks)
            }
        }

        updateCounterDisplay(textCounter)
        updateTasksDisplay(findViewById(R.id.textTasks), tasks)
        updateTaskCountDisplay(textTaskCount, tasks)

        buttonIncrement.setOnClickListener {
            counter++
            updateCounterDisplay(textCounter)
        }

        buttonReset.setOnClickListener {
            counter = 0
            updateCounterDisplay(textCounter)
            Toast.makeText(this, "Счётчик сброшен", Toast.LENGTH_SHORT).show()
        }

        val editTextInput = findViewById<EditText>(R.id.editTextInput)
        val buttonShow = findViewById<Button>(R.id.buttonShow)
        val textEntered = findViewById<TextView>(R.id.textEntered)

        buttonShow.setOnClickListener {
            val inputText = editTextInput.text.toString()
            textEntered.text = getString(R.string.label_entered) + " $inputText"
        }

        val editTextTask = findViewById<EditText>(R.id.editTextTask)
        val buttonAddTask = findViewById<Button>(R.id.buttonAddTask)
        val textTasks = findViewById<TextView>(R.id.textTasks)
        val buttonRemoveLast = findViewById<Button>(R.id.buttonRemoveLast)

        buttonAddTask.setOnClickListener {
            val task = editTextTask.text.toString()
            if (task.isNotBlank()) {
                tasks.add(task)
                updateTasksDisplay(textTasks, tasks)
                updateTaskCountDisplay(textTaskCount, tasks)
                editTextTask.text.clear()
            } else {
                Toast.makeText(this, "Введите задачу", Toast.LENGTH_SHORT).show()
            }
        }

        buttonRemoveLast.setOnClickListener {
            if (tasks.isNotEmpty()) {
                val removedTask = tasks.removeAt(tasks.size - 1)
                updateTasksDisplay(textTasks, tasks)
                updateTaskCountDisplay(textTaskCount, tasks)
                Toast.makeText(
                    this,
                    "Удалена задача: $removedTask",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, "Нет задач для удаления", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("counter", counter)
        outState.putStringArrayList("tasks", ArrayList(tasks))
    }

    private fun updateCounterDisplay(textView: TextView) {
        textView.text = getString(R.string.counter_text, counter)
    }

    private fun updateTasksDisplay(textTasks: TextView, tasks: List<String>) {
        if (tasks.isEmpty()) {
            textTasks.text = getString(R.string.label_tasks)
        } else {
            textTasks.text = tasks.joinToString("\n• ") { "• $it" }
        }
    }
    private fun updateTaskCountDisplay(textTaskCount: TextView, tasks: List<String>) {
        textTaskCount.text = "Задач: ${tasks.size}"
    }
}

