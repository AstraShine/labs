package com.example.lab6

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class TaskAdapter(
    private val tasks: MutableList<String>,
    private val onItemLongClick: (Int) -> Unit,
    private val onItemClick: (Int, String) -> Unit // Новый параметр
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    // ViewHolder хранит ссылки на элементы внутри карточки
    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTask: TextView = itemView.findViewById(R.id.textTask)
        val checkTask: CheckBox = itemView.findViewById(R.id.checkTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.textTask.text = task

        // Установка цвета фона в зависимости от чётности позиции
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(0xFFF5F5F5.toInt()) // Светло‑серый для чётных
        } else {
            holder.itemView.setBackgroundColor(0xFFFFFFFF.toInt()) // Белый для нечётных
        }
        // Обработка чекбокса (опционально)
        holder.checkTask.setOnCheckedChangeListener { _, isChecked ->
            // Можно добавить логику отметки выполнения, например, перечеркивание текста
            if (isChecked) {
                holder.textTask.paintFlags = holder.textTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                holder.textTask.paintFlags = holder.textTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }
        holder.itemView.setOnClickListener {
            onItemClick(position, tasks[position])
        }
    }

    override fun getItemCount(): Int = tasks.size

    // Метод для обновления списка
    fun updateData(newTasks: List<String>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }

    // Удаление задачи по позиции
    fun removeTask(position: Int) {
        if (position in tasks.indices) {
            tasks.removeAt(position)
        }
    }

    //Добавление задачи по позиции
    fun insertTask(position: Int, task: String) {
        tasks.add(position, task)
    }

    // Получение задачи по позиции
    fun getTaskAt(position: Int): String {
        return if (position in tasks.indices) tasks[position] else ""
    }


}