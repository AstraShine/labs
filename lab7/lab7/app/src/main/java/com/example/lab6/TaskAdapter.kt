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
    private val onItemClick: (String, Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

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

        // Установка цвета фона
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(0xFFF5F5F5.toInt())
        } else {
            holder.itemView.setBackgroundColor(0xFFFFFFFF.toInt())
        }

        // Отключаем слушатель на время обновления
        holder.checkTask.setOnCheckedChangeListener(null)

        // Устанавливаем актуальное состояние чекбокса (если нужно)
        // Здесь можно добавить логику восстановления состояния чекбокса, если оно сохраняется

        // Восстанавливаем слушатель с корректной логикой
        holder.checkTask.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                holder.textTask.paintFlags = holder.textTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                holder.textTask.paintFlags = holder.textTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }

        // Устанавливаем обработчик клика — передаём актуальную позицию
        holder.itemView.setOnClickListener {
            onItemClick(task, position)  // Передаём текущий текст и позицию
        }
    }


    override fun getItemCount(): Int = tasks.size

    // Удаление задачи по позиции
    fun removeTask(position: Int) {
        if (position in tasks.indices) {
            tasks.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    //Добавление задачи по позиции
    fun insertTask(position: Int, task: String) {
        tasks.add(position, task)
        notifyItemInserted(position)
    }


    // Получение задачи по позиции
    fun getTaskAt(position: Int): String {
        return if (position in tasks.indices) tasks[position] else ""
    }

}