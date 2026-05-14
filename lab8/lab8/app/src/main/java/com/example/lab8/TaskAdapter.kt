package com.example.lab8

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class TaskAdapter(
    private var tasks: List<Pair<String, Boolean>>,
    private val onItemClick: (Int) -> Unit,
    private val onItemLongClick: (Int) -> Unit,
    private val onCheckChanged: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>()
 {

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
         val (taskText, isCompleted) = tasks[position]
         holder.textTask.text = taskText

         // Устанавливаем состояние чекбокса
         holder.checkTask.isChecked = isCompleted

         // Обновляем стиль текста
         holder.textTask.paintFlags = if (isCompleted) {
             holder.textTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
         } else {
             holder.textTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
         }

         // Обработчик изменения состояния чекбокса
         holder.checkTask.setOnCheckedChangeListener { _, isChecked ->
             onCheckChanged(position, isChecked)
         }

         holder.itemView.setOnClickListener {
             onItemClick(position)
         }
         holder.itemView.setOnLongClickListener {
             onItemLongClick(position)
             true
         }
     }

     override fun getItemCount(): Int = tasks.size

     fun updateData(newTasks: List<Pair<String, Boolean>>) {
         tasks = newTasks
         notifyDataSetChanged()
     }
 }