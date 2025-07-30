package com.mibanco.creditorapido.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_loan_requests")
data class PendingLoanRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val term: Int,
    val interestRate: Double,
    val monthlyPayment: Double,
    val timestamp: Long
)