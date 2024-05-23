package com.example.finances.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Transaction(
    val name: String,
    val amount: Double,
    val date: Date,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)