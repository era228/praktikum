package com.example.finances.ui.screens // Определение пакета для экрана приложения

import androidx.compose.foundation.ExperimentalFoundationApi // Импорт аннотации для использования экспериментальных API
import androidx.compose.foundation.background // Импорт функции для задания фона
import androidx.compose.foundation.clickable // Импорт функции для задания кликабельности
import androidx.compose.foundation.layout.Arrangement // Импорт для управления расположением элементов
import androidx.compose.foundation.layout.Box // Импорт для создания контейнера с наложением
import androidx.compose.foundation.layout.Column // Импорт для создания вертикального компоновщика
import androidx.compose.foundation.layout.Row // Импорт для создания горизонтального компоновщика
import androidx.compose.foundation.layout.Spacer // Импорт для создания пустого пространства
import androidx.compose.foundation.layout.fillMaxWidth // Импорт для заполнения доступной ширины
import androidx.compose.foundation.layout.height // Импорт для задания высоты элемента
import androidx.compose.foundation.layout.padding // Импорт для задания отступов
import androidx.compose.foundation.layout.size // Импорт для задания размера элемента
import androidx.compose.foundation.layout.width // Импорт для задания ширины элемента
import androidx.compose.foundation.lazy.LazyColumn // Импорт для создания ленивого вертикального списка
import androidx.compose.foundation.lazy.items // Импорт для отображения элементов в ленивом списке
import androidx.compose.foundation.shape.RoundedCornerShape // Импорт для задания скругленных углов
import androidx.compose.material.icons.Icons // Импорт иконок
import androidx.compose.material.icons.filled.Add // Импорт иконки "Добавить"
import androidx.compose.material3.Button // Импорт кнопки Material 3
import androidx.compose.material3.ButtonDefaults // Импорт для задания значений по умолчанию для кнопок
import androidx.compose.material3.FloatingActionButton // Импорт плавающей кнопки действия
import androidx.compose.material3.Icon // Импорт для отображения иконок
import androidx.compose.material3.MaterialTheme // Импорт для использования темы Material
import androidx.compose.material3.Scaffold // Импорт для создания структуры экрана
import androidx.compose.material3.Text // Импорт для отображения текста
import androidx.compose.runtime.Composable // Импорт аннотации для составных функций
import androidx.compose.runtime.collectAsState // Импорт для получения состояния из Flow
import androidx.compose.runtime.getValue // Импорт для получения значения из состояния
import androidx.compose.ui.Alignment // Импорт для выравнивания элементов
import androidx.compose.ui.Modifier // Импорт для модификации компонентов
import androidx.compose.ui.draw.clip // Импорт для обрезки элементов
import androidx.compose.ui.graphics.Color // Импорт для работы с цветами
import androidx.compose.ui.res.stringResource // Импорт для получения строковых ресурсов
import androidx.compose.ui.text.style.TextAlign // Импорт для выравнивания текста
import androidx.compose.ui.unit.dp // Импорт для работы с единицами измерения dp
import androidx.compose.ui.unit.sp // Импорт для работы с единицами измерения sp
import com.example.finances.R // Импорт ресурсов приложения
import com.example.finances.Utils // Импорт утилит
import com.example.finances.model.Transaction // Импорт модели Transaction
import com.example.finances.ui.components.AppButton // Импорт компонента кнопки приложения
import com.example.finances.ui.components.Background // Импорт компонента фона
import com.example.finances.ui.theme.Purple2 // Импорт цвета Purple2
import com.example.finances.ui.theme.Purple1 // Импорт цвета Purple1
import com.example.finances.ui.theme.ExpenseColor // Импорт цвета для расходов
import com.example.finances.ui.theme.IncomeColor // Импорт цвета для доходов
import com.example.finances.ui.theme.Purple3 // Импорт цвета Purple3
import com.example.finances.viewmodel.MainViewModel // Импорт ViewModel
import java.text.SimpleDateFormat // Импорт для форматирования даты
import kotlin.math.absoluteValue // Импорт для работы с абсолютным значением

@OptIn(ExperimentalFoundationApi::class) // Аннотация для использования экспериментальных API
@Composable
fun MainScreen(viewModel: MainViewModel, addOrEdit: (Transaction?) -> Unit) { // Функция для главного экрана, принимает ViewModel и функцию для добавления/редактирования транзакции

    val currentPeriod = viewModel.currentPeriod.collectAsState() // Получение текущего периода из ViewModel
    val transactions = viewModel.transactions.collectAsState() // Получение списка транзакций из ViewModel
    val totalIncome = viewModel.totalIncome.collectAsState() // Получение общей суммы доходов из ViewModel
    val totalExpense = viewModel.totalExpense.collectAsState() // Получение общей суммы расходов из ViewModel

    Scaffold( // Создание структуры экрана
        floatingActionButton = { // Определение плавающей кнопки действия
            FloatingActionButton(onClick = { addOrEdit(null) }) { // Обработка клика по кнопке
                Icon(Icons.Default.Add, stringResource(R.string.button_add)) // Отображение иконки "Добавить"
            }
        }
    ) { paddingValues -> // Получение значений отступов для Scaffold
        Background { // Использование компонента фона
            Column( // Вертикальная компоновка
                modifier = Modifier.padding(paddingValues), // Задание отступов
                horizontalAlignment = Alignment.CenterHorizontally // Выравнивание по горизонтали
            ) {
                Row( // Горизонтальная компоновка для выбора периода
                    modifier = Modifier.padding(16.dp), // Задание отступов
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // Расположение элементов с промежутками
                ) {
                    for (period in MainViewModel.Period.entries) { // Перебор доступных периодов
                        AppButton( // Использование кнопки приложения для выбора периода
                            text = period.getDisplayName(), // Получение отображаемого имени периода
                            onClick = { viewModel.setPeriod(period) }, // Обработка клика по кнопке
                            isActive = period == currentPeriod.value // Проверка, является ли период активным
                        )
                    }
                }

                Row { // Горизонтальная компоновка для отображения общей суммы доходов и расходов
                    TotalItem(true, totalIncome.value) // Отображение общей суммы доходов
                    TotalItem(false, totalExpense.value) // Отображение общей суммы расходов
                }

                Spacer(Modifier.height(8.dp)) // Пустое пространство между элементами

                Row { // Горизонтальная компоновка для отображения валют
                    val valute by viewModel.valute.collectAsState() // Получение валют из ViewModel
                    val valuteCodes = listOf("USD", "EUR", "GBP") // Список кодов валют
                    for (code in valuteCodes) { // Перебор кодов валют
                        valute[code]?.let { // Проверка наличия значения для каждой валюты
                            Text( // Отображение текста с информацией о валюте
                                fontSize = 16.sp, // Размер шрифта
                                modifier = Modifier
                                    .padding(4.dp) // Задание отступов
                                    .background( // Задание фона
                                        color = Purple2, // Цвет фона
                                        shape = RoundedCornerShape(8.dp) // Скругление углов
                                    )
                                    .padding(4.dp), // Внутренние отступы
                                text = "$code: ${Utils.formatCurrency(it.value)}", // Форматирование текста
                                color = Color.White // Цвет текста
                            )
                        }
                    }
                }

                LazyColumn( // Ленивый вертикальный список для отображения транзакций
                    modifier = Modifier
                        .fillMaxWidth() // Заполнение доступной ширины
                        .padding(16.dp), // Задание отступов
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Расположение элементов с промежутками
                ) {
                    items(transactions.value) { note -> // Отображение элементов списка транзакций
                        TransactionItem(note, addOrEdit) // Использование компонента для отображения транзакции
                    }

                    stickyHeader { // Закрепленный заголовок
                        Spacer(modifier = Modifier.height(64.dp)) // Пустое пространство для заголовка
                    }
                }
            }
        }
    }
}

@Composable
fun TotalItem( // Функция для отображения общей суммы (доходов или расходов)
    isIncome: Boolean, // Параметр для определения, является ли сумма доходом
    value: Double // Значение суммы
) {
    Column( // Вертикальная компоновка
        modifier = Modifier
            .padding(8.dp) // Задание отступов
            .background( // Задание фона
                color = Purple1, // Цвет фона
                shape = RoundedCornerShape(12.dp) // Скругление углов
            )
            .padding(16.dp), // Внутренние отступы
        horizontalAlignment = Alignment.CenterHorizontally // Выравнивание по горизонтали
    ) {
        val color = if (isIncome) IncomeColor else ExpenseColor // Определение цвета в зависимости от типа суммы
        Text( // Отображение текста с типом суммы
            fontSize = 24.sp, // Размер шрифта
            modifier = Modifier, // Модификатор
            text = stringResource( // Получение строки из ресурсов
                if (isIncome) R.string.income else R.string.expenses, // Проверка типа суммы
            ),
            color = color // Цвет текста
        )
        Text( // Отображение текста с суммой
            fontSize = 28.sp, // Размер шрифта
            modifier = Modifier, // Модификатор
            text = Utils.formatCurrency(value), // Форматирование суммы
            color = Color.White // Цвет текста
        )
    }

}

@Composable
fun TransactionItem( // Функция для отображения отдельной транзакции
    transaction: Transaction, // Транзакция
    edit: (Transaction?) -> Unit // Функция для редактирования транзакции
) {
    Row( // Горизонтальная компоновка для отображения данных транзакции
        modifier = Modifier
            .fillMaxWidth() // Заполнение доступной ширины
            .clip(RoundedCornerShape(8.dp)) // Скругление углов
            .background(color = Purple1) // Задание фона
            .clickable { // Обработка клика по элементу
                edit(transaction) // Вызов функции редактирования
            }
            .padding(16.dp) // Задание отступов
    ) {

        Row( // Горизонтальная компоновка для отображения информации о транзакции
            verticalAlignment = Alignment.CenterVertically // Выравнивание по вертикали
        ) {
            val color = if (transaction.amount > 0) IncomeColor else ExpenseColor // Определение цвета в зависимости от суммы
            Box( // Контейнер для отображения знака суммы
                modifier = Modifier
                    .size(48.dp) // Размер контейнера
                    .clip(RoundedCornerShape(8.dp)) // Скругление углов
                    .background(color = Purple2), // Задание фона
            ) {
                Text( // Отображение знака суммы
                    text = if (transaction.amount > 0) "+" else "−", // Определение знака
                    modifier = Modifier.align(Alignment.Center), // Выравнивание по центру
                    color = color, // Цвет текста
                    style = MaterialTheme.typography.titleMedium, // Стиль текста
                    fontSize = 32.sp, // Размер шрифта
                )
            }

            Column( // Вертикальная компоновка для информации о транзакции
                modifier = Modifier
                    .weight(1f) // Задание веса для растяжения
                    .padding(horizontal = 16.dp) // Задание горизонтальных отступов
            ) {
                Text( // Отображение имени транзакции
                    text = transaction.name, // Имя транзакции
                    style = MaterialTheme.typography.titleMedium, // Стиль текста
                    color = Color.White, // Цвет текста
                )

                val format = SimpleDateFormat.getDateInstance() // Создание форматтера даты
                Text(format.format(transaction.date)) // Отображение даты транзакции
            }

            Text( // Отображение суммы транзакции
                text = Utils.formatCurrency(transaction.amount.absoluteValue), // Форматирование суммы
                color = Color.White, // Цвет текста
                style = MaterialTheme.typography.titleMedium, // Стиль текста
                fontSize = 18.sp // Размер шрифта
            )
        }
    }
}
