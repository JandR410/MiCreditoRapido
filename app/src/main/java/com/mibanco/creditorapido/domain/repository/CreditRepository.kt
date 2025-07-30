package com.mibanco.creditorapido.domain.repository

import com.mibanco.creditorapido.data.model.request.LoanRequest
import com.mibanco.creditorapido.domain.model.ClientCreditLine
import com.mibanco.creditorapido.domain.model.LoanStatus

interface CreditRepository {
    suspend fun getClientCreditLine(clientId: String): Result<ClientCreditLine>
    suspend fun requestLoan(loanRequest: LoanRequest): Result<LoanStatus>
    suspend fun getPendingLoanRequests(): List<LoanRequest>
    suspend fun deletePendingLoanRequest(loanRequest: LoanRequest)
}