package com.example.finances.viewmodel
// Declares the package name, indicating the folder structure where this file resides.

import com.example.finances.api.CbrService
// Imports the CbrService class for interacting with an API.

import com.example.finances.model.Amount
// Imports the Amount model class, likely representing monetary values.

import com.example.finances.model.CbrResult
// Imports the CbrResult model class, likely representing the result of a currency exchange API response.

import com.example.finances.model.Transaction
// Imports the Transaction model class, likely representing financial transactions.

import com.example.finances.room.AppDatabase
// Imports the AppDatabase class, which provides access to the Room database.

import kotlinx.coroutines.flow.Flow
// Imports the Flow class from Kotlin Coroutines for handling asynchronous data streams.

import kotlinx.coroutines.flow.flow
// Imports the flow builder for creating Flow objects.

import java.util.Date
// Imports the Date class to work with dates.

class Repository(private val db: AppDatabase) {
// Defines a Repository class that handles data operations. It takes an AppDatabase instance as a dependency.

    fun getDaily(): Flow<CbrResult?> {
        // Defines a function to fetch daily data from an API, returning a Flow of CbrResult.
        return flow {
            emit(CbrService.getInstance().getDaily().body())
            // Emits the result of the API call, using CbrService to fetch daily data and extracting the response body.
        }
    }

    fun getTransactionForDate(date: Date): Flow<List<Transaction>> {
        // Defines a function to fetch transactions for a specific date, returning a Flow of a list of Transaction objects.
        return db.transactionDao.getTransactionForDate(date)
        // Uses the DAO to query transactions for the given date.
    }

    fun getTotalIncomeAmount(date: Date): Flow<Amount> {
        // Defines a function to calculate the total income amount for a specific date, returning a Flow of Amount.
        return db.transactionDao.getTotalIncomeAmount(date)
        // Uses the DAO to fetch the total income amount for the given date.
    }

    fun getTotalExpenseAmount(date: Date): Flow<Amount> {
        // Defines a function to calculate the total expense amount for a specific date, returning a Flow of Amount.
        return db.transactionDao.getTotalExpenseAmount(date)
        // Uses the DAO to fetch the total expense amount for the given date.
    }

    suspend fun upsert(transaction: Transaction) {
        // Defines a suspend function to insert or update a transaction in the database.
        db.transactionDao.upsert(transaction)
        // Calls the DAO's upsert method to perform the database operation.
    }

    suspend fun delete(transaction: Transaction) {
        // Defines a suspend function to delete a transaction from the database.
        db.transactionDao.delete(transaction)
        // Calls the DAO's delete method to remove the transaction from the database.
    }
}
