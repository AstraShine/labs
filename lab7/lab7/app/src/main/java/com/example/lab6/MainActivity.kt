package com.example.lab6

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    private val tasks = mutableListOf<String>()
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTextTask = findViewById<EditText>(R.id.editTextTask)
        val buttonAddTask = findViewById<Button>(R.id.buttonAddTask)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewTasks)


        // Настройка RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TaskAdapter(tasks) { taskText, position ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("task_text", taskText)
            intent.putExtra("task_position", position)  // Передаём актуальную позицию
            startActivityForResult(intent, 1)
        }


        recyclerView.adapter = adapter

        // Добавление анимации
        val itemAnimator = DefaultItemAnimator()
        itemAnimator.addDuration = 300
        itemAnimator.removeDuration = 300
        recyclerView.itemAnimator = itemAnimator

        // Добавление задачи
        buttonAddTask.setOnClickListener {
            val task = editTextTask.text.toString()
            if (task.isNotBlank()) {
                tasks.add(task)
                adapter.notifyItemInserted(tasks.size - 1) // более эффективно, чем notifyDataSetChanged
                editTextTask.text.clear()
            } else {
                Toast.makeText(this, "Введите задачу", Toast.LENGTH_SHORT).show()
            }
        }

        // Восстановление данных при повороте
        if (savedInstanceState != null) {
            val savedTasks = savedInstanceState.getStringArrayList("tasks")
            if (savedTasks != null) {
                tasks.clear()
                tasks.addAll(savedTasks)
                adapter.notifyDataSetChanged()
            }
        }

        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(this, adapter))
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                1 -> {
                    // Обработка редактирования
                    if (data.hasExtra("edited_task")) {
                        val editedTask = data.getStringExtra("edited_task")!!
                        val originalPosition = data.getIntExtra("original_position", -1)

                        // Находим задачу по тексту, а не по позиции
                        val currentPosition = tasks.indexOfFirst { it == data.getStringExtra("original_task_text") }
                        if (currentPosition != -1) {
                            tasks[currentPosition] = editedTask
                            adapter.notifyItemChanged(currentPosition)
                        }
                    }

                    // Обработка удаления
                    if (data.hasExtra("delete_task_text")) {
                        val taskToDelete = data.getStringExtra("delete_task_text")!!
                        val positionToDelete = tasks.indexOf(taskToDelete)
                        if (positionToDelete != -1) {
                            tasks.removeAt(positionToDelete)
                            adapter.notifyItemRemoved(positionToDelete)
                        }
                    }
                }
            }
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("tasks", ArrayList(tasks))
    }

    private fun showEditDialog(position: Int, currentText: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Редактировать задачу")

        val input = EditText(this)
        input.setText(currentText)
        builder.setView(input)

        builder.setPositiveButton("Сохранить") { _, _ ->
            val newText = input.text.toString()
            if (newText.isNotBlank()) {
                tasks[position] = newText
                adapter.notifyItemChanged(position)
            } else {
                Toast.makeText(this, "Текст не может быть пустым", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }

        builder.show()
    }
}

