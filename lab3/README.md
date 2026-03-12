<div align="center">
    
МИНИСТЕРСТВО НАУКИ И ВЫСШЕГО ОБРАЗОВАНИЯ РОССИЙСКОЙ ФЕДЕРАЦИИ ФЕДЕРАЛЬНОЕ ГОСУДАРСТВЕННОЕ БЮДЖЕТНОЕ ОБРАЗОВАТЕЛЬНОЕ УЧРЕЖДЕНИЕ ВЫСШЕГО ОБРАЗОВАНИЯ
"САХАЛИНСКИЙ ГОСУДАРСТВЕННЫЙ УНИВЕРСИТЕТ»

<br><br><br><br>

Институт естественных наук и техносферной безопасности

Кафедра информатики

Лапырёнок Анастасия

<br><br><br><br>

Лабораторная работа № 3

«Реализация списка объектов с фильтрацией с использованием .map, .filter, .sortedBy»

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

**Цель работы:** Изучить функциональные методы обработки коллекций в Kotlin (filter, map, sortedBy) на примере списка объектов и вывести результаты в интерфейс Android-приложения.

<br><br>


## Листинг файла `Product.kt`

```kotlin
// Пакет, в котором находится класс Product
package com.example.mysecondapp.models

// Data class — специальный тип класса в Kotlin для хранения данных
data class Product(
    // Название товара, тип String (строка)
    val name: String,
    
    // Категория товара, тип String
    val category: String,
    
    // Цена товара, тип Double (число с плавающей точкой)
    val price: Double,
    
    // Признак наличия на складе, тип Boolean (true / false)
    val inStock: Boolean
)
```

<br><br>

## Листинг файла `Employee.kt`

```kotlin
// Пакет, в котором находится класс Employee
package com.example.mysecondapp.models

// Data class — специальный тип класса в Kotlin для хранения данных
data class Employee(
    // Имя сотрудника, тип String (строка)
    val name: String,
    
    // Отдел, в котором работает сотрудник, тип String
    val department: String,
    
    // Зарплата сотрудника, тип Doubl
    val salary: Double,
    
    // Опыт работы сотрудника в годах, тип Double
    val experience: Double
)

```

<br><br>

## Листинг файла `MainActivity.kt`

```kotlin
// Пакет, в котором находится MainActivity
package com.example.mysecondapp

// Импорты необходимых классов и компонентов Android
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
// Импорт моделей данных
import com.example.mysecondapp.models.Product
import com.example.mysecondapp.models.Employee
// Импорт виджетов для отображения данных
import android.widget.TextView

// Основной класс активности приложения, наследуется от AppCompatActivity
class MainActivity : AppCompatActivity() {
    // Переопределённый метод жизненного цикла — вызывается при создании активности
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Устанавливаем UI из layout‑файла
        setContentView(R.layout.activity_main)

        // Получаем список товаров
        val products = getProducts()

        // 1. Отображение исходного списка товаров
        // Преобразуем список в строку для отображения в TextView
        val originalText = products.joinToString("\n") {
            "${it.name} – ${it.price} руб. (${if (it.inStock) "в наличии" else "нет"})"
        }
        findViewById<TextView>(R.id.textOriginal).text = originalText

        // 2. Фильтрация: отображаем только товары, которые есть в наличии
        val inStockProducts = products.filter { it.inStock }
        val inStockText = inStockProducts.joinToString("\n") {
            "${it.name} – ${it.price} руб."
        }
        findViewById<TextView>(R.id.textInStock).text = inStockText

        // 3. Цепочка операций:
        // - фильтруем товары категории «Электроника», которые есть в наличии;
        // - сортируем по цене (по возрастанию);
        // - преобразуем в список строк с названием и ценой.
        val electronicsSorted = products
            .filter { it.category == "Электроника" && it.inStock }
            .sortedBy { it.price }
            .map { "${it.name} – ${it.price} руб." }
        val electronicsText = electronicsSorted.joinToString("\n")
        findViewById<TextView>(R.id.textSorted).text = electronicsText

        // 4. Цепочка операций:
        // - фильтруем товары с ценой меньше 2000 руб.;
        // - сортируем по названию (алфавитно);
        // - преобразуем в строки с названием, ценой и статусом наличия.
        val priceSorted = products
            .filter { it.price < 2000 }
            .sortedBy { it.name }
            .map { "${it.name} – ${it.price} руб. (${if (it.inStock) "в наличии" else "нет"})" }
        val priceText = priceSorted.joinToString("\n")
        findViewById<TextView>(R.id.textPrice).text = priceText

        // Индивидуальное задание: работа с сотрудниками
        val employees = getEmployees()

        // Фильтрация: сотрудники с зарплатой больше 100 000 руб.
        val SalaryMore = employees.filter { it.salary > 100000 }
        val SalaryMoreText = SalaryMore.joinToString("\n") {
            "${it.name}, ${it.department}. Зарплата: ${it.salary} руб. Стаж работы: ${it.experience}"
        }
        findViewById<TextView>(R.id.textSalary).text = SalaryMoreText

        // Сортировка: сотрудники по убыванию стажа работы
        val Experience = employees.sortedByDescending { it.experience }
        val ExperienceText = Experience.joinToString("\n") {
            "${it.name}, стаж работы: ${it.experience}"
        }
        findViewById<TextView>(R.id.textExperience).text = ExperienceText

        // Преобразование: список строк «имя — отдел» для всех сотрудников
        val Names = employees.map { "${it.name} – ${it.department}" }
        val NamesText = Names.joinToString("\n")
        findViewById<TextView>(R.id.textName).text = NamesText
    }

    // Вспомогательный метод для получения тестового списка товаров
    private fun getProducts(): List<Product> {
        return listOf(
            Product("Ноутбук", "Электроника", 75000.0, true),
            Product("Мышь", "Электроника", 1500.0, true),
            Product("Книга 'Котлин'", "Книги", 1200.0, false),
            Product("Флешка 64GB", "Электроника", 2000.0, true),
            Product("Блокнот", "Канцелярия", 300.0, true),
            Product("Ручка", "Канцелярия", 50.0, false),
            Product("Монитор", "Электроника", 25000.0, true)
        )
    }

    // Вспомогательный метод для получения тестового списка сотрудников
    private fun getEmployees(): List<Employee> {
        return listOf(
            Employee("Андреев А. А.", "Отдел продаж", 90000.0, 5.0),
            Employee("Борисов Б. Б.", "Отдел поставок", 120000.0, 10.0),
            Employee("Владимиров В. В.", "Отдел разработки", 110000.0, 8.0),
            Employee("Григорьев Г. Г.", "Отдел маркетинка", 8000.0, 3.0),
            Employee("Дмитриев Д. Д.", "Отдел поставок", 85000.0, 5.0)
        )
    }
}

```

<br><br>

### Скриншот приложения с отображением результатов
![My Image](images/screen.jpg)

<br><br>

### Ответы на контрольные вопросы:

**1. Что возвращает функция `filter` — новый список или изменяет существующий?**

Функция `filter` возвращает новый список, не изменяя исходный. Она перебирает элементы коллекции и включает в новый список только те, которые удовлетворяют заданному условию (предикату).

**2. В чём разница между `sortedBy` и `sortedByDescending`?**

Обе функции возвращают новый отсортированный список, но:

- `sortedBy` сортирует элементы по возрастанию на основе заданного критерия.
- `sortedByDescending` сортирует элементы по убыванию на основе того же критерия.

**3. Как можно объединить несколько условий в `filter`?**

Несколько условий объединяются внутри лямбда‑выражения с помощью логических операторов:

- `&&` — логическое И (должны выполняться все условия);
- `||` — логическое ИЛИ (должно выполняться хотя бы одно условие).

**4. Для чего используется функция `map`? Приведите пример**

Функция `map` преобразует каждый элемент коллекции по заданному правилу и возвращает новый список с результатами преобразования. Она «проецирует» один тип данных на другой.

**Пример:**

```kotlin
val numbers = listOf(1, 2, 3, 4)
val newNumbers = numbers.map { it + 2 } // каждое число увеличиваем на двойку
println(newNumbers) // [3, 4, 5, 6]
```

**5. Что такое `joinToString` и как она работает?**

`joinToString` — функция, которая преобразует коллекцию в одну строку, объединяя её элементы. Она полезна для вывода данных в удобочитаемом формате.

**Основные параметры:**

- `separator` — разделитель между элементами (по умолчанию — `", "`);
- `prefix` — текст перед началом строки (по умолчанию — пустая строка);
- `postfix` — текст после окончания строки (по умолчанию — пустая строка);
- `limit` — максимальное количество элементов для включения (если элементов больше, добавляется `truncated`);
- `truncated` — строка, добавляемая, если количество элементов превышает `limit` (по умолчанию — `"..."`);
- `transform` — функция для преобразования каждого элемента перед объединением.


**Вывод:** Изучила функциональные методы обработки коллекций в Kotlin (filter, map, sortedBy) на примере списка объектов и вывела результаты в интерфейс Android-приложения.