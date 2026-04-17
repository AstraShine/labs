<div align="center">
    
МИНИСТЕРСТВО НАУКИ И ВЫСШЕГО ОБРАЗОВАНИЯ РОССИЙСКОЙ ФЕДЕРАЦИИ ФЕДЕРАЛЬНОЕ ГОСУДАРСТВЕННОЕ БЮДЖЕТНОЕ ОБРАЗОВАТЕЛЬНОЕ УЧРЕЖДЕНИЕ ВЫСШЕГО ОБРАЗОВАНИЯ
"САХАЛИНСКИЙ ГОСУДАРСТВЕННЫЙ УНИВЕРСИТЕТ»

<br><br><br><br>

Институт естественных наук и техносферной безопасности

Кафедра информатики

Лапырёнок Анастасия

<br><br><br><br>

Лабораторная работа №6

«Отображение списка задач в карточках»

01.03.02 Прикладная математика и информатика

<br><br><br><br><br>

<div align="right">
Научный руководитель

Соболев Евгений Игоревич
</div>

<br><br><br><br><br>

г. Южно-Сахалинск
2026 г.

</div>

<br><br>

**Цель работы:** Научиться использовать `RecyclerView` для отображения списка данных, освоить создание адаптера и `ViewHolder`, применить `CardView` для оформления элементов списка.

<br><br>


# Листинг файла `item_task.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- Объявление XML-документа с указанием версии и кодировки -->

<!-- Пространства имён (namespaces):
         - xmlns:android — стандартное пространство имён Android для базовых атрибутов.
         - xmlns:app — пространство имён для атрибутов из библиотек поддержки (в т. ч. CardView).-->
<androidx.cardview.widget.CardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="8dp"
    app:cardCornerRadius="8dp"
    app:cardElevation="4dp"
    app:cardBackgroundColor="#FFFFFF">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="16dp">

        <TextView
            android:id="@+id/textTask"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:textSize="18sp"
            android:textColor="#333333"/>

        <CheckBox
            android:id="@+id/checkTask"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"/>

    </LinearLayout>

</androidx.cardview.widget.CardView>
```

<br><br>

# Листинг файла `TaskAdapter.kt`

```kotlin
package com.example.lab6
// Пакет, к которому принадлежит класс. Соответствует структуре проекта.

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
// Импорты необходимых классов Android и библиотеки RecyclerView.

class TaskAdapter(
    private val tasks: MutableList<String>,
    // Список задач, отображаемых в RecyclerView. Изменяемый (MutableList).
    private val onItemClick: (Int, String) -> Unit
    // Лямбда‑функция для обработки клика на элементе списка.
    // Принимает два параметра: позицию элемента (Int) и текст задачи (String).
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    // Класс адаптера для RecyclerView, параметризованный собственным ViewHolder'ом.

    // ViewHolder хранит ссылки на элементы внутри карточки
    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTask: TextView = itemView.findViewById(R.id.textTask)
        // Ссылка на TextView с текстом задачи (из item_task.xml).

        val checkTask: CheckBox = itemView.findViewById(R.id.checkTask)
        // Ссылка на CheckBox (флажок выполнения задачи) из item_task.xml.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        // Создаёт новый ViewHolder при необходимости (когда RecyclerView нуждается в новом элементе).
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        // Раздувает (inflates) разметку item_task.xml в View.
        // parent.context — контекст родительского контейнера.
        // false — не прикреплять View к родительскому контейнеру сразу.
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        // Связывает данные (задачу) с конкретным ViewHolder (элементом списка).
        val task = tasks[position]
        holder.textTask.text = task
        // Устанавливает текст задачи в TextView.

        // Установка цвета фона в зависимости от чётности позиции
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(0xFFF5F5F5.toInt()) // Светло‑серый для чётных
        } else {
            holder.itemView.setBackgroundColor(0xFFFFFFFF.toInt()) // Белый для нечётных
        }
        // Чередование цветов фона для улучшения читаемости списка:
        // чётные позиции — светло‑серый (#F5F5F5), нечётные — белый (#FFFFFF).

        // Обработка чекбокса (опционально)
        holder.checkTask.setOnCheckedChangeListener { _, isChecked ->
            // Слушатель изменения состояния флажка.
            // _ — игнорируемый параметр (сам CheckBox).
            // isChecked — текущее состояние (true/false).

            if (isChecked) {
                holder.textTask.paintFlags = holder.textTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                // Если флажок отмечен, добавляем эффект перечёркнутого текста для визуализации выполненности задачи.
            } else {
                holder.textTask.paintFlags = holder.textTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                // Если флажок снят, убираем эффект перечёркивания.
            }
        }

        holder.itemView.setOnClickListener {
            onItemClick(position, tasks[position])
            // Вызывает лямбду onItemClick, передавая позицию и текст задачи.
            // Используется для обработки обычного клика по элементу списка.
        }
    }

    override fun getItemCount(): Int = tasks.size
    // Возвращает общее количество элементов в списке (размер коллекции tasks).

    // Удаление задачи по позиции
    fun removeTask(position: Int) {
        if (position in tasks.indices) {
            tasks.removeAt(position)
            // Удаляет элемент из списка по индексу, если индекс корректен.
            // Проверка `position in tasks.indices` предотвращает ошибки выхода за границы массива.
        }
    }

    // Добавление задачи по позиции
    fun insertTask(position: Int, task: String) {
        tasks.add(position, task)
        // Добавляет задачу в список на указанную позицию.
        // Если позиция превышает текущий размер списка, элемент будет добавлен в конец.
    }

    // Получение задачи по позиции
    fun getTaskAt(position: Int): String {
        return if (position in tasks.indices) tasks[position] else ""
        // Возвращает текст задачи по индексу или пустую строку, если индекс некорректен.
        // Позволяет безопасно получать данные без риска выброса исключения.
    }
}
```

<br><br>

# Листинг файла `Main.Activity.kt`

```kotlin
package com.example.lab6
// Пакет, к которому принадлежит класс. Соответствует структуре проекта.

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
// Импорты необходимых классов Android и компонентов библиотеки поддержки.

class MainActivity : AppCompatActivity() {

    private val tasks = mutableListOf<String>()
    // Изменяемый список задач, который будет отображаться в RecyclerView.

    private lateinit var adapter: TaskAdapter
    // Экземпляр адаптера для RecyclerView (инициализируется позже в onCreate).

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Устанавливает разметку activity_main.xml как содержимое активности.

        val editTextTask = findViewById<EditText>(R.id.editTextTask)
        // Ссылка на поле ввода текста задачи (EditText) из разметки.

        val buttonAddTask = findViewById<Button>(R.id.buttonAddTask)
        // Ссылка на кнопку добавления задачи (Button) из разметки.

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewTasks)
        // Ссылка на RecyclerView из разметки, который будет отображать список задач.

        // Настройка RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Устанавливает LinearLayoutManager — отвечает за вертикальную раскладку элементов списка.

        adapter = TaskAdapter(tasks, { position, taskText ->
            showEditDialog(position, taskText)
        })
        // Создаёт экземпляр адаптера TaskAdapter, передавая список задач и лямбду для обработки клика.
        // При клике на элемент вызывается метод showEditDialog для редактирования задачи.

        recyclerView.adapter = adapter
        // Подключает адаптер к RecyclerView.

        // Добавление анимации
        val itemAnimator = DefaultItemAnimator()
        // Создаёт аниматор для плавных анимаций добавления и удаления элементов.

        itemAnimator.addDuration = 300
        itemAnimator.removeDuration = 300
        // Задаёт длительность анимаций (300 мс) для добавления и удаления элементов.

        recyclerView.itemAnimator = itemAnimator
        // Подключает аниматор к RecyclerView.

        // Добавление задачи
        buttonAddTask.setOnClickListener {
            val task = editTextTask.text.toString()
            // Получает текст из поля ввода.

            if (task.isNotBlank()) {
                tasks.add(task)
                // Добавляет задачу в список, если текст не пустой.

                adapter.notifyItemInserted(tasks.size - 1)
                // Уведомляет адаптер о добавлении нового элемента

                editTextTask.text.clear()
                // Очищает поле ввода после добавления задачи.
            } else {
                Toast.makeText(this, "Введите задачу", Toast.LENGTH_SHORT).show()
                // Показывает всплывающее сообщение (Toast), если поле ввода пустое.
            }
        }

        // Восстановление данных при повороте экрана (опционально)
        if (savedInstanceState != null) {
            val savedTasks = savedInstanceState.getStringArrayList("tasks")
            // Пытается получить сохранённый список задач из Bundle.

            if (savedTasks != null) {
                tasks.clear()
                tasks.addAll(savedTasks)
                // Восстанавливает список задач из сохранённого состояния.

                adapter.notifyDataSetChanged()
                // Уведомляет адаптер об изменении данных — все элементы перерисовываются.
            }
        }

        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(this, adapter))
        // Создаёт помощник для обработки жестов (например, свайпа для удаления).

        itemTouchHelper.attachToRecyclerView(recyclerView)
        // Прикрепляет обработчик жестов к RecyclerView — позволяет удалять задачи свайпом.
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("tasks", ArrayList(tasks))
        // Сохраняет список задач в Bundle перед уничтожением активности (например, при повороте экрана).
    }

    private fun showEditDialog(position: Int, currentText: String) {
        // Метод для отображения диалогового окна редактирования задачи.

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Редактировать задачу")
        // Создаёт построитель диалогового окна и задаёт заголовок.

        val input = EditText(this)
        input.setText(currentText)
        // Создаёт поле ввода и устанавливает текущий текст задачи для редактирования.

        builder.setView(input)
        // Добавляет поле ввода в диалоговое окно.

        builder.setPositiveButton("Сохранить") { _, _ ->
            val newText = input.text.toString()
            // При нажатии «Сохранить» получает новый текст из поля ввода.

            if (newText.isNotBlank()) {
                tasks[position] = newText
                // Обновляет текст задачи в списке по указанной позиции.

                adapter.notifyItemChanged(position)
                // Уведомляет адаптер об изменении элемента — перерисовывает только этот элемент.
            } else {
                Toast.makeText(this, "Текст не может быть пустым", Toast.LENGTH_SHORT).show()
                // Показывает сообщение, если пользователь пытается сохранить пустую строку.
            }
        }

        builder.setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }
        // Добавляет кнопку «Отмена», которая закрывает диалоговое окно без изменений.

        builder.show()
        // Отображает диалоговое окно на экране.
    }
}
```

<br><br>

# Скриншот приложения с отображением результатов
![My Image](images/screen.png)

<br><br>

## Ответы на контрольные вопросы:

### 1. Для чего нужен RecyclerView? Чем он лучше ListView?

**RecyclerView** — компонент Android для отображения прокручиваемых списков (списков, сеток, каруселей и т. д.).

**Преимущества перед ListView:**

* **Переиспользование View:** RecyclerView активно переиспользует элементы интерфейса, которые выходят за экран — это экономит память и ускоряет скролл.
* **Гибкость макета:** поддерживает разные типы разметки через `LayoutManager` (`Linear`, `Grid`, `StaggeredGrid`).
* **Анимации:** встроенная поддержка плавных анимаций при изменении данных (добавление, удаление, перемещение элементов).
* **Оптимизация:** более продуманная архитектура с разделением ответственности между компонентами.

### 2. Какие компоненты необходимы для работы RecyclerView?

Для работы RecyclerView нужны 4 компонента:

1. **RecyclerView** — сам контейнер в разметке (`layout.xml`).
2. **LayoutManager** — определяет, как располагаются элементы (например, `LinearLayoutManager`, `GridLayoutManager`).
3. **Adapter** — «мост» между данными и RecyclerView: передаёт данные в ViewHolder и создаёт ViewHolder‑ы.
4. **ViewHolder** — хранит ссылки на View элемента списка (кэширует их), чтобы не искать каждый раз через `findViewById()`.

### 3. Что такое ViewHolder и для чего он используется?

**ViewHolder** — класс, который:

* хранит ссылки на все `View` внутри одного элемента списка (например, `TextView`, `ImageView`);
* создаётся один раз для каждого типа элемента;
* переиспользуется при прокрутке — RecyclerView не создаёт новые View, а берёт готовый ViewHolder и обновляет в нём данные.

**Цель:** избежать вызова `findViewById()` при каждом отображении элемента — это значительно ускоряет скроллинг.

### 4. Чем отличается `notifyDataSetChanged()` от `notifyItemInserted()`?

* **`notifyDataSetChanged()`** — сообщает RecyclerView, что **все данные** в адаптере изменились. Приводит к полному перестроению списка: все элементы пересоздаются и перерисовываются. **Менее эффективно**, но просто в использовании.
* **`notifyItemInserted(position)`** — сообщает, что **в определённую позицию** (`position`) добавлен новый элемент. RecyclerView анимирует добавление только этого элемента, остальные не трогаются. **Более эффективно**, требует точного указания позиции.

**Аналогичные методы для точечного обновления:**

* `notifyItemRemoved(position)` — удаление элемента.
* `notifyItemChanged(position)` — изменение элемента.
* `notifyItemMoved(fromPosition, toPosition)` — перемещение элемента.

### 5. Как добавить обработку кликов на элементы RecyclerView?

Реализация на Kotlin

**Шаг 1. Определите интерфейс слушателя в адаптере**

```kotlin
class MyAdapter(
    private val data: List<String>,
    private val onItemClick: (Int) -> Unit // лямбда-функция для обработки клика
) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    // Код
}
```
**Шаг 2. Создайте ViewHolder и установите обработчик клика на корневой элемент:**

```inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val textView: TextView = itemView.findViewById(R.id.text_view)
    init {
        itemView.setOnClickListener {
            onItemClick(adapterPosition) // передаём позицию элемента
        }
    }
    fun bind(item: String) {
        textView.text = item
    }
}
```

**Шаг 3. В методе onBindViewHolder свяжите данные с ViewHolder**

```kotlin
override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = data[position]
    holder.bind(item)
}
```

**Шаг 4. В Activity_Main.kt создайте адаптер и обработайте клик**

```kotlin
val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
recyclerView.layoutManager = LinearLayoutManager(this)

val adapter = MyAdapter(myDataList) { position ->
    // Обработка клика: что делать при нажатии на элемент с позицией position
    Toast.makeText(this, "Clicked item $position", Toast.LENGTH_SHORT).show()
}

recyclerView.adapter = adapter
```

## Вывод

В ходе работы успешно освоено использование `RecyclerView` для отображения списка данных в Android‑приложении.

**Достигнуты следующие результаты:**

* Реализован адаптер (`TaskAdapter`) с `ViewHolder` (`TaskViewHolder`), обеспечивающий эффективное переиспользование элементов списка и высокую производительность при скроллинге.
* Настроен `RecyclerView` в `MainActivity` с `LinearLayoutManager` для вертикальной раскладки элементов.
* Элементы списка оформлены с помощью `CardView` (файл `item_task.xml`), что обеспечило визуальную привлекательность и отступы между карточками.
* Реализованы ключевые интерактивные функции:
  * добавление задач через `EditText` и `Button`;
  * редактирование задач через диалоговое окно `AlertDialog`;
  * удаление задач свайпом (с использованием `ItemTouchHelper` и `SwipeToDeleteCallback`);
  * визуализация статуса выполнения задачи через `CheckBox` (перечёркнутый текст для выполненных задач).
* Настроены плавные анимации изменений элементов с помощью `DefaultItemAnimator`.
* Обеспечено сохранение состояния списка задач при повороте экрана через `onSaveInstanceState` и восстановление в `onCreate`.
* Оптимизировано обновление данных в `RecyclerView` за счёт использования точечных уведомлений (`notifyItemInserted()`, `notifyItemChanged()`) вместо полного обновления (`notifyDataSetChanged()`).

**Итог:** создана функциональное приложение, демонстрирующее работу с `RecyclerView` в Android на Kotlin.
