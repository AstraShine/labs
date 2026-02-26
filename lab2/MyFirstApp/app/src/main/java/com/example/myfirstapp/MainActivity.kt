package com.example.myfirstapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myfirstapp.utils.Book
import com.example.myfirstapp.utils.formatAuthorName
import com.example.myfirstapp.utils.isValidEmail
import com.example.myfirstapp.utils.applyDiscount
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val book = Book("Война и мир", "Толстой Лев Николаевич", 1869, 500.0)
        val formattedAuthor = formatAuthorName(book.author)
        val discountedPrice = applyDiscount(book.price, 15.0)

        findViewById<TextView>(R.id.textView1).text = "Книга: ${book.title}"
        findViewById<TextView>(R.id.textView2).text = "Автор: $formattedAuthor"
        findViewById<TextView>(R.id.textView3).text = "Цена со скидкой: $discountedPrice руб."
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}