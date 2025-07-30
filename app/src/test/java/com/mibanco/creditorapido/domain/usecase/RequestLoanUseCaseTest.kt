package com.mibanco.creditorapido.domain.usecase

import com.mibanco.creditorapido.data.model.request.LoanRequest
import com.mibanco.creditorapido.domain.model.LoanStatus
import com.mibanco.creditorapido.domain.repository.CreditRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RequestLoanUseCaseTest {

    @Mock
    private lateinit var mockCreditRepository: CreditRepository

    private lateinit var requestLoanUseCase: RequestLoanUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        requestLoanUseCase = RequestLoanUseCase(mockCreditRepository)
    }

    @Test
    fun `invoke should return success when repository requestLoan is successful`() = runTest {
        val testLoanRequest = LoanRequest(
            amount = 1000.0,
            term = 12,
            clientId = "client123",
            requestTime = System.currentTimeMillis()
        )
        val expectedLoanStatus =
            LoanStatus(success = true, message = "Loan approved", loanId = "loan_abc")

        whenever(mockCreditRepository.requestLoan(any()))
            .thenReturn(Result.success(expectedLoanStatus))

        val result = requestLoanUseCase.invoke(testLoanRequest)

        assert(result.isSuccess)
        assert(result.getOrNull() == expectedLoanStatus)

        verify(mockCreditRepository).requestLoan(testLoanRequest)
    }

    @Test
    fun `invoke should return failure when repository requestLoan fails`() = runTest {
        val testLoanRequest = LoanRequest(
            amount = 500.0,
            term = 6,
            clientId = "client456",
            requestTime = System.currentTimeMillis()
        )
        val expectedException = RuntimeException("Network error occurred")

        whenever(mockCreditRepository.requestLoan(any()))
            .thenReturn(Result.failure(expectedException))

        val result = requestLoanUseCase.invoke(testLoanRequest)

        assert(result.isFailure)
        assert(result.exceptionOrNull() == expectedException)

        verify(mockCreditRepository).requestLoan(testLoanRequest)
    }
}