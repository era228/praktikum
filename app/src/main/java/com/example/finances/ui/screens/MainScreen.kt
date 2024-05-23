package com.example.finances.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finances.R
import com.example.finances.Utils
import com.example.finances.model.Transaction
import com.example.finances.ui.components.AppButton
import com.example.finances.ui.components.Background
import com.example.finances.ui.theme.Purple2
import com.example.finances.ui.theme.Purple1
import com.example.finances.ui.theme.ExpenseColor
import com.example.finances.ui.theme.IncomeColor
import com.example.finances.ui.theme.Purple3
import com.example.finances.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(viewModel: MainViewModel, addOrEdit: (Transaction?) -> Unit) {

    val currentPeriod = viewModel.currentPeriod.collectAsState()
    val transactions = viewModel.transactions.collectAsState()
    val totalIncome = viewModel.totalIncome.collectAsState()
    val totalExpense = viewModel.totalExpense.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { addOrEdit(null) }) {
                Icon(Icons.Default.Add, stringResource(R.string.button_add))
            }
        }
    ) { paddingValues ->
        Background {
            Column(
                modifier = Modifier.padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    for (period in MainViewModel.Period.entries) {
                        AppButton(
                            text = period.getDisplayName(),
                            onClick = { viewModel.setPeriod(period) },
                            isActive = period == currentPeriod.value
                        )
                    }
                }

                Row {
                    TotalItem(true, totalIncome.value)
                    TotalItem(false, totalExpense.value)
                }

                Spacer(Modifier.height(8.dp))

                Row {
                    val valute by viewModel.valute.collectAsState()
                    val valuteCodes = listOf("USD", "EUR", "GBP")
                    for (code in valuteCodes) {
                        valute[code]?.let {
                            Text(
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .background(
                                        color = Purple2,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(4.dp),
                                text = "$code: ${Utils.formatCurrency(it.value)}",
                                color = Color.White
                            )
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactions.value) { note ->
                        TransactionItem(note, addOrEdit)
                    }

                    stickyHeader {
                        Spacer(modifier = Modifier.height(64.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TotalItem(
    isIncome: Boolean,
    value: Double
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .background(
                color = Purple1,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val color = if (isIncome) IncomeColor else ExpenseColor
        Text(
            fontSize = 24.sp,
            modifier = Modifier,
            text = stringResource(
                if (isIncome) R.string.income else R.string.expenses,
            ),
            color = color
        )
        Text(
            fontSize = 28.sp,
            modifier = Modifier,
            text = Utils.formatCurrency(value),
            color = Color.White
        )
    }

}

@Composable
fun TransactionItem(
    transaction: Transaction, edit: (Transaction?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color = Purple1)
            .clickable {
                edit(transaction)
            }
            .padding(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            val color = if (transaction.amount > 0) IncomeColor else ExpenseColor
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = Purple2),
            ) {
                Text(
                    text = if (transaction.amount > 0) "+" else "−",
                    modifier = Modifier.align(Alignment.Center),
                    color = color,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 32.sp,
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = transaction.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                )

                val format = SimpleDateFormat.getDateInstance()
                Text(format.format(transaction.date))
            }

            Text(
                text = Utils.formatCurrency(transaction.amount.absoluteValue),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp
            )
        }
    }
}