package com.mibanco.creditorapido.data.model.request

data class LoanRequest(
    val amount: Double,
    val term: Int,
    val clientId: String,
    val requestTime: Long
)