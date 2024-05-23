package com.example.finances.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finances.R
import com.example.finances.model.CbrResult
import com.example.finances.model.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class MainViewModel(private val repository: Repository) : ViewModel() {

    val currentPeriod = MutableStateFlow(Period.LastWeek)
    val valute = MutableStateFlow(HashMap<String, CbrResult.Valuta>())
    val transactions = MutableStateFlow(listOf<Transaction>())
    val totalIncome = MutableStateFlow(0.0)
    val totalExpense = MutableStateFlow(0.0)

    init {
        setPeriod(Period.LastWeek)

        viewModelScope.launch(Dispatchers.IO) {
            repository.getDaily().collect {
                valute.value = it?.valute ?: HashMap()
            }
        }
    }

    enum class Period {
        LastWeek,
        LastMonth,
        AllTime;

        fun getDisplayName() = when (this) {
            LastWeek -> R.string.button_last_week
            LastMonth -> R.string.button_last_month
            AllTime -> R.string.button_all_time
        }
    }

    fun setPeriod(period: Period) {
        currentPeriod.value = period

        viewModelScope.launch(Dispatchers.IO) {
            val currentDate = Date().time
            val date = when (period) {
                Period.LastWeek -> Date(currentDate - 7 * 24 * 60 * 60 * 1000L)
                Period.LastMonth -> Date(currentDate - 30 * 24 * 60 * 60 * 1000L)
                Period.AllTime -> Date(0)
            }

            launch {
                repository.getTransactionForDate(date).collect {
                    transactions.value = it
                }
            }
            launch {
                repository.getTotalIncomeAmount(date).collect {
                    totalIncome.value = it.value
                }
            }
            launch {
                repository.getTotalExpenseAmount(date).collect {
                    totalExpense.value = it.value
                }
            }
        }
    }

    fun upsert(transaction: Transaction) {
        viewModelScope.launch {
            repository.upsert(transaction)
        }
    }

    fun delete(transaction: Transaction) {
        viewModelScope.launch {
            repository.delete(transaction)
        }
    }
}