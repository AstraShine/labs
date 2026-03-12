package com.example.mysecondapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mysecondapp.models.Product
import com.example.mysecondapp.models.Employee
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val products = getProducts()

        // 1. Исходный список (для наглядности преобразуем в строку)
        val originalText = products.joinToString("\n") { "${it.name} – ${it.price} руб. (${if (it.inStock) "в наличии" else "нет"})" }
        findViewById<TextView>(R.id.textOriginal).text = originalText

        // 2. Фильтр: только товары в наличии
        val inStockProducts = products.filter { it.inStock }
        val inStockText = inStockProducts.joinToString("\n") { "${it.name} – ${it.price} руб." }
        findViewById<TextView>(R.id.textInStock).text = inStockText

        // 3. Цепочка: отфильтровать электронику, отсортировать по цене и получить список строк с названием и ценой
        val electronicsSorted = products
            .filter { it.category == "Электроника" && it.inStock }
            .sortedBy { it.price }
            .map { "${it.name} – ${it.price} руб." }
        val electronicsText = electronicsSorted.joinToString("\n")
        findViewById<TextView>(R.id.textSorted).text = electronicsText

        // 4. Цепочка: отфильтровать товары дешевле 2000, отсортировать по названию и получить список строк с названием и ценой
        val priceSorted = products
            .filter { it.price < 2000}
            .sortedBy { it.name }
            .map { "${it.name} – ${it.price} руб. (${if (it.inStock) "в наличии" else "нет"})" }
        val priceText = priceSorted.joinToString("\n")
        findViewById<TextView>(R.id.textPrice).text = priceText


        //Индивидуальное задание
        val employees = getEmployees()

        val SalaryMore = employees.filter { it.salary > 100000 }
        val SalaryMoreText = SalaryMore.joinToString("\n") { "${it.name}, ${it.department}. Зарплата:  ${it.salary} руб. Стаж работы:  ${it.experience}" }
        findViewById<TextView>(R.id.textSalary).text = SalaryMoreText

        val Experience = employees.sortedByDescending { it.experience }
        val ExperienceText = Experience.joinToString("\n") { "${it.name}, стаж работы:  ${it.experience}" }
        findViewById<TextView>(R.id.textExperience).text = ExperienceText

        val Names = employees.map { "${it.name} – ${it.department}" }
        val NamesText = Names.joinToString("\n")
        findViewById<TextView>(R.id.textName).text = NamesText
    }

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