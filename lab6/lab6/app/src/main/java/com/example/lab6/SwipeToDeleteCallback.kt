package com.example.lab6

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class SwipeToDeleteCallback(
    private val context: Context,
    private val adapter: TaskAdapter
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.bindingAdapterPosition
        if (position != RecyclerView.NO_POSITION) {
            val task = adapter.getTaskAt(position)

            // Сохраняем для отмены
            adapter.removeTask(position)
            adapter.notifyItemRemoved(position)

            // Показываем Snackbar с возможностью отмены
            Snackbar.make(
                (context as AppCompatActivity).findViewById(android.R.id.content),
                "Задача '$task' удалена",
                Snackbar.LENGTH_LONG
            ).setAction("ОТМЕНИТЬ") { _ ->
                adapter.insertTask(position, task)
                adapter.notifyItemInserted(position)
            }.show()
        }
    }
}
