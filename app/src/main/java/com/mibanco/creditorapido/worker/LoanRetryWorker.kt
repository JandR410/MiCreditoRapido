package com.mibanco.creditorapido.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mibanco.creditorapido.domain.repository.CreditRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class LoanRetryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val creditRepository: CreditRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val pendingRequests = creditRepository.getPendingLoanRequests()

        if (pendingRequests.isEmpty()) {
            println("LoanRetryWorker: No hay solicitudes pendientes. Finalizando.")
            return@withContext Result.success()
        }

        var allRequestsSuccessful = true
        var retryNeeded = false

        for (request in pendingRequests) {
            val result = creditRepository.requestLoan(request)

            result.onSuccess { loanStatus ->
                if (loanStatus.success) {
                    creditRepository.deletePendingLoanRequest(request)
                    println("LoanRetryWorker: Solicitud de préstamo reintentada y eliminada para cliente: ${request.clientId}")
                } else {
                    allRequestsSuccessful = false
                    println("LoanRetryWorker: Solicitud de préstamo para cliente ${request.clientId} falló por lógica de negocio: ${loanStatus.message}")
                }
            }.onFailure { throwable ->
                allRequestsSuccessful = false
                retryNeeded = true
                System.err.println("LoanRetryWorker: Fallo al reintentar solicitud de préstamo para cliente ${request.clientId}: ${throwable.message}")
            }
        }

            if (allRequestsSuccessful) {
            Result.success()
        } else if (retryNeeded) {
            println("LoanRetryWorker: Algunas solicitudes fallaron y necesitan reintentarse más tarde.")
            Result.retry()
        } else {
            println("LoanRetryWorker: Todas las solicitudes pendientes se procesaron (algunas fallaron por lógica de negocio, no por red).")
            Result.success()
        }
    }
}