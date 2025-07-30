package com.mibanco.creditorapido.data.repository

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import  androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.mibanco.creditorapido.data.local.dao.PendingLoanRequestDao
import com.mibanco.creditorapido.data.local.entity.PendingLoanRequestEntity
import com.mibanco.creditorapido.data.model.request.LoanRequest
import com.mibanco.creditorapido.data.remote.MyBenchApi
import com.mibanco.creditorapido.domain.model.ClientCreditLine
import com.mibanco.creditorapido.domain.model.LoanStatus
import com.mibanco.creditorapido.domain.repository.CreditRepository
import com.mibanco.creditorapido.worker.LoanRetryWorker
import com.mibanco.creditorapido.worker.NetworkMonitor
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CreditRepositoryImpl @Inject constructor(
    private val apiService: MyBenchApi,
    private val pendingLoanRequestDao: PendingLoanRequestDao,
    private val networkMonitor: NetworkMonitor,
    @ApplicationContext private val context: Context
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
        return if (networkMonitor.isOnline()) {
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
                saveLoanRequestLocally(loanRequest)
                enqueueLoanRetryWorker()
                Result.failure(
                    Exception("Error de red. Solicitud guardada para reintentar.")
                )
            }
        } else {
            saveLoanRequestLocally(loanRequest)
            enqueueLoanRetryWorker()
            Result.failure(
                Exception("No hay conexión a internet. Solicitud guardada para reintentar.")
            )
        }
    }

    private suspend fun saveLoanRequestLocally(loanRequest: LoanRequest) {
        val entity = PendingLoanRequestEntity(
            amount = loanRequest.amount,
            term = loanRequest.term,
            clientId = loanRequest.clientId,
            requestTime = loanRequest.requestTime
        )
        pendingLoanRequestDao.insertLoanRequest(entity)
    }

    override suspend fun getPendingLoanRequests(): List<LoanRequest> {
        return pendingLoanRequestDao.getAllLoanRequests().map { entity ->
            LoanRequest(
                amount = entity.amount,
                term = entity.term,
                clientId = entity.clientId,
                requestTime = entity.requestTime
            )
        }
    }

    override suspend fun deletePendingLoanRequest(loanRequest: LoanRequest) {
        pendingLoanRequestDao.deleteLoanRequestByDetails(loanRequest.clientId, loanRequest.requestTime)
    }

    private fun enqueueLoanRetryWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val retryWorkRequest = OneTimeWorkRequest.Builder(LoanRetryWorker::class.java)
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                5_000L,
                TimeUnit.MILLISECONDS
            )
            .addTag("loan_retry_work")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "loan_retry_unique_work",
            ExistingWorkPolicy.REPLACE,
            retryWorkRequest
        )
    }
}