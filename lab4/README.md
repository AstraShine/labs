<div align="center">
    
МИНИСТЕРСТВО НАУКИ И ВЫСШЕГО ОБРАЗОВАНИЯ РОССИЙСКОЙ ФЕДЕРАЦИИ ФЕДЕРАЛЬНОЕ ГОСУДАРСТВЕННОЕ БЮДЖЕТНОЕ ОБРАЗОВАТЕЛЬНОЕ УЧРЕЖДЕНИЕ ВЫСШЕГО ОБРАЗОВАНИЯ
"САХАЛИНСКИЙ ГОСУДАРСТВЕННЫЙ УНИВЕРСИТЕТ»

<br><br><br><br>

Институт естественных наук и техносферной безопасности

Кафедра информатики

Лапырёнок Анастасия

<br><br><br><br>

Лабораторная работа №4

«Верстка экрана профиля пользователя (аватар, имя, кнопка «Редактировать»)»

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

**Цель работы:** Освоить создание пользовательского интерфейса в Android с использованием ConstraintLayout, изучить основные компоненты: ImageView, TextView, Button. Научиться работать с ресурсами (строки, цвета, размеры) и обрабатывать нажатия кнопок.

<br><br>


## Листинг файла `activity_main.xml`

```kotlin
<?xml version="1.0" encoding="utf-8"?>
<!-- Объявление XML-документа с указанием версии и кодировки -->

<!-- Корневой контейнер — ConstraintLayout, обеспечивающий гибкое позиционирование элементов через привязки -->
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"  <!-- Ширина контейнера занимает всю доступную ширину родителя -->
    android:layout_height="match_parent" <!-- Высота контейнера занимает всю доступную высоту родителя -->
    android:background="@color/gray_light" <!-- Фон контейнера — светло‑серый цвет -->
    tools:context=".MainActivity"> <!-- Указывает, что этот layout связан с MainActivity -->

    <!-- Аватар пользователя -->
    <ImageView
        android:id="@+id/imageAvatar" <!-- Уникальный идентификатор элемента -->
        android:layout_width="@dimen/avatar_size" <!-- Ширина задаётся через ресурс dimen -->
        android:layout_height="@dimen/avatar_size" <!-- Высота задаётся через ресурс dimen -->
        android:src="@drawable/ic_profile" <!-- Изображение для отображения -->
        app:layout_constraintTop_toTopOf="parent" <!-- Привязка к верхнему краю родителя -->
        app:layout_constraintBottom_toTopOf="@+id/textName" <!-- Привязка нижнего края к верху TextView с именем -->
        app:layout_constraintLeft_toLeftOf="parent" <!-- Привязка левого края к левому краю родителя -->
        app:layout_constraintRight_toRightOf="parent" <!-- Привязка правого края к правому краю родителя -->
        android:layout_marginTop="@dimen/margin_normal" <!-- Отступ сверху -->
        android:contentDescription="@string/profile_name" /> <!-- Описание для доступности -->

    <!-- Имя пользователя -->
    <TextView
        android:id="@+id/textName" <!-- Уникальный идентификатор элемента -->
        android:layout_width="wrap_content" <!-- Ширина подстраивается под содержимое -->
        android:layout_height="wrap_content" <!-- Высота подстраивается под содержимое -->
        android:text="@string/profile_name" <!-- Текст задаётся через строковый ресурс -->
        android:textSize="@dimen/text_size_name" <!-- Размер текста задаётся через ресурс dimen -->
        android:textColor="@color/black" <!-- Цвет текста — чёрный -->
        android:textStyle="bold" <!-- Жирное начертание текста -->
        app:layout_constraintTop_toBottomOf="@id/imageAvatar" <!-- Привязка к низу ImageView с аватаром -->
        app:layout_constraintLeft_toLeftOf="parent" <!-- Привязка левого края к левому краю родителя -->
        app:layout_constraintRight_toRightOf="parent" <!-- Привязка правого края к правому краю родителя -->
        android:layout_marginTop="@dimen/margin_small" /> <!-- Небольшой отступ сверху -->

    <!-- Статус пользователя -->
    <TextView
        android:id="@+id/textStatus" <!-- Уникальный идентификатор элемента -->
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/profile_status" <!-- Текст статуса из строкового ресурса -->
        android:textSize="@dimen/text_size_status" <!-- Размер текста статуса -->
        android:textColor="@color/orange" <!-- Цвет текста — оранжевый -->
        app:layout_constraintTop_toBottomOf="@id/textName" <!-- Привязка к низу TextView с именем -->
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        android:layout_marginTop="@dimen/margin_small" />

    <!-- Контейнер для контактной информации (телефон и email) -->
    <LinearLayout
        android:id="@+id/LinearLayout"
        android:layout_width="wrap_content"
        android:layout_height="50dp" <!-- Фиксированная высота контейнера -->
        android:orientation="horizontal" <!-- Горизонтальное расположение дочерних элементов -->
        android:paddingLeft="16dp" <!-- Отступы слева -->
        android:paddingRight="16dp" <!-- Отступы справа -->
        app:layout_constraintHorizontal_bias="0.5" <!-- Центрирование по горизонтали -->
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toBottomOf="@id/textStatus"> <!-- Привязка к низу TextView со статусом -->

        <!-- Номер телефона -->
        <TextView
            android:id="@+id/textPhone"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="@dimen/margin_small"
            android:layout_marginStart="@dimen/margin_normal"
            android:text="@string/profile_phone" <!-- Текст телефона из строкового ресурса -->
            android:textColor="@color/blue_light_2" <!-- Цвет текста — светло‑синий -->
            android:textSize="@dimen/text_size_status"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"/>

        <!-- Email пользователя -->
        <TextView
            android:id="@+id/textEmail"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="@dimen/margin_small"
            android:layout_marginStart="@dimen/margin_normal"
            android:text="@string/profile_email" <!-- Текст email из строкового ресурса -->
            android:textColor="@color/blue_light_2"
            android:textSize="@dimen/text_size_status"
            app:layout_constraintLeft_toLeftOf="@+id/textPhone" <!-- Привязка к левому краю TextView с телефоном -->
            app:layout_constraintRight_toRightOf="parent"/>
    </LinearLayout>

    <!-- Кнопка «Редактировать» -->
    <Button
        android:id="@+id/buttonEdit"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/button_edit" <!-- Текст кнопки из строкового ресурса -->
        android:backgroundTint="@color/blue_light" <!-- Цвет фона кнопки — светло‑синий -->
        app:cornerRadius="@dimen/button_corner_radius" <!-- Радиус скругления углов кнопки -->
        app:layout_constraintTop_toBottomOf="@+id/LinearLayout" <!-- Привязка к низу LinearLayout с контактами -->
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        android:layout_marginTop="@dimen/margin_normal"/> <!-- Отступ сверху -->

    <!-- Кнопка «Выйти» -->
    <Button
        android:id="@+id/buttonExit"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/button_exit" <!-- Текст кнопки из строкового ресурса -->
        android:backgroundTint="@color/blue_light"
        app:cornerRadius="@dimen/button_corner_radius"
        app:layout_constraintTop_toBottomOf="@id/buttonEdit" <!-- Привязка к низу кнопки «Редактировать» -->
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        android:layout_marginTop="@dimen/margin_normal"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```

<br><br>

## Листинг файла `Main.Activity.kt`

```kotlin
package com.example.profileapp
// Объявление пакета приложения — определяет пространство имён для класса MainActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import kotlin.system.exitProcess
import androidx.core.content.ContextCompat
import android.widget.TextView
// Импорты необходимых классов и функций:
// - AppCompatActivity — базовый класс для активности с поддержкой обратной совместимости
// - Bundle — используется для сохранения и восстановления состояния активности
// - Button, TextView — виджеты интерфейса
// - Toast — класс для отображения коротких всплывающих сообщений
// - exitProcess — функция для завершения процесса приложения
// - ContextCompat — утилита для безопасного получения ресурсов (например, Drawable)

class MainActivity : AppCompatActivity() {
    // Объявление класса MainActivity, наследующего от AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        // Переопределение метода onCreate — точка входа в активность
        // Вызывается при создании активности, здесь выполняется основная инициализация

        super.onCreate(savedInstanceState)
        // Вызов реализации onCreate родительского класса — обязательный шаг

        setContentView(R.layout.activity_main)
        // Установка UI-макета (activity_main.xml) в качестве содержимого активности

        // Иконки для TextView
        val textPhone = findViewById<TextView>(R.id.textPhone)
        // Находим TextView с ID textPhone в макете
        val ic_phone = ContextCompat.getDrawable(this, R.drawable.ic_call)
        // Получаем Drawable-ресурс иконки телефона (ic_call) с учётом контекста активности
        textPhone.setCompoundDrawablesWithIntrinsicBounds(ic_phone, null, null, null)
        // Устанавливаем иконку слева от текста в TextView (остальные позиции — null)

        val textEmail = findViewById<TextView>(R.id.textEmail)
        // Находим TextView с ID textEmail в макете
        val ic_email = ContextCompat.getDrawable(this, R.drawable.ic_email)
        // Получаем Drawable-ресурс иконки email (ic_email)
        textEmail.setCompoundDrawablesWithIntrinsicBounds(ic_email, null, null, null)
        // Устанавливаем иконку слева от текста в TextView

        val buttonEdit = findViewById<Button>(R.id.buttonEdit)
        // Находим кнопку «Редактировать» (buttonEdit) в макете
        buttonEdit.setOnClickListener {
            // Устанавливаем обработчик нажатия на кнопку
            Toast.makeText(this, R.string.toast_message, Toast.LENGTH_SHORT).show()
            // При нажатии показываем короткое всплывающее сообщение (Toast) с текстом из строкового ресурса
        }

        val buttonExit = findViewById<Button>(R.id.buttonExit)
        // Находим кнопку «Выйти» (buttonExit) в макете
        buttonExit.setOnClickListener {
            // Устанавливаем обработчик нажатия на кнопку

            // Закрывает основную активность
            this().finish()

            // Закрывает приложение
            exitProcess(0)
        }
    }
}
```

<br><br>

### Скриншот приложения с отображением результатов
![My Image](images/Screen.jpg)

<br><br>

### Ответы на контрольные вопросы:

## 1. ConstraintLayout: назначение и преимущества перед LinearLayout**

**ConstraintLayout** — контейнер для размещения элементов интерфейса в Android‑приложениях. Позволяет гибко позиционировать и связывать виджеты относительно друг друга или родительского контейнера.

**Преимущества перед LinearLayout**

* **Гибкость позиционирования.** В `ConstraintLayout` элементы можно привязывать к любым сторонам других элементов или границ контейнера. В `LinearLayout` элементы располагаются только последовательно (горизонтально/вертикально).
* **Снижение вложенности.** Позволяет избежать глубоких иерархий контейнеров — это улучшает производительность. `LinearLayout` часто требует вложенных контейнеров для сложных макетов.
* **Адаптивность.** Лучше адаптируется к разным размерам экранов и ориентациям без необходимости создавать отдельные XML‑файлы.
* **Оптимизация производительности.** За счёт плоской иерархии виджетов рендеринг происходит быстрее по сравнению с глубоко вложенными `LinearLayout`.
* **Поддержка цепочек (Chains).** Позволяет равномерно распределять элементы по горизонтали или вертикали с гибким управлением отступами.
* **Базовые линии (Baseline alignment).** Можно выравнивать текст в разных элементах по базовой линии.

## 2. Атрибуты app:layout_constraint...

Атрибуты с префиксом `app:layout_constraint` используются в `ConstraintLayout` для определения связей (ограничений) между элементами. Они задают, как виджет должен быть позиционирован относительно других виджетов или границ контейнера.

**Основные атрибуты**

* `app:layout_constraintLeft_toLeftOf` — левая сторона элемента привязывается к левой стороне указанного элемента.
* `app:layout_constraintLeft_toRightOf` — левая сторона элемента привязывается к правой стороне указанного элемента.
* Аналогичные атрибуты для других сторон:
  * `...Right_toLeftOf`
  * `...Right_toRightOf`
  * `...Top_toTopOf`
  * `...Top_toBottomOf`
  * `...Bottom_toTopOf`
  * `...Bottom_toBottomOf`
* `app:layout_constraintStart_toStartOf` / `app:layout_constraintEnd_toEndOf` — привязки для языков с направлением письма справа налево (RTL).
* `app:layout_constraintTop_bias` — смещение элемента вдоль горизонтальной/вертикальной оси (значение от $0$ до $1$).
* `app:layout_constraintWidth_percent` / `app:layout_constraintHeight_percent` — установка размера элемента в процентах от доступного пространства.

## 3. Вынесение размеров и цветов в ресурсы

**Цвета**

1. Создайте или откройте файл `res/values/colors.xml`.
2. Добавьте цветовые ресурсы с уникальными именами:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="primary_color">#3F51B5</color>
    <color name="accent_color">#FF4081</color>
    <color name="background_white">#FFFFFF</color>
    <color name="text_dark">#333333</color>
</resources>
```
3. Используйте в разметке через ссылку `@color/имя_ресурса`:
```kotlin
android:background="@color/primary_color"
android:textColor="@color/text_dark"
```
**Размеры**
1. Создайте или откройте файл `res/values/dimens.xml`.
2. Определите размеры с понятными именами:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <dimen name="text_size_large">24sp</dimen>
    <dimen name="text_size_medium">16sp</dimen>
    <dimen name="margin_small">8dp</dimen>
    <dimen name="margin_medium">16dp</dimen>
    <dimen name="margin_large">32dp</dimen>
    <dimen name="button_height">48dp</dimen>
</resources>
```
3. Применяйте в разметке через `@dimen/имя_ресурса`:
```kotlin
android:textSize="@dimen/text_size_medium"
android:layout_margin="@dimen/margin_medium"
android:layout_height="@dimen/button_height"
```

**Зачем выносить в ресурсы?**
- **Централизованное управление.** Все значения в одном месте — легко менять и поддерживать.
- **Повторное использование.** Один ресурс можно применять в разных частях приложения.
- **Адаптивность.** Можно создавать разные файлы ресурсов для: разных размеров экранов (values-sw600dp/ для планшетов); разных ориентаций (values-land/); разных плотностей пикселей (drawable-hdpi/, drawable-xhdpi/ и т. д.).
- **Соблюдение единого стиля.** Гарантирует единообразие цветов и размеров во всём приложении.
- **Упрощение локализации.** Облегчает поддержку разных языков и региональных настроек.
- **Лёгкое обновление дизайна.** Изменение одного значения в ресурсах автоматически применяется везде.

## 4. Обработка клика на кнопке в Kotlin‑коде

Существует несколько способов обработки клика на кнопке в Android‑приложениях на Kotlin. Ниже — основные варианты.
**Способ 1: `setOnClickListener` с лямбда‑выражением (рекомендуемый)**
Самый простой и лаконичный способ для обработки клика одной кнопки.

```kotlin
val button: Button = findViewById(R.id.my_button)
button.setOnClickListener {
    Toast.makeText(this, "Кнопка нажата!", Toast.LENGTH_SHORT).show()
    // Здесь можно добавить любую другую логику
}
```
**Преимущества:**
- минимум кода;
- интуитивно понятный синтаксис;
- подходит для простых сценариев.
**Способ 2: Реализация интерфейса View.OnClickListener**
Подходит, когда нужно обрабатывать клики нескольких элементов в одном активити или фрагменте.
```kotlin
class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Назначаем обработчик для кнопок
        findViewById<Button>(R.id.button1).setOnClickListener(this)
        findViewById<Button>(R.id.button2).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button1 -> {
                Toast.makeText(this, "Нажата кнопка 1", Toast.LENGTH_SHORT).show()
            }
            R.id.button2 -> {
                Toast.makeText(this, "Нажата кнопка 2", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
```
**Преимущества:**
- централизованная обработка кликов;
- удобно, если много кнопок в одном месте;
- легко добавлять новые обработчики.
**Способ 3: Лямбда‑выражение с явной проверкой ID**
Используется, когда нужно обработать клик и проверить ID элемента внутри лямбды.
```kotlin
findViewById<Button>(R.id.my_button).setOnClickListener { clickedView ->
    when (clickedView.id) {
        R.id.my_button -> {
            Toast.makeText(this, "Основная кнопка нажата", Toast.LENGTH_SHORT).show()
        }
        // Можно добавить другие условия
    }
}
```
**Преимущества:**
- гибкость в обработке разных элементов;
- можно комбинировать с другими условиями.

## 5. Добавление обработчика нажатия на ImageView
`ImageView` по умолчанию не является кликабельным, поэтому перед добавлением обработчика нужно убедиться, что он включён. Разберём процесс пошагово.
**Шаг 1. Настройка кликабельности ImageView**
Есть два способа сделать `ImageView` кликабельным:
**Способ 1: В XML‑разметке**
Добавьте атрибуты `android:clickable="true"` и `android:focusable="true"` в описание `ImageView` в XML:
```xml
<ImageView
    android:id="@+id/my_image_view"
    android:layout_width="100dp"
    android:layout_height="100dp"
    android:src="@drawable/my_image"
    android:clickable="true"
    android:focusable="true" />
```
**Способ 2: Программно в Kotlin**
Установите свойства `isClickable` и `isFocusable` в `true` в коде Kotlin:
```kotlin
val imageView: ImageView = findViewById(R.id.my_image_view)
imageView.isClickable = true
imageView.isFocusable = true
```
**Шаг 2. Добавление обработчика клика**
После того как `ImageView` стал кликабельным, добавьте обработчик нажатия через `setOnClickListener`:
```kotlin
val imageView: ImageView = findViewById(R.id.my_image_view)
imageView.setOnClickListener {
    Toast.makeText(this, "Изображение нажато!", Toast.LENGTH_SHORT).show()
}
```
**Шаг 3. Добавление визуальной обратной связи**
Чтобы пользователь видел, что элемент реагирует на нажатие, добавьте эффект нажатия. Рассмотрим два способа.
**Способ 1: Через атрибут `foreground` (простой способ)**
Добавьте `android:foreground="?attr/selectableItemBackground"` в XML — это добавит стандартный эффект ряби (ripple effect) при нажатии:
```xml
<ImageView
    android:id="@+id/my_image_view"
    android:layout_width="100dp"
    android:layout_height="100dp"
    android:src="@drawable/my_image"
    android:clickable="true"
    android:focusable="true"
    android:foreground="?attr/selectableItemBackground" />
```
**Способ 2: Через фоновый селектор (кастомизация)**
1. Создайте файл `res/drawable/imageview_selector.xml` со следующим содержимым:
```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:drawable="@color/accent_color" android:state_pressed="true"/>
    <item android:drawable="@color/background_white"/>
</selector>
```
2. Установите этот селектор как фон в `ImageView`:
```xml
<ImageView
    android:id="@+id/my_image_view"
    android:layout_width="100dp"
    android:layout_height="100dp"
    android:src="@drawable/my_image"
    android:clickable="true"
    android:focusable="true"
    android:background="@drawable/imageview_selector" />
```

**Вывод:** Освоила создание пользовательского интерфейса в Android с использованием ConstraintLayout, изучила основные компоненты: ImageView, TextView, Button. Научилась работать с ресурсами (строки, цвета, размеры) и обрабатывать нажатия кнопок.