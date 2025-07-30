package com.mibanco.creditorapido.domain.model

data class LoanSimulation(
    val amount: Double,
    val term: Int,
    val interestRate: Double,
    val monthlyPayment: Double
)