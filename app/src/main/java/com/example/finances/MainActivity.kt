package com.example.finances

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

class MainActivity : ComponentActivity() {

    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "main.db"
        ).build()
    }

    private val viewModel by viewModels<MainViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return MainViewModel(Repository(database)) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinancesTheme {
                val controller = rememberNavController()

                NavHost(
                    navController = controller,
                    startDestination = "main"
                ) {
                    composable("main") {
                        MainScreen(viewModel) { transaction: Transaction? ->
                            val json = Gson().toJson(transaction)
                            controller.navigate("addOrEdit/$json")
                        }
                    }
                    composable("addOrEdit/{transaction}") {
                        AddOrEditScreen(
                            viewModel,
                            Gson().fromJson(
                                it.arguments?.getString("transaction"),
                                Transaction::class.java
                            )
                        ) {
                            controller.popBackStack()
                        }
                    }
                }
            }
        }
    }
}


