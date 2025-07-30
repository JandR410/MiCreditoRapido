package com.mibanco.creditorapido.domain.usecase

import com.mibanco.creditorapido.domain.model.LoanSimulation
import javax.inject.Inject

class SimulateLoanUseCase @Inject constructor(

) {

    operator fun invoke(amount: Double, term: Int, annualInterestRate: Double): LoanSimulation {
        if (amount <= 0 || term <= 0 || annualInterestRate < 0) {
            return LoanSimulation(amount, term, annualInterestRate, 0.0)
        }

        val monthlyInterestRate = annualInterestRate / 12.0

        val totalInterest = amount * monthlyInterestRate * term
        val totalAmountToPay = amount + totalInterest
        val monthlyPayment = totalAmountToPay / term

        val roundedMonthlyPayment = String.format("%.2f", monthlyPayment).toDouble()

        return LoanSimulation(amount, term, annualInterestRate, roundedMonthlyPayment)
    }
}
