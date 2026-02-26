package com.example.myfirstapp.utils

// Проверка, что строка похожа на email (содержит @ и .)
fun String.isValidEmail(): Boolean {
    return this.contains("@") && this.contains(".")
}

// Форматирование автора: "Толстой Л.Н."
fun formatAuthorName(fullName: String): String {
    val parts = fullName.split(" ").filter { it.isNotBlank() }
    return when (parts.size) {
        1 -> parts[0]  // только фамилия
        2 -> "${parts[0]} ${parts[1].first()}."  // фамилия и инициал
        3 -> "${parts[0]} ${parts[1].first()}.${parts[2].first()}."  // фамилия и два инициала
        else -> fullName
    }
}

// Применение скидки к цене книги
fun applyDiscount(price: Double, discountPercent: Double): Double {
    require(discountPercent in 0.0..100.0) { "Скидка должна быть от 0 до 100" }
    return price * (1 - discountPercent / 100)
}

//получение строки с фамилией инициалом и группой студента
fun Student.getStudent():String {
    return this.surname + " " + this.name.first() + ". (группа "+this.group+")"
}

//сведения о студенте по средней оценке
fun Student.getStatus(): String{
    return when (this.mark){
        5 -> "Отличник"
        4 -> "Хорошист"
        3 -> "Троечник"
        else -> ""
    }
}
