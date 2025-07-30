package com.mibanco.creditorapido.data.repository

import com.mibanco.creditorapido.data.local.dao.PendingLoanRequestDao
import com.mibanco.creditorapido.data.model.request.LoanRequest
import com.mibanco.creditorapido.data.remote.MyBenchApi
import com.mibanco.creditorapido.domain.model.ClientCreditLine
import com.mibanco.creditorapido.domain.model.LoanStatus
import com.mibanco.creditorapido.domain.repository.CreditRepository
import com.mibanco.creditorapido.worker.NetworkMonitor
import javax.inject.Inject

class CreditRepositoryImpl @Inject constructor(
    private val apiService: MyBenchApi,
    private val pendingLoanRequestDao: PendingLoanRequestDao,
    private val networkMonitor: NetworkMonitor // Inyectamos el monitor de red
) : CreditRepository {

    override suspend fun getClientCreditLine(clientId: String): Result<ClientCreditLine> {
        return try {
            val response = apiService.getClientCreditLine(clientId)
            Result.success(
                ClientCreditLine(
                    clientId = response.clientId,
                    clientName = response.clientName,
                    preApprovedAmount = response.preApprovedAmount,
                    interestRate = response.interestRate,
                    minTerm = response.minTerm,
                    maxTerm = response.maxTerm
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun requestLoan(loanRequest: LoanRequest): Result<LoanStatus> {
        return if (networkMonitor.isOnline()) { // Verificar si hay conexión
            try {
                val response = apiService.sendLoanRequest(loanRequest)
                if (response.success) {
                    Result.success(
                        LoanStatus(
                            success = true,
                            message = response.message ?: "Solicitud enviada con éxito",
                            loanId = response.transactionId
                        )
                    )
                } else {
                    Result.failure(
                        Exception(response.message ?: "Error al procesar solicitud")
                    )
                }
            } catch (e: Exception) {
                // Si falla la llamada API a pesar de haber red (ej. timeout, error del servidor)
                // Guardar localmente para reintentar más tarde
                saveLoanRequestLocally(loanRequest)
                Result.failure(
                    Exception("Error de red. Solicitud guardada para reintentar.")
                )
            }
        } else {
            // No hay conexión, guardar la solicitud localmente
            saveLoanRequestLocally(loanRequest)
            Result.failure(
                Exception("No hay conexión a internet. Solicitud guardada para reintentar.")
            )
        }
    }

    private suspend fun saveLoanRequestLocally(loanRequest: LoanRequest) {
        /*val entity = PendingLoanRequestEntity(
            amount = loanRequest.amount,
            term = loanRequest.term,
            monthlyPayment = loanRequest.clientId,
            interestRate = loanRequest.requestTime
        )
        pendingLoanRequestDao.insertLoanRequest(entity)*/
    }

    override suspend fun getPendingLoanRequests(): List<LoanRequest> {
        return pendingLoanRequestDao.getAllPendingRequests().map { entity ->
            LoanRequest(
                amount = entity.amount,
                term = entity.term,
                clientId = entity.interestRate.toString(),
                requestTime = entity.timestamp
            )
        }
    }

    override suspend fun deletePendingLoanRequest(loanRequest: LoanRequest) {
        /*  val entity = PendingLoanRequestEntity(
              amount = loanRequest.amount,
              term = loanRequest.term,
              clientId = loanRequest.clientId,
              requestTime = loanRequest.requestTime
          )

          pendingLoanRequestDao.deleteLoanRequestByDetails(loanRequest.clientId, loanRequest.requestTime)
      */
    }
}