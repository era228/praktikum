package com.example.finances.viewmodel

// Импортируем необходимые библиотеки для работы с ViewModel, потоками данных и корутинами
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finances.R
import com.example.finances.model.CbrResult
import com.example.finances.model.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Date

// Класс MainViewModel наследуется от ViewModel и используется для управления данными UI
class MainViewModel(private val repository: Repository) : ViewModel() {

    // Текущее выбранное время периода (по умолчанию - последняя неделя)
    val currentPeriod = MutableStateFlow(Period.LastWeek)

    // Хранит данные о валютах, полученных из API
    val valute = MutableStateFlow(HashMap<String, CbrResult.Valuta>())

    // Список транзакций, соответствующих текущему периоду
    val transactions = MutableStateFlow(listOf<Transaction>())

    // Общий доход за текущий период
    val totalIncome = MutableStateFlow(0.0)

    // Общие расходы за текущий период
    val totalExpense = MutableStateFlow(0.0)

    // Инициализируем данные при создании ViewModel
    init {
        // Устанавливаем период по умолчанию
        setPeriod(Period.LastWeek)

        // Загружаем данные о валютах в фоновом потоке
        viewModelScope.launch(Dispatchers.IO) {
            repository.getDaily().collect {
                // Обновляем данные о валютах
                valute.value = it?.valute ?: HashMap()
            }
        }
    }

    // Перечисление для выбора временного периода
    enum class Period {
        LastWeek, // Последняя неделя
        LastMonth, // Последний месяц
        AllTime; // Все время

        // Метод для получения ресурса строки, соответствующей периоду
        fun getDisplayName() = when (this) {
            LastWeek -> R.string.button_last_week
            LastMonth -> R.string.button_last_month
            AllTime -> R.string.button_all_time
        }
    }

    // Устанавливаем текущий период и обновляем данные
    fun setPeriod(period: Period) {
        // Обновляем текущий выбранный период
        currentPeriod.value = period

        // Загружаем данные для выбранного периода в фоновом потоке
        viewModelScope.launch(Dispatchers.IO) {
            val currentDate = Date().time // Текущая дата в миллисекундах
            val date = when (period) {
                Period.LastWeek -> Date(currentDate - 7 * 24 * 60 * 60 * 1000L) // Неделя назад
                Period.LastMonth -> Date(currentDate - 30 * 24 * 60 * 60 * 1000L) // Месяц назад
                Period.AllTime -> Date(0) // С самого начала времени
            }

            // Загружаем транзакции для выбранного периода
            launch {
                repository.getTransactionForDate(date).collect {
                    transactions.value = it
                }
            }

            // Загружаем общий доход для выбранного периода
            launch {
                repository.getTotalIncomeAmount(date).collect {
                    totalIncome.value = it.value
                }
            }

            // Загружаем общие расходы для выбранного периода
            launch {
                repository.getTotalExpenseAmount(date).collect {
                    totalExpense.value = it.value
                }
            }
        }
    }

    // Метод для добавления или обновления транзакции
    fun upsert(transaction: Transaction) {
        viewModelScope.launch {
            repository.upsert(transaction) // Вставляем или обновляем транзакцию в базе данных
        }
    }

    // Метод для удаления транзакции
    fun delete(transaction: Transaction) {
        viewModelScope.launch {
            repository.delete(transaction) // Удаляем транзакцию из базы данных
        }
    }
}
