package com.mibanco.creditorapido.data.model.response

data class ClientCreditLineResponse(
    val clientId: String,
    val clientName: String,
    val preApprovedAmount: Double,
    val interestRate: Double,
    val minTerm: Int,
    val maxTerm: Int
)