package com.mibanco.creditorapido.data.model.response

data class LoanResponse(
    val success: Boolean,
    val transactionId: String?,
    val message: String?
)