package com.example.finances.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finances.R
import com.example.finances.model.Transaction
import com.example.finances.ui.components.AppButton
import com.example.finances.ui.components.Background
import com.example.finances.ui.theme.Purple1
import com.example.finances.ui.theme.ExpenseColor
import com.example.finances.ui.theme.IncomeColor
import com.example.finances.ui.theme.Purple2
import com.example.finances.viewmodel.MainViewModel
import java.util.Date
import kotlin.math.absoluteValue

@Composable
fun AddOrEditScreen(viewModel: MainViewModel, transaction: Transaction?, onFinished: () -> Unit) {
    Scaffold { paddingValues ->
        Background {

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                val income = rememberSaveable {
                    mutableStateOf((transaction?.amount ?: 0.0) > 0.0)
                }
                val name = rememberSaveable {
                    mutableStateOf(transaction?.name ?: "")
                }
                val amount = rememberSaveable {
                    mutableStateOf(transaction?.amount?.absoluteValue?.toString() ?: "")
                }
                val amountError = rememberSaveable {
                    mutableStateOf(false)
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    for (item in listOf(false, true)) {
                        AppButton(
                            text = if (item) R.string.income else R.string.expenses,
                            onClick = { income.value = item },
                            isActive = item == income.value,
                            textColor = if (item) IncomeColor else ExpenseColor
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = {
                        Text(
                            text = stringResource(R.string.label_name),
                            color = Color.Black
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFE4DAF5),
                        unfocusedContainerColor = Color(0xFFE4DAF5)
                    ),
                    singleLine = true
                )
                Spacer(Modifier.height(16.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = amount.value,
                    onValueChange = {
                        amount.value = it
                        amountError.value = false
                    },
                    label = {
                        Text(
                            text = stringResource(R.string.label_amount),
                            color = Color.Black
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFE4DAF5),
                        unfocusedContainerColor = Color(0xFFE4DAF5),
                        errorContainerColor = Color(0xFFE4DAF5),
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = amountError.value,
                    trailingIcon = {
                        if (!amountError.value) return@TextField
                        Icon(Icons.Default.Warning, "Error")
                    }
                )
                Spacer(Modifier.weight(1f))

                val incomeText = stringResource(
                    if (income.value) R.string.income else R.string.expenses,
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(Purple1),
                    onClick = {
                        val sign = if (income.value) 1 else -1
                        val value = amount.value.toDoubleOrNull()
                        if (value == null) {
                            amountError.value = true
                            return@Button
                        }
                        viewModel.upsert(
                            Transaction(
                                name.value.ifBlank { incomeText },
                                sign * value,
                                transaction?.date ?: Date(),
                                transaction?.id ?: 0
                            )
                        )
                        onFinished()
                    }
                ) {
                    val text = if (transaction == null) {
                        R.string.button_add
                    } else {
                        R.string.button_update
                    }
                    Text(
                        fontSize = 20.sp,
                        modifier = Modifier,
                        text = stringResource(text),
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(16.dp))

                if (transaction != null) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(Purple1),
                        onClick = {
                            viewModel.delete(transaction)
                            onFinished()
                        }
                    ) {
                        Text(
                            fontSize = 20.sp,
                            modifier = Modifier,
                            text = stringResource(R.string.button_delete),
                            color = Color.Red
                        )
                    }
                }
            }
        }
    }
}