package com.mibanco.creditorapido.domain.usecase

import com.mibanco.creditorapido.data.model.request.LoanRequest
import com.mibanco.creditorapido.domain.model.LoanStatus
import com.mibanco.creditorapido.domain.repository.CreditRepository
import javax.inject.Inject

class RequestLoanUseCase @Inject constructor(
    private val repository: CreditRepository
) {
    suspend operator fun invoke(loanRequest: LoanRequest): Result<LoanStatus> {
        return repository.requestLoan(loanRequest)
    }
}