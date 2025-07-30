package com.mibanco.creditorapido.data.local.entity

import androidx.room.Entity

@Entity(tableName = "pending_loan_requests", primaryKeys = ["clientId", "requestTime"])
data class PendingLoanRequestEntity(
    val amount: Double,
    val term: Int,
    val clientId: String,
    val requestTime: Long
)