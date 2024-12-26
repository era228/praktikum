package com.example.finances

// Импортируем необходимые пакеты
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.finances.model.Transaction
import com.example.finances.room.AppDatabase
import com.example.finances.ui.screens.AddOrEditScreen
import com.example.finances.ui.screens.MainScreen
import com.example.finances.ui.theme.FinancesTheme
import com.example.finances.viewmodel.MainViewModel
import com.example.finances.viewmodel.Repository
import com.google.gson.Gson

// Класс MainActivity расширяет ComponentActivity
class MainActivity : ComponentActivity() {

    // Инициализируем объект базы данных с помощью библиотеки Room
    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, // Указываем класс базы данных
            "main.db" // Имя базы данных
        ).build()
    }

    // Создаем экземпляр ViewModel с использованием фабрики для внедрения зависимостей
    private val viewModel by viewModels<MainViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    // Возвращаем MainViewModel с репозиторием, основанным на базе данных
                    return MainViewModel(Repository(database)) as T
                }
            }
        }
    )

    // Переопределяем метод onCreate, который запускается при создании активности
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Устанавливаем UI для компонента с помощью Jetpack Compose
        setContent {
            FinancesTheme {
                // Создаем навигационный контроллер для работы с навигацией между экранами
                val controller = rememberNavController()

                // Настроим NavHost для навигации по экранам
                NavHost(
                    navController = controller, // Устанавливаем контроллер навигации
                    startDestination = "main" // Указываем начальный экран
                ) {
                    // Определяем маршрут для экрана MainScreen
                    composable("main") {
                        MainScreen(viewModel) { transaction: Transaction? ->
                            // При нажатии на элемент, преобразуем объект в JSON и переходим на экран редактирования
                            val json = Gson().toJson(transaction)
                            controller.navigate("addOrEdit/$json")
                        }
                    }

                    // Определяем маршрут для экрана AddOrEditScreen, где мы передаем транзакцию как JSON
                    composable("addOrEdit/{transaction}") {
                        AddOrEditScreen(
                            viewModel,
                            // Преобразуем строку JSON обратно в объект Transaction
                            Gson().fromJson(
                                it.arguments?.getString("transaction"),
                                Transaction::class.java
                            )
                        ) {
                            // После редактирования возвращаемся назад
                            controller.popBackStack()
                        }
                    }
                }
            }
        }
    }
}
