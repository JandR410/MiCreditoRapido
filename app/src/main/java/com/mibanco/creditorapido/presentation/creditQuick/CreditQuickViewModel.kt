package com.mibanco.creditorapido.presentation.creditQuick

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mibanco.creditorapido.data.model.request.LoanRequest
import com.mibanco.creditorapido.domain.model.ClientCreditLine
import com.mibanco.creditorapido.domain.model.LoanSimulation
import com.mibanco.creditorapido.domain.model.LoanStatus
import com.mibanco.creditorapido.domain.usecase.GetClientCreditLineUseCase
import com.mibanco.creditorapido.domain.usecase.RequestLoanUseCase
import com.mibanco.creditorapido.domain.usecase.SimulateLoanUseCase
import com.mibanco.creditorapido.presentation.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreditQuickViewModel @Inject constructor(
    private val getClientCreditLineUseCase: GetClientCreditLineUseCase,
    private val simulateLoanUseCase: SimulateLoanUseCase,
    private val requestLoanUseCase: RequestLoanUseCase
) : ViewModel() {

    private val _clientCreditLine = MutableStateFlow<Resource<ClientCreditLine>>(Resource.Loading())
    val clientCreditLine: StateFlow<Resource<ClientCreditLine>> = _clientCreditLine.asStateFlow()

    private val _loanSimulation = MutableLiveData<LoanSimulation>()
    val loanSimulation: LiveData<LoanSimulation> = _loanSimulation

    private val _loanRequestStatus = MutableStateFlow<Resource<LoanStatus>>(Resource.Empty())
    val loanRequestStatus: StateFlow<Resource<LoanStatus>> = _loanRequestStatus.asStateFlow()

    private var currentAmount: Double = 0.0
    private var currentTerm: Int = 0
    private var currentInterestRate: Double = 0.0
    private var currentClientId: String = ""

    fun fetchClientCreditLine(clientId: String) {
        viewModelScope.launch {
            _clientCreditLine.value = Resource.Loading()
            try {
                val result = getClientCreditLineUseCase(clientId)
                result.fold(
                    onSuccess = { creditLine ->
                        _clientCreditLine.value = Resource.Success(creditLine)
                        currentAmount = creditLine.preApprovedAmount
                        currentTerm = creditLine.minTerm
                        currentInterestRate = creditLine.interestRate
                        currentClientId = creditLine.clientId

                        simulateLoan(currentAmount, currentTerm)
                    },
                    onFailure = { error ->
                        _clientCreditLine.value = Resource.Error(error.message ?: "Error desconocido")
                    }
                )
            } catch (e: Exception) {
                _clientCreditLine.value = Resource.Error(e.message ?: "Error de red/desconocido")
            }
        }
    }

    fun simulateLoan(amount: Double, term: Int) {
        val interestRateToUse = _clientCreditLine.value.data?.interestRate ?: 0.0
        val simulationResult = simulateLoanUseCase(amount, term, interestRateToUse)

        _loanSimulation.value = simulationResult
        currentAmount = amount
        currentTerm = term
    }

    fun requestLoan() {
        if (currentClientId.isEmpty() || currentAmount <= 0 || currentTerm <= 0) {
            _loanRequestStatus.value = Resource.Error("Datos de préstamo incompletos.")
            return
        }

        viewModelScope.launch {
            _loanRequestStatus.value = Resource.Loading()
            val loanRequest = LoanRequest(
                amount = currentAmount,
                term = currentTerm,
                clientId = currentClientId,
                requestTime = System.currentTimeMillis()
            )

            val result = requestLoanUseCase(loanRequest)
            result.fold(
                onSuccess = { loanStatus ->
                    _loanRequestStatus.value = Resource.Success(loanStatus)
                },
                onFailure = { error ->
                    _loanRequestStatus.value = Resource.Error(error.message ?: "Error al solicitar préstamo")
                }
            )
        }
    }

    fun resetLoanRequestStatus() {
        _loanRequestStatus.value = Resource.Empty()
    }
}