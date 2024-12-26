package com.example.finances

// Импортируем класс DecimalFormat для работы с форматированием чисел
import java.text.DecimalFormat

// Создаем объект Utils для хранения полезных методов
object Utils {

    // Функция для форматирования числовых значений в строку с валютой
    fun formatCurrency(double: Double): String {
        // Используем DecimalFormat для форматирования числа в строку с двумя знаками после запятой и добавлением символа рубля
        return DecimalFormat("0.00 ₽").format(double)
    }
}
