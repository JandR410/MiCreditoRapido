package com.mibanco.creditorapido.presentation.creditQuick

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mibanco.creditorapido.domain.usecase.GetClientCreditLineUseCase
import com.mibanco.creditorapido.domain.usecase.RequestLoanUseCase
import com.mibanco.creditorapido.domain.usecase.SimulateLoanUseCase

class CreditQuickViewModelFactory(
    private val getClientCreditLineUseCase: GetClientCreditLineUseCase,
    private val simulateLoanUseCase: SimulateLoanUseCase,
    private val requestLoanUseCase: RequestLoanUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreditQuickViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreditQuickViewModel(
                getClientCreditLineUseCase,
                simulateLoanUseCase,
                requestLoanUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}