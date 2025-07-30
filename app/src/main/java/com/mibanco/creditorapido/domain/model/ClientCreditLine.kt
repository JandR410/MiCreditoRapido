package com.mibanco.creditorapido.domain.model

data class ClientCreditLine(
    val clientId: String,
    val clientName: String,
    val preApprovedAmount: Double,
    val interestRate: Double,
    val minTerm: Int,
    val maxTerm: Int
)