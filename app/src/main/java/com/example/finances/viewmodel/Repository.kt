package com.example.finances.viewmodel

import com.example.finances.api.CbrService
import com.example.finances.model.Amount
import com.example.finances.model.CbrResult
import com.example.finances.model.Transaction
import com.example.finances.room.AppDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date

class Repository(private val db: AppDatabase) {

    fun getDaily(): Flow<CbrResult?> {
        return flow {
            emit(CbrService.getInstance().getDaily().body())
        }
    }

    fun getTransactionForDate(date: Date): Flow<List<Transaction>> {
        return db.transactionDao.getTransactionForDate(date)
    }

    fun getTotalIncomeAmount(date: Date): Flow<Amount> {
        return db.transactionDao.getTotalIncomeAmount(date)
    }

    fun getTotalExpenseAmount(date: Date): Flow<Amount> {
        return db.transactionDao.getTotalExpenseAmount(date)
    }

    suspend fun upsert(transaction: Transaction) {
        db.transactionDao.upsert(transaction)
    }

    suspend fun delete(transaction: Transaction) {
        db.transactionDao.delete(transaction)
    }
}