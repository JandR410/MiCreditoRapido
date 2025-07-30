package com.mibanco.creditorapido.domain.usecase

import com.mibanco.creditorapido.domain.model.ClientCreditLine
import com.mibanco.creditorapido.domain.repository.CreditRepository
import javax.inject.Inject

class GetClientCreditLineUseCase @Inject constructor(
    private val repository: CreditRepository
) {
    suspend operator fun invoke(clientId: String): Result<ClientCreditLine> {
        return repository.getClientCreditLine(clientId)
    }
}