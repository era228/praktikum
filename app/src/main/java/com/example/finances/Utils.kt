package com.example.finances

import java.text.DecimalFormat

object Utils {
    fun formatCurrency(double: Double): String {
        return DecimalFormat("0.00 ₽").format(double)
    }
}