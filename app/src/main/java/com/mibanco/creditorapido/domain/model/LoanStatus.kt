package com.mibanco.creditorapido.domain.model

data class LoanStatus(
    val success: Boolean,
    val message: String,
    val loanId: String? = null
)