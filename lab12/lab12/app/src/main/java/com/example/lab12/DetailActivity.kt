package com.example.lab12

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val editTextTaskDetail = findViewById<EditText>(R.id.editTextTaskDetail)
        val buttonBack = findViewById<Button>(R.id.buttonBack)
        val buttonSave = findViewById<Button>(R.id.buttonSave)
        val buttonDelete = findViewById<Button>(R.id.buttonDelete)

        // Получаем данные из Intent
        val taskText = intent.getStringExtra("task_text") ?: "Нет данных"
        // Сохраняем исходный текст для поиска позиции при возврате
        val originalTaskText = taskText

        editTextTaskDetail.setText(taskText)

        // Кнопка "Сохранить" — редактирование задачи
        buttonSave.setOnClickListener {
            val newText = editTextTaskDetail.text.toString()
            if (newText.isNotBlank()) {
                returnResult("edit", "$originalTaskText|$newText")
            } else {
                showErrorDialog("Текст задачи не может быть пустым")
            }
        }


        // Кнопка "Удалить" с подтверждением
        buttonDelete.setOnClickListener {
            showDeleteConfirmation { confirmed ->
                if (confirmed) {
                    returnResult("delete", originalTaskText)
                }
            }
        }


        buttonBack.setOnClickListener {
            finish()
        }
    }

    private fun showDeleteConfirmation(onConfirm: (Boolean) -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Подтверждение удаления")
            .setMessage("Вы уверены, что хотите удалить эту задачу?")
            .setPositiveButton("Удалить") { _, _ ->
                onConfirm(true)
            }
            .setNegativeButton("Отмена") { _, _ ->
                onConfirm(false)
            }
            .show()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Ошибка")
            .setMessage(message)
            .setPositiveButton("ОК", null)
            .show()
    }

    private fun returnResult(action: String, data: String) {
        val resultIntent = Intent()
        resultIntent.putExtra("action", action)
        resultIntent.putExtra("data", data)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

}