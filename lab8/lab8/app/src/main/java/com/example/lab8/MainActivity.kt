package com.example.lab8

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.Collections.emptyList


class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTextTask = findViewById<EditText>(R.id.editTextTask)
        val buttonAddTask = findViewById<Button>(R.id.buttonAddTask)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewTasks)


        adapter = TaskAdapter(
            tasks = emptyList(),
            onItemClick = { position ->
                val taskText = viewModel.tasks.value[position].first
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("task_text", taskText)
                detailActivityResultLauncher.launch(intent)
            },
            onItemLongClick = { position ->
                viewModel.deleteTask(position)
                Toast.makeText(this, "Задача удалена", Toast.LENGTH_SHORT).show()
            },
            onCheckChanged = { position, isChecked ->
                viewModel.updateTaskCompletion(position, isChecked)
            }
        )

        // Настройка RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = adapter

        // Создаём ItemTouchHelper с кастомным callback
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, // Не поддерживаем drag-and-drop
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT // Поддерживаем свайп влево и вправо
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false // Drag не поддерживаем

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val taskToDelete = viewModel.tasks.value[position]

                // Сохраняем данные удаляемой задачи для возможности отмены
                val deletedTask = taskToDelete.first

                // Удаляем задачу через ViewModel
                viewModel.deleteTask(position)

                // Показываем Snackbar с возможностью отмены
                showUndoSnackbar(deletedTask, position)
            }

            // Визуальное оформление при свайпе (опционально)
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                // Подсвечиваем фон при свайпе
                val itemView = viewHolder.itemView
                val background = ColorDrawable(Color.RED)
                background.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                background.draw(c)
            }
        })

// Прикрепляем ItemTouchHelper к RecyclerView
        itemTouchHelper.attachToRecyclerView(recyclerView)


        // Подписка на изменения списка задач
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasks.collect { tasks ->
                    adapter.updateData(tasks) // предполагаем, что у адаптера есть такой метод
                }
            }
        }

        // Добавление анимации
        val itemAnimator = DefaultItemAnimator()
        itemAnimator.addDuration = 300
        itemAnimator.removeDuration = 300
        recyclerView.itemAnimator = itemAnimator

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

        // Загрузим тестовые данные при первом запуске (если список пуст)
        if (viewModel.tasks.value.isEmpty()) {
            viewModel.loadTestData()
        }


    }
    private val detailActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val action = result.data?.getStringExtra("action")
            val data = result.data?.getStringExtra("data")

            when (action) {
                "edit" -> {
                    val parts = data?.split("|")
                    if (parts != null && parts.size == 2) {
                        val originalText = parts[0]
                        val newText = parts[1]
                        viewModel.updateTaskText(originalText, newText)
                    }
                }
                "delete" -> {
                    data?.let { taskText ->
                        viewModel.deleteTaskByText(taskText)
                    }
                }
            }
        }
    }

    private fun showUndoSnackbar(taskText: String, position: Int) {
        Snackbar.make(
            findViewById(android.R.id.content),
            "Задача '$taskText' удалена",
            Snackbar.LENGTH_LONG
        ).setAction("ОТМЕНИТЬ") {
            // При нажатии "ОТМЕНИТЬ" восстанавливаем задачу
            viewModel.insertTaskAtPosition(position, taskText)
        }.show()
    }
}

