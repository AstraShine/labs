package com.example.myfirstapp.utils

import androidx.core.widget.TextViewCompat
import org.junit.Assert.*
import org.junit.Test

class StringUtilsTest {

    @Test
    fun emailValidation_correct() {
        assertTrue("test@example.com".isValidEmail())
        assertTrue("user.name@domain.co".isValidEmail())
    }

    @Test
    fun emailValidation_incorrect() {
        assertFalse("testexample.com".isValidEmail())
        assertFalse("test@example".isValidEmail())
        assertFalse("".isValidEmail())
    }

    @Test
    fun formatAuthorName_fullName() {
        assertEquals("Толстой Л.Н.", formatAuthorName("Толстой Лев Николаевич"))
        assertEquals("Пушкин А.С.", formatAuthorName("Пушкин Александр Сергеевич"))
    }

    @Test
    fun formatAuthorName_twoParts() {
        assertEquals("Толстой Л.", formatAuthorName("Толстой Лев"))
        assertEquals("Пушкин А.", formatAuthorName("Пушкин Александр"))
    }

    @Test
    fun formatAuthorName_onePart() {
        assertEquals("Толстой", formatAuthorName("Толстой"))
    }

    @Test
    fun applyDiscount_normal() {
        assertEquals(90.0, applyDiscount(100.0, 10.0), 0.001)
        assertEquals(75.0, applyDiscount(150.0, 50.0), 0.001)
    }

    @Test
    fun applyDiscount_zero() {
        assertEquals(100.0, applyDiscount(100.0, 0.0), 0.001)
    }

    @Test(expected = IllegalArgumentException::class)
    fun applyDiscount_invalidLow() {
        applyDiscount(100.0, -5.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun applyDiscount_invalidHigh() {
        applyDiscount(100.0, 110.0)
    }

    //тесты для нового класса Student
    @Test
    fun getStudent_correct(){
        val student = Student("Иван", "Иванов","ПИ-101", 5)
        assertEquals("Иванов И. (группа ПИ-101)", student.getStudent())
    }

    @Test
    fun getStatus_five(){
        val student = Student("Иван", "Иванов","ПИ-101", 5)
        assertEquals("Отличник", student.getStatus())
    }
    @Test
    fun getStatus_four(){
        val student = Student("Иван", "Иванов","ПИ-101", 4)
        assertEquals("Хорошист", student.getStatus())
    }
    @Test
    fun getStatus_three(){
        val student = Student("Иван", "Иванов","ПИ-101", 3)
        assertEquals("Троечник", student.getStatus())
    }
}