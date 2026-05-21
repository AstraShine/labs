package com.example.lab10

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.lab10.database.TaskEntity

class TaskAdapter(
    private var tasks: List<TaskEntity>,
    private val onItemClick: (TaskEntity) -> Unit,
    private val onItemLongClick: (TaskEntity) -> Unit,
    private val onCheckChange: (TaskEntity, Boolean) -> Unit
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
        holder.textTask.text = task.title

        // Отключаем слушатель, чтобы избежать двойного вызова
        holder.checkTask.setOnCheckedChangeListener(null)

        // Явно устанавливаем состояние из модели
        holder.checkTask.isChecked = task.isCompleted

        // Настраиваем слушатель после установки значения
        holder.checkTask.setOnCheckedChangeListener { _, isChecked ->
            onCheckChange(task, isChecked)
        }

        holder.itemView.setOnClickListener {
            onItemClick(task)
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClick(task)
            true
        }
    }

    override fun getItemCount(): Int = tasks.size

    fun updateData(newTasks: List<TaskEntity>) {
        val diffCallback = TaskDiffCallback(tasks, newTasks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        tasks = newTasks // Заменяем старый список новым
        diffResult.dispatchUpdatesTo(this)
    }
    class TaskDiffCallback(
        private val oldList: List<TaskEntity>,
        private val newList: List<TaskEntity>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            // Сравниваем ВСЕ поля, влияющие на UI
            return oldItem.title == newItem.title &&
                    oldItem.isCompleted == newItem.isCompleted &&
                    oldItem.createdTime == newItem.createdTime
        }
    }
}